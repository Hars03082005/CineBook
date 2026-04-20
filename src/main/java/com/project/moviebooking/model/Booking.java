package com.project.moviebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Booking Model - represents a ticket reservation
 * SOLID: S - Single Responsibility - BookingService only handles bookings
 * GRASP: Creator - Booking creates Ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String userId;

    private String showId;

    private String movieId;

    private String theatreId;

    private List<String> seatNumbers;

    private int numberOfSeats;

    private double totalAmount;

    // CONFIRMED, CANCELLED, PENDING, EXPIRED
    private String status = "PENDING";

    // BrowsingMovies → MovieSelected → TheatreSelected → ShowSelected →
    // SeatSelection → CheckingSeats → SeatsReserved → PaymentPending
    private String bookingState = "PaymentPending";

    // PaymentInitiated → ProcessingPayment → PaymentSuccess / PaymentFailed
    private String paymentState = "PaymentInitiated";

    // TicketBooked → CancellationRequested → CheckingPolicy →
    // CancellationApproved / CancellationRejected → TicketCancelled →
    // RefundProcessing → RefundCompleted
    private String cancellationState = "TicketBooked";

    private double convenienceFee;

    // If payment is not completed in 10 minutes, booking expires and seats are released.
    private LocalDateTime holdExpiresAt;

    private String paymentId;

    private LocalDateTime bookingTime = LocalDateTime.now();

    /** GRASP Information Expert: Booking knows total charged price. */
    public double getTotalPrice() {
        return totalAmount;
    }

    /** GRASP Information Expert: Booking can evaluate full-refund eligibility from show start. */
    public boolean isEligibleForFullRefund(LocalDateTime showStart) {
        return Duration.between(LocalDateTime.now(), showStart).toMinutes() > 120;
    }
}
