package com.project.moviebooking.service;

import com.project.moviebooking.contracts.INotifiable;
import com.project.moviebooking.patterns.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SOLID: S (notification only)
 * GRASP: Pure Fabrication
 */
@Service
@RequiredArgsConstructor
public class NotificationService implements INotifiable {
    private final BookingEventPublisher bookingEventPublisher;

    @Override
    public void sendConfirmation(String bookingId, String userId, String userEmail, String movieTitle, String ticketCode, double amount, String seatNumbers) {
        bookingEventPublisher.publishBookingConfirmed(bookingId, userId, userEmail, movieTitle, ticketCode, amount, seatNumbers);
    }

    @Override
    public void sendCancellationAlert(String bookingId, String userId, String reason) {
        System.out.println("[NOTIFICATION] Cancellation alert | booking=" + bookingId + " user=" + userId + " reason=" + reason);
    }
}
