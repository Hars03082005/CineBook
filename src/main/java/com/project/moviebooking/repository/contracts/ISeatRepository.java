package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.Seat;

import java.util.List;
import java.util.Optional;

/** DIP abstraction for seat persistence. */
public interface ISeatRepository {
    List<Seat> findByShowId(String showId);
    List<Seat> findByShowIdAndBookedFalse(String showId);
    Optional<Seat> findByShowIdAndSeatNumber(String showId, String seatNumber);
}
