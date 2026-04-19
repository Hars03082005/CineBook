package com.project.moviebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Payment Model — stores payment + GST breakdown
 * ================================================
 * SOLID: O — GST logic added via PaymentService (Open for extension)
 * OOAD: Adapter — PaymentGatewayAdapter bridges ExternalPaymentAPI
 *
 * GST RULES (Indian cinema):
 *   baseAmount > 100 → GST = 18%
 *   baseAmount ≤ 100 → GST = 12%
 * ================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String bookingId;

    private String userId;

    // GST Breakdown fields
    private double baseAmount;      // ticket price before GST

    private double gstPercent;      // 18.0 or 12.0

    private double gstAmount;       // calculated GST

    private double totalAmount;     // baseAmount + gstAmount  ← total charged

    // CREDIT_CARD, DEBIT_CARD, UPI, WALLET
    private String paymentMethod;

    // SUCCESS, FAILED, PENDING, REFUNDED
    private String status = "PENDING";

    private String transactionId;

    // Refund fields
    private double refundAmount = 0;       // 50% of baseAmount if cancelled > 2h before show
    private String refundStatus = "NONE";  // NONE, ELIGIBLE, PROCESSED, NOT_ELIGIBLE
    private String refundReason = "";

    private LocalDateTime paymentTime = LocalDateTime.now();
}
