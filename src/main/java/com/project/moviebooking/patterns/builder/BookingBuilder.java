package com.project.moviebooking.patterns.builder;

import com.project.moviebooking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SOLID: S (only builds Booking objects)
 * GRASP: Creator (builder owns construction details)
 * Pattern: Builder
 */
public class BookingBuilder {
    private final Booking booking = new Booking();

    public BookingBuilder setUser(String userId) {
        booking.setUserId(userId);
        return this;
    }

    public BookingBuilder setShow(String showId) {
        booking.setShowId(showId);
        return this;
    }

    public BookingBuilder setMovie(String movieId) {
        booking.setMovieId(movieId);
        return this;
    }

    public BookingBuilder setTheatre(String theatreId) {
        booking.setTheatreId(theatreId);
        return this;
    }

    public BookingBuilder setSeats(List<String> seatNumbers) {
        booking.setSeatNumbers(seatNumbers);
        booking.setNumberOfSeats(seatNumbers == null ? 0 : seatNumbers.size());
        return this;
    }

    public BookingBuilder setTotalAmount(double totalAmount) {
        booking.setTotalAmount(totalAmount);
        return this;
    }

    public BookingBuilder setConvenienceFee(double convenienceFee) {
        booking.setConvenienceFee(convenienceFee);
        return this;
    }

    public BookingBuilder setPaymentTimeoutMinutes(int minutes) {
        booking.setHoldExpiresAt(LocalDateTime.now().plusMinutes(minutes));
        return this;
    }

    public Booking build() {
        if (booking.getUserId() == null || booking.getUserId().isBlank()) {
            throw new IllegalStateException("User is required for booking build");
        }
        if (booking.getShowId() == null || booking.getShowId().isBlank()) {
            throw new IllegalStateException("Show is required for booking build");
        }
        if (booking.getSeatNumbers() == null || booking.getSeatNumbers().isEmpty()) {
            throw new IllegalStateException("At least one seat is required for booking build");
        }
        booking.setStatus("PENDING");
        booking.setBookingState("SeatsReserved");
        booking.setPaymentState("PaymentInitiated");
        booking.setCancellationState("TicketBooked");
        booking.setBookingTime(LocalDateTime.now());
        return booking;
    }
}
