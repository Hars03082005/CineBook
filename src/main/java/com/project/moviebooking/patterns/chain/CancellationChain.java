package com.project.moviebooking.patterns.chain;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * SOLID: O (new handlers can be added without changing chain)
 * GRASP: Protected Variations for cancellation policy changes
 * Pattern: Chain of Responsibility
 */
@Component
public class CancellationChain {
    private final List<CancellationHandler> handlers;

    public CancellationChain(List<CancellationHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparingInt(h -> {
                    org.springframework.core.annotation.Order o = h.getClass().getAnnotation(org.springframework.core.annotation.Order.class);
                    return o == null ? Integer.MAX_VALUE : o.value();
                }))
                .toList();
    }

    public CancellationEvaluationContext evaluate(CancellationEvaluationContext context) {
        for (CancellationHandler handler : handlers) {
            handler.handle(context);
        }
        return context;
    }
}
