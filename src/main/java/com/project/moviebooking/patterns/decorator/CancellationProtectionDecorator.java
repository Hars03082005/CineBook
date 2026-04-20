package com.project.moviebooking.patterns.decorator;

/**
 * Pattern: Decorator
 */
public class CancellationProtectionDecorator extends TicketDecorator {
    public CancellationProtectionDecorator(BaseTicketComponent delegate) {
        super(delegate);
    }

    @Override
    public double cost() {
        return delegate.cost() + 40.0;
    }

    @Override
    public String description() {
        return delegate.description() + ", Cancellation Protection";
    }
}
