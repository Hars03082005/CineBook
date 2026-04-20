package com.project.moviebooking.patterns.state;

import java.util.Map;
import java.util.Set;

/**
 * Pattern: State
 */
public class CancellationContextState {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "TicketBooked", Set.of("CancellationRequested"),
            "CancellationRequested", Set.of("CheckingPolicy"),
            "CheckingPolicy", Set.of("CancellationApproved", "CancellationRejected"),
            "CancellationApproved", Set.of("RefundProcessing"),
            "RefundProcessing", Set.of("RefundCompleted")
    );

    public String transition(String current, String next) {
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(next)) {
            throw new InvalidStateTransitionException("Invalid cancellation state transition: " + current + " -> " + next);
        }
        return next;
    }
}
