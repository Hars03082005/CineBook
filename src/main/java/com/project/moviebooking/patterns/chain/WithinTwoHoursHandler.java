package com.project.moviebooking.patterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pattern: Chain of Responsibility
 */
@Component
@Order(3)
public class WithinTwoHoursHandler implements CancellationHandler {
    @Override
    public void handle(CancellationEvaluationContext context) {
        if (!context.isCancellable()) {
            return;
        }
        long minutes = context.minutesBeforeShow();
        if (minutes > 120) {
            context.setRefundPercent(50.0);
            context.setReason("Cancellation requested more than 2 hours before show. 50% refund applicable.");
        } else {
            context.setRefundPercent(0.0);
            context.setReason("Cancellation requested within 2 hours of show. No refund applicable.");
        }
    }
}
