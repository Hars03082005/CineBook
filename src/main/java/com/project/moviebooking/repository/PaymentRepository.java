package com.project.moviebooking.repository;

import com.project.moviebooking.model.Payment;
import com.project.moviebooking.repository.contracts.IPaymentRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository - MongoDB CRUD for Payment
 */
@Repository
public interface PaymentRepository extends MongoRepository<Payment, String>, IPaymentRepository {

    Optional<Payment> findByBookingId(String bookingId);

    List<Payment> findByUserId(String userId);

    List<Payment> findByStatus(String status);

    List<Payment> findByRefundStatus(String refundStatus);
}
