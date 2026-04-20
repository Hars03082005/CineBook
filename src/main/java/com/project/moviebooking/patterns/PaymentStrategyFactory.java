package com.project.moviebooking.patterns;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SOLID: O (add strategy class without modifying callers)
 * GRASP: Indirection for runtime strategy resolution
 * Pattern: Factory Method
 */
@Component
public class PaymentStrategyFactory {
    private final List<PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(String method) {
        if (method == null) {
            return getDefaultStrategy();
        }
        String norm = method.toUpperCase().replace("_", "").replace(" ", "");
        return strategies.stream()
                .filter(s -> norm.contains(s.getMethodName().replace("_", "")))
                .findFirst()
                .orElse(getDefaultStrategy());
    }

    private PaymentStrategy getDefaultStrategy() {
        return strategies.stream()
                .filter(s -> "UPI".equals(s.getMethodName()))
                .findFirst()
                .orElse(strategies.get(0));
    }
}
