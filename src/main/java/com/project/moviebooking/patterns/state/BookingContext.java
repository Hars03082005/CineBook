package com.project.moviebooking.patterns.state;

import java.util.Map;
import java.util.Set;

/**
 * SOLID: S (booking state transition rules only)
 * GRASP: Protected Variations for lifecycle changes
 * Pattern: State
 */
public class BookingContext {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "PENDING", Set.of("CONFIRMED", "CANCELLED", "EXPIRED"),
            "CONFIRMED", Set.of("CANCELLED"),
            "CANCELLED", Set.of(),
            "EXPIRED", Set.of()
    );

    public String transition(String current, String next) {
        if (current == null || next == null) {
            throw new InvalidStateTransitionException("State transition cannot be null");
        }
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(next)) {
            throw new InvalidStateTransitionException("Invalid booking state transition: " + current + " -> " + next);
        }
        return next;
    }
}
