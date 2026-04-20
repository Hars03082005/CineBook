package com.project.moviebooking.patterns.decorator;

/**
 * Pattern: Decorator
 */
public class MealComboDecorator extends TicketDecorator {
    public MealComboDecorator(BaseTicketComponent delegate) {
        super(delegate);
    }

    @Override
    public double cost() {
        return delegate.cost() + 180.0;
    }

    @Override
    public String description() {
        return delegate.description() + ", Meal Combo";
    }
}
