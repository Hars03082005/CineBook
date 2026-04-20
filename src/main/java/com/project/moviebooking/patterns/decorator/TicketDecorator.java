package com.project.moviebooking.patterns.decorator;

/**
 * Pattern: Decorator
 */
public abstract class TicketDecorator implements BaseTicketComponent {
    protected final BaseTicketComponent delegate;

    protected TicketDecorator(BaseTicketComponent delegate) {
        this.delegate = delegate;
    }
}
