package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.Booking;

import java.util.List;

/** DIP abstraction for booking persistence. */
public interface IBookingRepository {
    List<Booking> findByUserId(String userId);
    List<Booking> findByShowId(String showId);
    List<Booking> findByMovieId(String movieId);
    List<Booking> findByUserIdAndShowId(String userId, String showId);
}
