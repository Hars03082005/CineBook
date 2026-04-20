package com.project.moviebooking.service;

import com.project.moviebooking.contracts.IBookable;
import com.project.moviebooking.contracts.ICancellable;
import com.project.moviebooking.contracts.IPayable;
import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.dto.PaymentRequest;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Payment;
import com.project.moviebooking.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * SOLID: D (depends on abstractions/services), S (workflow orchestration only)
 * GRASP: Indirection + Low Coupling
 * Pattern: Facade
 */
@Component
@RequiredArgsConstructor
public class BookingFacade implements IBookable, ICancellable, IPayable {
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final TicketService ticketService;

    @Override
    public Booking initiateBooking(BookingRequest request, String userId) {
        return bookingService.createBooking(request, userId);
    }

    @Override
    public Booking confirmBooking(String bookingId, String paymentId, String userEmail) {
        bookingService.confirmBookingAndIssueTicket(bookingId, paymentId, userEmail);
        return bookingService.getBookingById(bookingId);
    }

    @Override
    public Booking cancelBooking(String bookingId, String userId) {
        return bookingService.cancelBooking(bookingId, userId);
    }

    @Override
    public CancellationPreview getRefundAmount(String bookingId, String userId) {
        return bookingService.getCancellationPreview(bookingId, userId);
    }

    @Override
    public Payment processPayment(PaymentRequest request, String userId) {
        return paymentService.processPayment(request, userId);
    }

    @Override
    public String getPaymentStatus(String bookingId) {
        return paymentService.getPaymentByBookingId(bookingId).getStatus();
    }

    public Ticket downloadTicket(String bookingId) {
        return ticketService.getByBookingId(bookingId);
    }

    public Booking getBookingById(String bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    public java.util.List<Booking> getBookingsByUser(String userId) {
        return bookingService.getBookingsByUser(userId);
    }

    public java.util.List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
