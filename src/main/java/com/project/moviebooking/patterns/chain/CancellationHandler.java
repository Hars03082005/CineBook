package com.project.moviebooking.patterns.chain;

/**
 * Pattern: Chain of Responsibility
 */
public interface CancellationHandler {
    void handle(CancellationEvaluationContext context);
}
