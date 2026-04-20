package com.project.moviebooking.patterns.state;

/**
 * Pattern: State
 */
public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
