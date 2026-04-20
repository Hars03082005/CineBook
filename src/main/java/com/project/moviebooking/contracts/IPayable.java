package com.project.moviebooking.contracts;

import com.project.moviebooking.dto.PaymentRequest;
import com.project.moviebooking.model.Payment;

/**
 * SOLID: I (fine-grained payment contract)
 * GRASP: Controller delegates payment to this abstraction.
 */
public interface IPayable {
    Payment processPayment(PaymentRequest request, String userId);
    String getPaymentStatus(String bookingId);
}
