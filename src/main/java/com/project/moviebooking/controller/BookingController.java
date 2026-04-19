package com.project.moviebooking.controller;

import com.project.moviebooking.config.JwtUtil;
import com.project.moviebooking.dto.ApiResponse;
import com.project.moviebooking.dto.BookingRequest;
import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Ticket;
import com.project.moviebooking.service.BookingService;
import com.project.moviebooking.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BookingController — GRASP Controller + DIP demonstration
 * =========================================================
 * SOLID: D — Dependency Inversion Principle
 *
 *   @Autowired BookingService bookingService;  ← INTERFACE (correct DIP)
 *   NOT:
 *   @Autowired BookingServiceImpl bookingService;  ← concrete class (wrong)
 *
 * Spring injects BookingServiceImpl at runtime transparently.
 * This controller does NOT import BookingServiceImpl at all.
 *
 * GRASP: Controller — handles HTTP events, delegates to service,
 *        contains ZERO business logic
 * SOLID: S — only HTTP request/response concerns here
 * =========================================================
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BookingController {

    // ── SOLID: D — depends on INTERFACE, not implementation ──
    private final BookingService     bookingService;     // ← interface injection (DIP)
    private final UserProfileService userProfileService; // ← SRP: profile concern
    private final JwtUtil            jwtUtil;

    /**
     * POST /api/bookings
     * Create booking → lock seats atomically → PENDING status
     *
     * GRASP: Controller — delegates entirely to bookingService.createBooking()
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String userId = extractUserId(authHeader);
        Booking booking = bookingService.createBooking(request, userId);  // ← delegation
        return ResponseEntity.ok(
                ApiResponse.success("Seats reserved! Proceed to payment.", booking));
    }

    /**
     * GET /api/bookings/my
     * User's booking history
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Booking>>> getMyBookings(
            @RequestHeader("Authorization") String authHeader) {

        String userId = extractUserId(authHeader);
        return ResponseEntity.ok(
                ApiResponse.success("Your bookings",
                        bookingService.getBookingsByUser(userId)));
    }

    /**
     * GET /api/bookings/user/{userId}
     * Booking history by userId (alternative endpoint)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByUser(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                ApiResponse.success("Bookings for user",
                        bookingService.getBookingsByUser(userId)));
    }

    /**
     * GET /api/bookings/{id}
     * Single booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBooking(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.success("Booking found", bookingService.getBookingById(id)));
    }

        /**
         * GET /api/bookings/{id}/cancellation-preview
         * Show policy result + refund amount before user confirms cancellation.
         */
        @GetMapping("/{id}/cancellation-preview")
        public ResponseEntity<ApiResponse<CancellationPreview>> getCancellationPreview(
                        @PathVariable String id,
                        @RequestHeader("Authorization") String authHeader) {

                String userId = extractUserId(authHeader);
                CancellationPreview preview = bookingService.getCancellationPreview(id, userId);
                return ResponseEntity.ok(ApiResponse.success("Cancellation policy evaluated.", preview));
        }

    /**
     * PUT /api/bookings/{id}/cancel
     * Cancel booking → release seats → status CANCELLED
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        String userId = extractUserId(authHeader);
        CancellationPreview preview = bookingService.getCancellationPreview(id, userId);
        Booking cancelled = bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok(
                ApiResponse.success("Booking cancelled. Refund processed: Rs " + preview.getRefundAmount(), cancelled));
    }

    /**
     * POST /api/bookings/{id}/cancel (also support POST for frontend)
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBookingPost(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {
        return cancelBooking(id, authHeader);
    }

    /**
     * GET /api/bookings/{bookingId}/ticket
     */
    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<ApiResponse<Ticket>> getTicketForBooking(
            @PathVariable String bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Ticket",
                bookingService.getTicketByBookingId(bookingId)));
    }

    /**
     * GET /api/bookings/admin/all — Admin only
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        return ResponseEntity.ok(
                ApiResponse.success("All bookings", bookingService.getAllBookings()));
    }

    // ── Helper: extract userId from JWT ──
    private String extractUserId(String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userProfileService.getUserByEmail(email).getId();
    }
}
