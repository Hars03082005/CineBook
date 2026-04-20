package com.project.moviebooking.patterns.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pattern: Chain of Responsibility
 */
@Component
@Order(5)
public class NotificationHandler implements CancellationHandler {
    @Override
    public void handle(CancellationEvaluationContext context) {
        // Notification is dispatched later by service layer; chain keeps reasoning trace only.
    }
}
