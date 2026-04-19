package com.project.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for cancellation policy preview.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancellationPreview {

    private boolean cancellable;

    private double refundPercent;

    private double refundAmount;

    private String reason;
}
