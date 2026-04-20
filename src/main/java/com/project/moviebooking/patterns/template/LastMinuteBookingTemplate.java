package com.project.moviebooking.patterns.template;

import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.service.BookingService;

/**
 * Pattern: Template Method
 */
public class LastMinuteBookingTemplate extends StandardBookingTemplate {
    public LastMinuteBookingTemplate(BookingService bookingService) {
        super(bookingService);
    }

    @Override
    protected void preProcess(BookingRequest request) {
        // Hook for last-minute constraints or dynamic pricing.
    }

    @Override
    protected void postProcess(Booking booking) {
        // Hook for reminder notification.
    }
}
