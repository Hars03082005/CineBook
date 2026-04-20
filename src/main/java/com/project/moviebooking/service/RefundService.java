package com.project.moviebooking.service;

import com.project.moviebooking.dto.CancellationPreview;
import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Show;
import com.project.moviebooking.patterns.chain.CancellationChain;
import com.project.moviebooking.patterns.chain.CancellationEvaluationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * SOLID: S (refund policy + refund amount only)
 * GRASP: Pure Fabrication + Protected Variations
 */
@Service
@RequiredArgsConstructor
public class RefundService {
    private final CancellationChain cancellationChain;

    public CancellationPreview evaluate(Booking booking, Show show) {
        CancellationEvaluationContext context = new CancellationEvaluationContext(booking, show, LocalDateTime.now());
        cancellationChain.evaluate(context);
        double refundAmount = booking.getTotalAmount() * context.getRefundPercent() / 100.0;
        return new CancellationPreview(context.isCancellable(), context.getRefundPercent(), refundAmount, context.getReason());
    }
}
