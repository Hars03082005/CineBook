package com.project.moviebooking.controller;

import com.project.moviebooking.dto.ApiResponse;
import com.project.moviebooking.model.Seat;
import com.project.moviebooking.model.Show;
import com.project.moviebooking.model.Theatre;
import com.project.moviebooking.model.Movie;
import com.project.moviebooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ShowController — public endpoints for shows and seat grid
 * GRASP: Controller — delegates to repositories / services
 * SOLID: D — depends on repository interfaces
 */
@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","http://localhost:5173"})
public class ShowController {

    private final ShowRepository    showRepository;
    private final SeatRepository    seatRepository;
    private final MovieRepository   movieRepository;
    private final TheatreRepository theatreRepository;

    /**
     * GET /api/shows/movie/{movieId}
     * Returns all active shows for a movie grouped by theatre + date
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getShowsByMovie(
            @PathVariable String movieId,
            @RequestParam(required = false) String date) {

        Movie movie = movieRepository.findById(movieId).orElse(null);

        List<Show> shows = showRepository.findByMovieIdAndActive(movieId, true).stream()
            .filter(this::isUpcoming)
            .collect(Collectors.toList());

        // Filter by date if provided
        if (date != null && !date.isBlank()) {
            LocalDate filterDate = LocalDate.parse(date);
            shows = shows.stream()
                    .filter(s -> s.getShowDate().equals(filterDate))
                    .collect(Collectors.toList());
        }

        // Group by theatreId
        Map<String, List<Show>> byTheatre = shows.stream()
                .collect(Collectors.groupingBy(Show::getTheatreId));

        List<Map<String,Object>> result = new ArrayList<>();
        byTheatre.forEach((theatreId, theatreShows) -> {
            Theatre theatre = theatreRepository.findById(theatreId).orElse(null);
            if (theatre == null) return;

            // Group by date inside each theatre
            Map<String, List<Map<String,Object>>> byDate = new LinkedHashMap<>();
            theatreShows.stream()
                    .sorted(Comparator.comparing(Show::getShowDate)
                            .thenComparing(Show::getShowTime))
                    .forEach(show -> {
                        String d = show.getShowDate().toString();
                        byDate.computeIfAbsent(d, k -> new ArrayList<>())
                              .add(showToMap(show));
                    });

            Map<String,Object> entry = new LinkedHashMap<>();
            entry.put("theatre",   theatre);
            entry.put("showsByDate", byDate);
            result.add(entry);
        });

        return ResponseEntity.ok(ApiResponse.success(
                "Shows for movie: " + (movie != null ? movie.getTitle() : movieId), result));
    }

    /**
     * GET /api/shows/{showId}/seats
     * Returns all 150 seats for a show — used to render seat grid
     */
    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSeatsByShow(
            @PathVariable String showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found: " + showId));

        if (!isUpcoming(show)) {
            throw new RuntimeException("Cannot access seats for an already-started/past show.");
        }

        List<Seat> seats = seatRepository.findByShowId(showId);

        // Enrich with show + movie + theatre info for frontend
        Movie   movie   = movieRepository.findById(show.getMovieId()).orElse(null);
        Theatre theatre = theatreRepository.findById(show.getTheatreId()).orElse(null);

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("show",       show);
        response.put("seats",      seats);
        response.put("movie",      movie);
        response.put("theatre",    theatre);
        response.put("totalSeats", seats.size());
        response.put("bookedCount", seats.stream().filter(Seat::isBooked).count());
        response.put("availableCount", seats.stream().filter(s -> !s.isBooked()).count());

        return ResponseEntity.ok(ApiResponse.success("Seats loaded", response));
    }

    /**
     * GET /api/shows/{showId}
     * Single show details
     */
    @GetMapping("/{showId}")
    public ResponseEntity<ApiResponse<Show>> getShow(@PathVariable String showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));
        return ResponseEntity.ok(ApiResponse.success("Show found", show));
    }

    /**
     * GET /api/shows?movieId=X&date=YYYY-MM-DD
     * Filtered shows
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Show>>> getShows(
            @RequestParam(required = false) String movieId,
            @RequestParam(required = false) String date) {

        List<Show> shows;
        if (movieId != null && date != null) {
            shows = showRepository.findByMovieIdAndShowDateAndActive(
                    movieId, LocalDate.parse(date), true);
        } else if (movieId != null) {
            shows = showRepository.findByMovieIdAndActive(movieId, true);
        } else {
            shows = showRepository.findAll().stream().filter(Show::isActive).collect(Collectors.toList());
        }
        shows = shows.stream().filter(this::isUpcoming).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Shows", shows));
    }

    private boolean isUpcoming(Show show) {
        LocalDateTime showDateTime = LocalDateTime.of(show.getShowDate(), show.getShowTime());
        return showDateTime.isAfter(LocalDateTime.now());
    }

    // Helper
    private Map<String,Object> showToMap(Show show) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",             show.getId());
        m.put("showDate",       show.getShowDate().toString());
        m.put("showTime",       show.getShowTime().toString());
        m.put("price",          show.getPrice());
        m.put("availableSeats", show.getAvailableSeats());
        m.put("active",         show.isActive());
        return m;
    }
}
