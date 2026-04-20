package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.Show;

import java.time.LocalDate;
import java.util.List;

/** DIP abstraction for show persistence. */
public interface IShowRepository {
    List<Show> findByMovieId(String movieId);
    List<Show> findByTheatreId(String theatreId);
    List<Show> findByMovieIdAndShowDate(String movieId, LocalDate showDate);
}
