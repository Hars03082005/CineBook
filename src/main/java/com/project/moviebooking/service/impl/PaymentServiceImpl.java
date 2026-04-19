package com.project.moviebooking.service.impl;

import com.project.moviebooking.dto.PaymentRequest;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Payment;
import com.project.moviebooking.model.Ticket;
import com.project.moviebooking.patterns.PaymentContext;
import com.project.moviebooking.repository.BookingRepository;
import com.project.moviebooking.repository.PaymentRepository;
import com.project.moviebooking.repository.SeatRepository;
import com.project.moviebooking.repository.ShowRepository;
import com.project.moviebooking.repository.UserRepository;
import com.project.moviebooking.service.BookingService;
import com.project.moviebooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PaymentServiceImpl — Handles payment processing
 * =================================================
 * SOLID: S — ONLY payment logic
 * SOLID: D — depends on BookingService (interface), PaymentContext (abstraction)
 *
 * DESIGN PATTERNS USED:
 *   Strategy → PaymentContext selects UPI/Card/Wallet at runtime
 *   Adapter  → PaymentGatewayAdapter wraps ExternalPaymentAPI (inside PaymentContext)
 * =================================================
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository    seatRepository;
    private final ShowRepository    showRepository;
    private final UserRepository    userRepository;

    // SOLID: D — interface injected
    private final BookingService    bookingService;
    private final PaymentContext    paymentContext;   // STRATEGY pattern

    /**
     * Full payment flow:
     * 1. Validate booking
     * 2. Execute payment via Strategy (UPI/Card/Wallet) + Adapter (ExternalAPI)
     * 3. On success → confirm booking + issue ticket (FactoryPattern inside BookingService)
     * 4. Save payment record to MongoDB
     */
    @Override
    public Payment processPayment(PaymentRequest request, String userId) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException(
                        "Booking not found: " + request.getBookingId()));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to pay for this booking.");
        }
        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already paid.");
        }
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot pay for a cancelled booking.");
        }
        if (booking.getHoldExpiresAt() != null && LocalDateTime.now().isAfter(booking.getHoldExpiresAt())) {
            releaseSeatsForExpiredHold(booking);
            booking.setStatus("EXPIRED");
            booking.setPaymentState("PaymentFailed");
            bookingRepository.save(booking);
            throw new RuntimeException("Payment timeout: seat hold exceeded 10 minutes. Please rebook.");
        }

        booking.setPaymentState("ProcessingPayment");
        bookingRepository.save(booking);

        // ── Determine payment details ──
        String detail = request.getUpiId() != null        ? request.getUpiId()
                      : request.getCardNumber() != null   ? request.getCardNumber()
                      : request.getBankName() != null     ? request.getBankName()
                      : "demo@cinebook";

        // ── STRATEGY PATTERN: PaymentContext selects correct algorithm ──
        // ── ADAPTER PATTERN: runs through PaymentGatewayAdapter to ExternalPaymentAPI ──
        String transactionId = paymentContext.executePayment(
                request.getPaymentMethod(), userId, booking.getTotalAmount(), detail);

        // ── Build payment record ──
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setUserId(userId);
        payment.setBaseAmount(booking.getTotalAmount());
        payment.setTotalAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentTime(LocalDateTime.now());

        if (transactionId != null) {
            payment.setStatus("SUCCESS");
            payment.setTransactionId(transactionId);
            Payment saved = paymentRepository.save(payment);

            booking.setPaymentState("PaymentSuccess");
            bookingRepository.save(booking);

            // Get user email for observer notification
            String userEmail = userRepository.findById(userId)
                    .map(u -> u.getEmail()).orElse("customer@cinebook.com");

            // ── Confirm booking + issue ticket via FACTORY PATTERN ──
            Ticket ticket = bookingService.confirmBookingAndIssueTicket(
                    booking.getId(), saved.getId(), userEmail);
            System.out.println("🎟️ [PAYMENT] Success. Ticket: " + ticket.getTicketCode());

            return saved;
        } else {
            payment.setStatus("FAILED");
            payment.setTransactionId("FAILED_" + System.currentTimeMillis());
            booking.setPaymentState("PaymentFailed");
            bookingRepository.save(booking);
            return paymentRepository.save(payment);
        }
    }

    @Override
    public Payment getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for booking: " + bookingId));
    }

    @Override
    public List<Payment> getPaymentsByUser(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public Payment processRefund(String bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "No payment for booking: " + bookingId));
        if ("SUCCESS".equals(payment.getStatus())) {
            payment.setStatus("REFUNDED");
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Only successful payments can be refunded.");
    }

    private void releaseSeatsForExpiredHold(Booking booking) {
        seatRepository.findByShowIdAndSeatNumberIn(booking.getShowId(), booking.getSeatNumbers())
                .forEach(seat -> {
                    seat.setBooked(false);
                    seat.setBookedByUserId(null);
                    seatRepository.save(seat);
                });

        showRepository.findById(booking.getShowId()).ifPresent(show -> {
            show.getBookedSeats().removeAll(booking.getSeatNumbers());
            show.setAvailableSeats(show.getAvailableSeats() + booking.getSeatNumbers().size());
            showRepository.save(show);
        });
    }
}
