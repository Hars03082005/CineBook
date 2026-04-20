package com.project.moviebooking.contracts;

import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.model.Booking;

/**
 * SOLID: I (fine-grained cancellation contract)
 * GRASP: Indirection for cancellation flow.
 */
public interface ICancellable {
    Booking cancelBooking(String bookingId, String userId);
    CancellationPreview getRefundAmount(String bookingId, String userId);
}
