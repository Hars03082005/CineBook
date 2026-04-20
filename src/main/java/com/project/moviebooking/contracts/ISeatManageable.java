package com.project.moviebooking.contracts;

import com.project.moviebooking.model.Seat;

import java.util.List;

/**
 * SOLID: I (seat lifecycle only)
 * GRASP: Information Expert for seat availability and locking.
 */
public interface ISeatManageable {
    boolean checkAvailability(String showId, List<String> seatNumbers);
    List<Seat> holdSeat(String showId, String userId, List<String> seatNumbers);
    void releaseSeat(String showId, List<String> seatNumbers);
}
