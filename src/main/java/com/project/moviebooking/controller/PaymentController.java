package com.project.moviebooking.controller;

import com.project.moviebooking.config.JwtUtil;
import com.project.moviebooking.dto.ApiResponse;
import com.project.moviebooking.dto.PaymentRequest;
import com.project.moviebooking.model.Payment;
import com.project.moviebooking.model.Ticket;
import com.project.moviebooking.service.BookingFacade;
import com.project.moviebooking.service.PaymentService;
import com.project.moviebooking.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController
 * SOLID: D — depends on PaymentService interface
 * GRASP: Controller — zero business logic, delegates entirely to service
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PaymentController {

    private final PaymentService     paymentService;  // kept for direct query endpoints
    private final BookingFacade      bookingFacade;   // facade indirection
    private final JwtUtil            jwtUtil;
    private final UserProfileService userProfileService;

    /**
     * POST /api/payments/process
     * Runs Strategy Pattern (UPI/Card/Wallet) → Adapter → ExternalPaymentAPI
     * On success → TicketFactory creates ticket → Observer notifies Email/SMS
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentWithTicket>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String userId = extractUserId(authHeader);
        Payment payment = bookingFacade.processPayment(request, userId);

        if ("SUCCESS".equals(payment.getStatus())) {
            Ticket ticket = null;
            try { ticket = bookingFacade.downloadTicket(payment.getBookingId()); }
            catch (Exception ignored) {}

            return ResponseEntity.ok(ApiResponse.success(
                    "Payment successful! Ticket issued.",
                    new PaymentWithTicket(payment, ticket)));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Payment failed. Please try again."));
        }
    }

    /**
     * GET /api/payments/booking/{bookingId}
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Payment>> getByBooking(
            @PathVariable String bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Payment details",
                paymentService.getPaymentByBookingId(bookingId)));
    }

    /**
     * GET /api/payments/my
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Payment>>> getMyPayments(
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Your payments",
                paymentService.getPaymentsByUser(userId)));
    }

    private String extractUserId(String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userProfileService.getUserByEmail(email).getId();
    }

    /** Response DTO containing both payment + ticket for frontend */
    public record PaymentWithTicket(Payment payment, Ticket ticket) {}
}
