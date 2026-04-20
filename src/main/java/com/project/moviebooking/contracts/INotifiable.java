package com.project.moviebooking.contracts;

/**
 * SOLID: I (notification-specific contract)
 * GRASP: Pure Fabrication for messaging responsibilities.
 */
public interface INotifiable {
    void sendConfirmation(String bookingId, String userId, String userEmail, String movieTitle, String ticketCode, double amount, String seatNumbers);
    void sendCancellationAlert(String bookingId, String userId, String reason);
}
