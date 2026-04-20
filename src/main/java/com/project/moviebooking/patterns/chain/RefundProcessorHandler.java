package com.project.moviebooking.patterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pattern: Chain of Responsibility
 */
@Component
@Order(4)
public class RefundProcessorHandler implements CancellationHandler {
    @Override
    public void handle(CancellationEvaluationContext context) {
        if (!context.isCancellable()) {
            context.setRefundPercent(0.0);
        }
    }
}
