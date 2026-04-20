package com.project.moviebooking.service.impl;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.model.*;
import com.project.moviebooking.patterns.*;
import com.project.moviebooking.patterns.builder.BookingBuilder;
import com.project.moviebooking.patterns.state.BookingContext;
import com.project.moviebooking.repository.*;
import com.project.moviebooking.service.BookingService;
import com.project.moviebooking.service.NotificationService;
import com.project.moviebooking.service.PricingService;
import com.project.moviebooking.service.RefundService;
import com.project.moviebooking.service.SeatService;
import com.project.moviebooking.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BookingServiceImpl — Atomic seat locking + TicketFactory + Observer
 * =====================================================================
 * SOLID: D — implements BookingService (interface), not imported by controllers
 * SOLID: S — ONLY booking lifecycle logic
 * GRASP: Creator — creates Ticket (has all booking data)
 * GRASP: Information Expert — validates seat availability (has seat data)
 * =====================================================================
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository  bookingRepository;
    private final ShowRepository     showRepository;
    private final SeatRepository     seatRepository;
    private final MovieRepository    movieRepository;
    private final TheatreRepository  theatreRepository;
    private final TicketRepository   ticketRepository;
    private final TicketFactory      ticketFactory;        // FACTORY Pattern
    private final NotificationService notificationService;  // OBSERVER facade
    private final UserProfileService userProfileService;
    private final PaymentRepository  paymentRepository;
    private final PricingService pricingService;
    private final RefundService refundService;
    private final SeatService seatService;

    private final BookingContext bookingContext = new BookingContext();

    // ─────────────────────────────────────────────────────────────
    // CREATE BOOKING — Atomic seat locking (all-or-nothing)
    // ─────────────────────────────────────────────────────────────
    @Override
    public Booking createBooking(BookingRequest request, String userId) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found: " + request.getShowId()));

        if (!show.isActive())
            throw new RuntimeException("This show is no longer available.");

        LocalDateTime showStart = LocalDateTime.of(show.getShowDate(), show.getShowTime());
        if (!showStart.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book seats for a show that has already started/passed.");
        }

        List<String> requested = request.getSeatNumbers();
        if (requested == null || requested.isEmpty())
            throw new RuntimeException("Please select at least one seat.");
        if (requested.size() > 8)
            throw new RuntimeException("Cannot book more than 8 seats at once.");

        // Prevent duplicate booking attempts by same user for same show + same seats.
        Set<String> requestedSet = requested.stream().collect(Collectors.toSet());
        List<Booking> existingForUser = bookingRepository.findByUserIdAndShowId(userId, show.getId());
        boolean duplicate = existingForUser.stream()
                .filter(b -> !"CANCELLED".equals(b.getStatus()) && !"EXPIRED".equals(b.getStatus()))
                .anyMatch(b -> b.getSeatNumbers() != null && b.getSeatNumbers().stream().anyMatch(requestedSet::contains));
        if (duplicate) {
            throw new RuntimeException("Duplicate booking prevented: some selected seats are already reserved by you for this show.");
        }

        if (!seatService.checkAvailability(show.getId(), requested)) {
            throw new RuntimeException("Some selected seats are no longer available.");
        }

        // ── PHASE 1: Validate ALL seats ──
        List<Seat> seatsToBook = new ArrayList<>();
        double totalAmount = 0;

        for (String seatNum : requested) {
            Seat seat = seatRepository
                    .findByShowIdAndSeatNumber(show.getId(), seatNum)
                    .orElseThrow(() -> new RuntimeException(
                            "Seat " + seatNum + " not found."));
            if (seat.isBooked())
                throw new RuntimeException(
                        "Seat " + seatNum + " is already booked. Choose another.");
            seatsToBook.add(seat);
            totalAmount += seat.getPrice();
        }

        // ── PHASE 2: Lock ALL seats atomically ──
        for (Seat seat : seatsToBook) {
            seat.setBooked(true);
            seat.setBookedByUserId(userId);
        }
        seatRepository.saveAll(seatsToBook);
        System.out.println("🔒 [BOOKING] Locked " + seatsToBook.size() + " seats for user: " + userId);

        // ── PHASE 3: Update show counters ──
        show.getBookedSeats().addAll(requested);
        show.setAvailableSeats(Math.max(0, show.getAvailableSeats() - requested.size()));
        showRepository.save(show);

        // ── PHASE 4: Create PENDING booking ──
        double convenienceFee = pricingService.calculateConvenienceFee(requested.size());
        Booking booking = new BookingBuilder()
            .setUser(userId)
            .setShow(show.getId())
            .setMovie(show.getMovieId())
            .setTheatre(show.getTheatreId())
            .setSeats(requested)
            .setConvenienceFee(convenienceFee)
            .setTotalAmount(pricingService.calculateTotal(totalAmount, requested.size()))
            .setPaymentTimeoutMinutes(10)
            .build();

        Booking saved = bookingRepository.save(booking);
        System.out.println("✅ [BOOKING] Pending booking created: " + saved.getId());
        return saved;
    }

    // ─────────────────────────────────────────────────────────────
    // CONFIRM BOOKING — called after payment success
    // ─────────────────────────────────────────────────────────────
    @Override
    public Ticket confirmBookingAndIssueTicket(String bookingId, String paymentId, String userEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus(bookingContext.transition(booking.getStatus(), "CONFIRMED"));
        booking.setBookingState("PaymentPending");
        booking.setPaymentState("PaymentSuccess");
        booking.setCancellationState("TicketBooked");
        booking.setPaymentId(paymentId);
        bookingRepository.save(booking);

        Show    show    = showRepository.findById(booking.getShowId()).orElseThrow();
        Movie   movie   = movieRepository.findById(booking.getMovieId()).orElseThrow();
        Theatre theatre = theatreRepository.findById(booking.getTheatreId()).orElseThrow();

        String userName = "Guest";
        try { userName = userProfileService.getUserById(booking.getUserId()).getName(); }
        catch (Exception ignored) {}

        // ── FACTORY PATTERN: create correct ticket type ──
        AbstractTicket abstractTicket = ticketFactory.createTicket(
                booking.getSeatNumbers(), booking,
                movie.getTitle(),
                theatre.getName() + " — " + theatre.getCity(),
                show.getShowDate().toString(),
                show.getShowTime().toString(),
                userName
        );

        Ticket ticket = new Ticket();
        ticket.setBookingId(booking.getId());
        ticket.setUserId(booking.getUserId());
        ticket.setUserName(abstractTicket.getUserName());
        ticket.setMovieTitle(abstractTicket.getMovieTitle());
        ticket.setTheatreName(abstractTicket.getTheatreName());
        ticket.setShowDate(abstractTicket.getShowDate());
        ticket.setShowTime(abstractTicket.getShowTime());
        ticket.setSeatNumbers(abstractTicket.getSeatNumbers());
        ticket.setTotalAmount(abstractTicket.getTotalAmount());
        ticket.setTicketCode(abstractTicket.getTicketCode());
        ticket.setQrCode("CINEBOOK|" + booking.getId() + "|" + abstractTicket.getTicketCode());
        ticket.setStatus("VALID");
        ticket.setIssuedAt(abstractTicket.getIssuedAt());

        Ticket savedTicket = ticketRepository.save(ticket);

        // ── OBSERVER PATTERN: fire email + SMS events ──
        notificationService.sendConfirmation(
                booking.getId(), booking.getUserId(), userEmail,
                movie.getTitle(), savedTicket.getTicketCode(),
                booking.getTotalAmount(),
                String.join(", ", booking.getSeatNumbers())
        );

        return savedTicket;
    }

    // ─────────────────────────────────────────────────────────────
    // CANCEL BOOKING
    // ─────────────────────────────────────────────────────────────
    @Override
    public Booking cancelBooking(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUserId().equals(userId))
            throw new RuntimeException("Unauthorized to cancel this booking.");
        if ("CANCELLED".equals(booking.getStatus()))
            throw new RuntimeException("Booking already cancelled.");
        if ("EXPIRED".equals(booking.getStatus()))
            throw new RuntimeException("Booking already expired.");

        booking.setCancellationState("CancellationRequested");
        CancellationPreview preview = getCancellationPreview(bookingId, userId);
        booking.setCancellationState("CheckingPolicy");
        if (!preview.isCancellable()) {
            booking.setCancellationState("CancellationRejected");
            bookingRepository.save(booking);
            throw new RuntimeException(preview.getReason());
        }

        booking.setCancellationState("CancellationApproved");

        releaseSeatsForBooking(booking);

        // Cancel ticket
        ticketRepository.findByBookingId(bookingId).ifPresent(t -> {
            t.setStatus("CANCELLED");
            ticketRepository.save(t);
        });

        paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            booking.setCancellationState("RefundProcessing");
            double refundAmount = preview.getRefundAmount();
            payment.setRefundAmount(refundAmount);
            payment.setRefundStatus("PROCESSED");
            payment.setRefundReason(preview.getReason());
            payment.setStatus("REFUNDED");
            paymentRepository.save(payment);
        });

        booking.setStatus(bookingContext.transition(booking.getStatus(), "CANCELLED"));
        booking.setCancellationState("RefundCompleted");
        booking.setPaymentState("PaymentSuccess");
        return bookingRepository.save(booking);
    }

    @Override
    public CancellationPreview getCancellationPreview(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to view cancellation policy for this booking.");
        }

        Show show = showRepository.findById(booking.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found for this booking."));

        return refundService.evaluate(booking, show);
    }

    private void releaseSeatsForBooking(Booking booking) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumberIn(
                booking.getShowId(), booking.getSeatNumbers());
        seats.forEach(s -> { s.setBooked(false); s.setBookedByUserId(null); });
        seatRepository.saveAll(seats);

        showRepository.findById(booking.getShowId()).ifPresent(show -> {
            show.getBookedSeats().removeAll(booking.getSeatNumbers());
            show.setAvailableSeats(show.getAvailableSeats() + booking.getSeatNumbers().size());
            showRepository.save(show);
        });
    }

    @Override public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    @Override public List<Booking> getBookingsByUser(String userId) { return bookingRepository.findByUserId(userId); }
    @Override public Ticket getTicketByBookingId(String bookingId) {
        return ticketRepository.findByBookingId(bookingId).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }
    @Override public Ticket getTicketById(String ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }
    @Override public List<Ticket> getTicketsByUser(String userId) { return ticketRepository.findByUserId(userId); }
    @Override public List<Booking> getAllBookings() { return bookingRepository.findAll(); }
}
