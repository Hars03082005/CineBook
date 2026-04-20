package com.project.moviebooking.patterns.chain;

import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Show;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Pattern: Chain of Responsibility
 */
public class CancellationEvaluationContext {
    private final Booking booking;
    private final Show show;
    private final LocalDateTime now;

    private boolean cancellable = true;
    private double refundPercent = 0.0;
    private String reason = "Cancellation policy evaluated.";

    public CancellationEvaluationContext(Booking booking, Show show, LocalDateTime now) {
        this.booking = booking;
        this.show = show;
        this.now = now;
    }

    public Booking getBooking() { return booking; }
    public Show getShow() { return show; }
    public LocalDateTime getNow() { return now; }
    public boolean isCancellable() { return cancellable; }
    public void setCancellable(boolean cancellable) { this.cancellable = cancellable; }
    public double getRefundPercent() { return refundPercent; }
    public void setRefundPercent(double refundPercent) { this.refundPercent = refundPercent; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long minutesBeforeShow() {
        LocalDateTime showStart = LocalDateTime.of(show.getShowDate(), show.getShowTime());
        return Duration.between(now, showStart).toMinutes();
    }
}
