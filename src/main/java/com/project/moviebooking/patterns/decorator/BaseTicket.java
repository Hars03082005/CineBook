package com.project.moviebooking.patterns.decorator;

/**
 * Pattern: Decorator
 */
public class BaseTicket implements BaseTicketComponent {
    private final double amount;

    public BaseTicket(double amount) {
        this.amount = amount;
    }

    @Override
    public double cost() {
        return amount;
    }

    @Override
    public String description() {
        return "Base Ticket";
    }
}
