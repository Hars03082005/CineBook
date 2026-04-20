package com.project.moviebooking.service;

import com.project.moviebooking.patterns.decorator.BaseTicket;
import com.project.moviebooking.patterns.decorator.BaseTicketComponent;
import com.project.moviebooking.patterns.decorator.CancellationProtectionDecorator;
import com.project.moviebooking.patterns.decorator.MealComboDecorator;
import com.project.moviebooking.patterns.decorator.SeatUpgradeDecorator;
import org.springframework.stereotype.Service;

/**
 * SOLID: S (all pricing rules in one place)
 * GRASP: Pure Fabrication
 */
@Service
public class PricingService {

    public double calculateConvenienceFee(int seatCount) {
        return seatCount * 20.0;
    }

    public double calculateTotal(double seatBaseAmount, int seatCount) {
        return seatBaseAmount + calculateConvenienceFee(seatCount);
    }

    public double applyDecorators(double baseAmount, boolean seatUpgrade, boolean cancellationProtection, boolean mealCombo) {
        BaseTicketComponent ticket = new BaseTicket(baseAmount);
        if (seatUpgrade) ticket = new SeatUpgradeDecorator(ticket);
        if (cancellationProtection) ticket = new CancellationProtectionDecorator(ticket);
        if (mealCombo) ticket = new MealComboDecorator(ticket);
        return ticket.cost();
    }
}
