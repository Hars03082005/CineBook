package com.project.moviebooking.repository;

import com.project.moviebooking.model.Show;
import com.project.moviebooking.repository.contracts.IShowRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends MongoRepository<Show, String>, IShowRepository {

    List<Show> findByMovieId(String movieId);

    List<Show> findByTheatreId(String theatreId);

    List<Show> findByMovieIdAndShowDate(String movieId, LocalDate showDate);

    List<Show> findByMovieIdAndActive(String movieId, boolean active);

    List<Show> findByMovieIdAndTheatreId(String movieId, String theatreId);

    List<Show> findByMovieIdAndShowDateAndActive(String movieId, LocalDate showDate, boolean active);

    List<Show> findByActiveTrue();
}
