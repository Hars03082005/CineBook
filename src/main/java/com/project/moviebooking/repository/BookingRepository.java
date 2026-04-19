package com.project.moviebooking.repository;

import com.project.moviebooking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BookingRepository - MongoDB CRUD for Booking
 */
@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByShowId(String showId);

    List<Booking> findByMovieId(String movieId);

    List<Booking> findByUserIdAndShowId(String userId, String showId);

    List<Booking> findByUserIdAndStatus(String userId, String status);
}
