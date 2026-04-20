package com.project.moviebooking.contracts;

import com.project.moviebooking.model.Movie;

import java.util.List;

/**
 * SOLID: I (search-focused interface)
 * GRASP: Information Expert over movie discovery.
 */
public interface ISearchable {
    List<Movie> searchMovies(String titleKeyword);
    List<Movie> filterByGenre(String genre);
    List<Movie> filterByLanguage(String language);
}
