package com.project.moviebooking.contracts;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.model.Booking;

/**
 * SOLID: I (fine-grained booking contract)
 * GRASP: Controller uses this to delegate booking actions.
 */
public interface IBookable {
    Booking initiateBooking(BookingRequest request, String userId);
    Booking confirmBooking(String bookingId, String paymentId, String userEmail);
}
