package com.project.moviebooking.patterns.state;

import java.util.Map;
import java.util.Set;

/**
 * Pattern: State
 */
public class PaymentContextState {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "PaymentInitiated", Set.of("ProcessingPayment", "PaymentFailed"),
            "ProcessingPayment", Set.of("PaymentSuccess", "PaymentFailed"),
            "PaymentSuccess", Set.of(),
            "PaymentFailed", Set.of()
    );

    public String transition(String current, String next) {
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(next)) {
            throw new InvalidStateTransitionException("Invalid payment state transition: " + current + " -> " + next);
        }
        return next;
    }
}
