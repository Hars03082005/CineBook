package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.Payment;

import java.util.List;
import java.util.Optional;

/** DIP abstraction for payment persistence. */
public interface IPaymentRepository {
    Optional<Payment> findByBookingId(String bookingId);
    List<Payment> findByUserId(String userId);
    List<Payment> findByStatus(String status);
}
