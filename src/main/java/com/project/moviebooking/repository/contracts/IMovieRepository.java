package com.project.moviebooking.repository.contracts;

import com.project.moviebooking.model.Movie;

import java.util.List;

/** DIP abstraction for movie persistence. */
public interface IMovieRepository {
    List<Movie> findByActiveTrue();
    List<Movie> findByGenreIgnoreCase(String genre);
    List<Movie> findByLanguageIgnoreCase(String language);
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
