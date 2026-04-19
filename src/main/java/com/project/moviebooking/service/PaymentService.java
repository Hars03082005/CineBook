package com.project.moviebooking.service;

import com.project.moviebooking.dto.PaymentRequest;
import com.project.moviebooking.model.Payment;

import java.util.List;

/**
 * PaymentService — Interface (Dependency Inversion Principle)
 * ============================================================
 * SOLID: D — PaymentController depends on this, not PaymentServiceImpl
 * ============================================================
 */
public interface PaymentService {

    /** Process payment and issue ticket on success */
    Payment processPayment(PaymentRequest request, String userId);

    /** Get payment record for a booking */
    Payment getPaymentByBookingId(String bookingId);

    /** Get all payments for a user */
    List<Payment> getPaymentsByUser(String userId);

    /** Process refund for a cancelled booking */
    Payment processRefund(String bookingId);
}
