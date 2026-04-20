package com.project.moviebooking.patterns.template;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.model.Booking;

/**
 * Pattern: Template Method
 */
public abstract class BookingTemplate {
    public final Booking bookTicket(BookingRequest request, String userId) {
        validateRequest(request, userId);
        preProcess(request);
        Booking booking = executeBooking(request, userId);
        postProcess(booking);
        return booking;
    }

    protected abstract void validateRequest(BookingRequest request, String userId);
    protected abstract void preProcess(BookingRequest request);
    protected abstract Booking executeBooking(BookingRequest request, String userId);
    protected abstract void postProcess(Booking booking);
}
