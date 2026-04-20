package com.project.moviebooking.patterns.template;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.service.BookingService;

/**
 * Pattern: Template Method
 */
public class StandardBookingTemplate extends BookingTemplate {
    private final BookingService bookingService;

    public StandardBookingTemplate(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    protected void validateRequest(BookingRequest request, String userId) {
        if (request == null || request.getShowId() == null || request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new RuntimeException("Invalid booking request");
        }
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Invalid user");
        }
    }

    @Override
    protected void preProcess(BookingRequest request) {
        // Standard flow has no pre-enrichment.
    }

    @Override
    protected Booking executeBooking(BookingRequest request, String userId) {
        return bookingService.createBooking(request, userId);
    }

    @Override
    protected void postProcess(Booking booking) {
        // Standard flow has no post hook.
    }
}
