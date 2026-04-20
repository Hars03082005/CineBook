package com.project.moviebooking.patterns.template;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.service.BookingService;

/**
 * Pattern: Template Method
 */
public class GroupBookingTemplate extends StandardBookingTemplate {
    public GroupBookingTemplate(BookingService bookingService) {
        super(bookingService);
    }

    @Override
    protected void validateRequest(BookingRequest request, String userId) {
        super.validateRequest(request, userId);
        if (request.getSeatNumbers().size() < 4) {
            throw new RuntimeException("Group booking requires at least 4 seats");
        }
    }

    @Override
    protected void postProcess(Booking booking) {
        // Placeholder for group perks.
    }
}
