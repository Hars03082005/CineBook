package com.project.moviebooking.patterns.state;

import java.util.Map;
import java.util.Set;

/**
 * Pattern: State
 */
public class UserAuthContextState {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "LOGGED_OUT", Set.of("LOGGED_IN"),
            "LOGGED_IN", Set.of("LOGGED_OUT")
    );

    public String transition(String current, String next) {
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(next)) {
            throw new InvalidStateTransitionException("Invalid auth state transition: " + current + " -> " + next);
        }
        return next;
    }
}
