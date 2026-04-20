package com.project.moviebooking.patterns.decorator;

/**
 * Pattern: Decorator
 */
public class SeatUpgradeDecorator extends TicketDecorator {
    public SeatUpgradeDecorator(BaseTicketComponent delegate) {
        super(delegate);
    }

    @Override
    public double cost() {
        return delegate.cost() + 120.0;
    }

    @Override
    public String description() {
        return delegate.description() + ", Seat Upgrade";
    }
}
