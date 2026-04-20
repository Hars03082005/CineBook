package com.project.moviebooking.patterns;

import com.project.moviebooking.patterns.proxy.PaymentProxy;
import org.springframework.stereotype.Component;

/**
 * PaymentContext — Strategy Pattern Selector
 * ===========================================
 * PATTERN: Strategy (Behavioral)
 * SOLID: O (Open/Closed) — add new strategy = new class, no changes here
 * SOLID: D — depends on PaymentStrategy interface list
 *
 * Also integrates the ADAPTER pattern:
 * Each strategy (UPI/Card/Wallet) internally delegates to
 * PaymentGatewayAdapter which adapts ExternalPaymentAPI.
 *
 * Flow:
 * PaymentServiceImpl → PaymentContext.executePayment()
 *                    → [selects Strategy: UPI/Card/Wallet]
 *                    → Strategy.pay()
 *                    → PaymentGatewayAdapter.processPayment()
 *                    → ExternalPaymentAPI.initiateTransaction()
 * ===========================================
 */
@Component
public class PaymentContext {

    private final PaymentStrategyFactory strategyFactory;
    private final PaymentProxy paymentProxy;

    // SOLID: D — Spring injects ALL PaymentStrategy beans automatically
    public PaymentContext(PaymentStrategyFactory strategyFactory,
                          PaymentProxy paymentProxy) {
        this.strategyFactory = strategyFactory;
        this.paymentProxy = paymentProxy;
    }

    /**
     * Select strategy at runtime and execute payment
     *
     * STRATEGY SELECTION ORDER:
     * 1. Try to find matching PaymentStrategy
     * 2. Delegate to the strategy (which calls PaymentGatewayAdapter internally)
     *
     * @param method  "UPI" | "CREDIT_CARD" | "DEBIT_CARD" | "WALLET"
     */
    public String executePayment(String method, String userId,
                                 double amount, String details) {

        PaymentStrategy strategy = strategyFactory.getStrategy(method);
        System.out.println("\n[STRATEGY] Selected: " + strategy.getMethodName());

        String safeDetails = (details != null && !details.isBlank())
                ? details : "demo@upi";

        try {
            // Strategy executes payment (internally uses Adapter → ExternalAPI)
            String txnId = strategy.pay(userId, amount, safeDetails);
            if (txnId != null) {
                // Protected variation point for payment provider
                paymentProxy.processPayment(userId, amount, method);
            }
            return txnId;
        } catch (Exception e) {
            System.out.println("❌ [STRATEGY] Payment error: " + e.getMessage());
            return null;
        }
    }

}
