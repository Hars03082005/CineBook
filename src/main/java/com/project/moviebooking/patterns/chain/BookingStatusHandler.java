package com.project.moviebooking.patterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pattern: Chain of Responsibility
 */
@Component
@Order(1)
public class BookingStatusHandler implements CancellationHandler {
    @Override
    public void handle(CancellationEvaluationContext context) {
        String status = context.getBooking().getStatus();
        if ("CANCELLED".equals(status)) {
            context.setCancellable(false);
            context.setReason("Booking already cancelled.");
        } else if ("EXPIRED".equals(status)) {
            context.setCancellable(false);
            context.setReason("Booking already expired.");
        }
    }
}
