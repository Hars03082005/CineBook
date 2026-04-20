package com.project.moviebooking.repository;

import com.project.moviebooking.model.Seat;
import com.project.moviebooking.repository.contracts.ISeatRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SeatRepository — MongoDB CRUD for Seat
 * SOLID: I — only seat-related queries
 */
@Repository
public interface SeatRepository extends MongoRepository<Seat, String>, ISeatRepository {

    /** All seats for a show — used to render the seat grid */
    List<Seat> findByShowId(String showId);

    /** All unbooked seats for a show — used by show service */
    List<Seat> findByShowIdAndBookedFalse(String showId);

    /** Find specific seat in a show — used for booking validation */
    Optional<Seat> findByShowIdAndSeatNumber(String showId, String seatNumber);

    /** Count available seats in a show */
    long countByShowIdAndBooked(String showId, boolean booked);

    /** Release seats for a cancelled booking */
    List<Seat> findByShowIdAndSeatNumberIn(String showId, List<String> seatNumbers);
}
