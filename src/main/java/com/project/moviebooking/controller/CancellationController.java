package com.project.moviebooking.controller;

import com.project.moviebooking.config.JwtUtil;
import com.project.moviebooking.dto.ApiResponse;
import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.service.BookingFacade;
import com.project.moviebooking.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SOLID: S (cancellation endpoint handling only)
 * GRASP: Controller
 */
@RestController
@RequestMapping("/api/cancellations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CancellationController {
    private final BookingFacade bookingFacade;
    private final JwtUtil jwtUtil;
    private final UserProfileService userProfileService;

    @GetMapping("/{bookingId}/preview")
    public ResponseEntity<ApiResponse<CancellationPreview>> preview(@PathVariable String bookingId,
                                                                    @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Cancellation preview", bookingFacade.getRefundAmount(bookingId, userId)));
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Booking>> cancel(@PathVariable String bookingId,
                                                       @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", bookingFacade.cancelBooking(bookingId, userId)));
    }

    private String extractUserId(String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userProfileService.getUserByEmail(email).getId();
    }
}
