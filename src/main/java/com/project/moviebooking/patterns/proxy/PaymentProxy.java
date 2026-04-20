package com.project.moviebooking.patterns.proxy;

import com.project.moviebooking.patterns.PaymentGateway;
import org.springframework.stereotype.Component;

/**
 * SOLID: D (depends on PaymentGateway abstraction)
 * GRASP: Protected Variations for gateway swaps
 * Pattern: Proxy
 */
@Component
public class PaymentProxy implements PaymentGateway {
    private final PaymentGateway realGateway;

    public PaymentProxy(com.project.moviebooking.patterns.PaymentGatewayAdapter realGateway) {
        this.realGateway = realGateway;
    }

    @Override
    public String processPayment(String userId, double amount, String method) {
        validate(userId, amount);
        String maskedUser = userId == null || userId.length() < 4 ? "***" : userId.substring(0, 4) + "***";
        System.out.println("[PROXY] Processing payment for user=" + maskedUser + " amount=" + amount + " method=" + method);

        RuntimeException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                String txn = realGateway.processPayment(userId, amount, method);
                if (txn != null) {
                    return txn;
                }
            } catch (RuntimeException ex) {
                last = ex;
            }
        }
        if (last != null) {
            throw last;
        }
        return null;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        return realGateway.refundPayment(transactionId);
    }

    @Override
    public String getPaymentStatus(String transactionId) {
        return realGateway.getPaymentStatus(transactionId);
    }

    private void validate(String userId, double amount) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Invalid user for payment");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
