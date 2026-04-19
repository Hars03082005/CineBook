package com.project.moviebooking.patterns;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Net banking payment strategy.
 */
@Component
public class NetBankingPaymentStrategy implements PaymentStrategy {

    @Override
    public String pay(String userId, double amount, String bankName) {
        System.out.println("🏦 [NET_BANKING] Processing Rs " + amount + " via bank: " + bankName);
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        return "NET_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    @Override
    public String getMethodName() {
        return "NETBANKING";
    }

    @Override
    public boolean validate(String details) {
        return details != null && !details.isBlank();
    }
}
