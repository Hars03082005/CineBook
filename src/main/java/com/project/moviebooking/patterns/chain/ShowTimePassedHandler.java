package com.project.moviebooking.patterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Pattern: Chain of Responsibility
 */
@Component
@Order(2)
public class ShowTimePassedHandler implements CancellationHandler {
    @Override
    public void handle(CancellationEvaluationContext context) {
        LocalDateTime showStart = LocalDateTime.of(context.getShow().getShowDate(), context.getShow().getShowTime());
        if (!showStart.isAfter(context.getNow())) {
            context.setCancellable(false);
            context.setRefundPercent(0.0);
            context.setReason("Cannot cancel after show has started.");
        }
    }
}
