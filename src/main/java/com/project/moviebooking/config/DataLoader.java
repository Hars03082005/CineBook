package com.project.moviebooking.config;

import com.project.moviebooking.model.*;
import com.project.moviebooking.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DataLoader — Auto-populates MongoDB on startup (idempotent)
 * =============================================================
 * Inserts: 10 Theatres · 6 Movies · Shows (movie×theatre×3times×3days)
 *          150 Seats per Show (VIP/PREMIUM/REGULAR by row)
 *
 * RUNS ONLY ONCE — checks if data exists before inserting.
 * Safe to restart the server multiple times.
 * =============================================================
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader {

    private final TheatreRepository theatreRepository;
    private final MovieRepository   movieRepository;
    private final ShowRepository    showRepository;
    private final SeatRepository    seatRepository;
    private final UserRepository    userRepository;
    private final PasswordEncoder   passwordEncoder;

    @PostConstruct
    public void loadData() {
        System.out.println("\n🎬 [DATALOADER] Starting CineBook data initialization...");

        seedDefaultAdmin();
        List<Theatre> theatres = loadTheatres();
        List<Movie>   movies   = loadMovies();
        loadShowsAndSeats(movies, theatres);

        System.out.println("✅ [DATALOADER] Database ready!");
        System.out.println("   Theatres: " + theatreRepository.count());
        System.out.println("   Movies:   " + movieRepository.count());
        System.out.println("   Shows:    " + showRepository.count());
        System.out.println("   Seats:    " + seatRepository.count() + "\n");
    }

    private void seedDefaultAdmin() {
        User admin = userRepository.findByEmail("admin@cinebook.com").orElseGet(User::new);
        boolean alreadyExisted = admin.getId() != null;

        admin.setName("CineBook Admin");
        admin.setEmail("admin@cinebook.com");
        admin.setPhone("9999999999");
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        System.out.println(alreadyExisted
                ? "ℹ️  [DATALOADER] Default admin reset: admin@cinebook.com / admin123"
                : "✅ [DATALOADER] Default admin created: admin@cinebook.com / admin123");
    }

    // ─────────────────────────────────────────────────────────────────
    // 10 REAL BANGALORE THEATRES
    // ─────────────────────────────────────────────────────────────────
    private List<Theatre> loadTheatres() {
        if (theatreRepository.count() > 0) {
            System.out.println("ℹ️  [DATALOADER] Theatres already loaded — skipping.");
            return theatreRepository.findAll();
        }

        List<Theatre> theatres = List.of(
            buildTheatre("PVR Orion Mall",          "Rajajinagar, Bengaluru"),
            buildTheatre("INOX Garuda Mall",         "Magrath Road, Bengaluru"),
            buildTheatre("Cinepolis Forum Mall",      "Koramangala, Bengaluru"),
            buildTheatre("PVR VR Bengaluru",          "Whitefield, Bengaluru"),
            buildTheatre("INOX Mantri Square",        "Malleswaram, Bengaluru"),
            buildTheatre("Cinepolis Nexus",           "Churchstreet, Bengaluru"),
            buildTheatre("PVR Gold Gopalan",          "Old Madras Road, Bengaluru"),
            buildTheatre("Urvashi Theatre",           "Lalbagh Road, Bengaluru"),
            buildTheatre("Innovative Multiplex",      "Marathahalli, Bengaluru"),
            buildTheatre("Swagath Cinema",            "JP Nagar, Bengaluru")
        );

        List<Theatre> saved = theatreRepository.saveAll(theatres);
        System.out.println("✅ [DATALOADER] Inserted " + saved.size() + " theatres.");
        return saved;
    }

    private Theatre buildTheatre(String name, String address) {
        Theatre t = new Theatre();
        t.setName(name);
        t.setCity("Bengaluru");
        t.setAddress(address);
        t.setRows(10);
        t.setColumns(15);
        t.setTotalSeats(150);
        t.setActive(true);
        return t;
    }

    // ─────────────────────────────────────────────────────────────────
    // 8 MOVIES
    // ─────────────────────────────────────────────────────────────────
    private List<Movie> loadMovies() {
        if (movieRepository.count() > 0) {
            System.out.println("ℹ️  [DATALOADER] Movies already loaded — skipping.");
            return movieRepository.findAll();
        }

        List<Movie> movies = List.of(
            buildMovie("KGF Chapter 3",     "Action",        "Kannada", 165, 9.0, "UA"),
            buildMovie("Pushpa 2 The Rule", "Action",        "Telugu",  190, 8.5, "A"),
            buildMovie("Stree 3",           "Horror-Comedy", "Hindi",   140, 8.8, "UA"),
            buildMovie("RRR 2",             "Action-Drama",  "Telugu",  185, 9.2, "UA"),
            buildMovie("Kalki 2899 AD",     "Sci-Fi",        "Telugu",  181, 8.2, "UA"),
            buildMovie("Salaar 2",          "Action",        "Kannada", 170, 8.3, "UA")
        );

        List<Movie> saved = movieRepository.saveAll(movies);
        System.out.println("✅ [DATALOADER] Inserted " + saved.size() + " movies.");
        return saved;
    }

    private Movie buildMovie(String title, String genre, String language,
                             int duration, double rating, String certificate) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        m.setLanguage(language);
        m.setDurationMinutes(duration);
        m.setRating(rating);
        m.setCertificate(certificate);
        m.setPosterUrl("");   // CSS gradient used in frontend
        m.setActive(true);
        return m;
    }

    // ─────────────────────────────────────────────────────────────────
    // SHOWS: each movie × each theatre × 3 timings × 3 days
    // SEATS: 150 per show (VIP rows A-B, PREMIUM C-E, REGULAR F-J)
    // ─────────────────────────────────────────────────────────────────
    private void loadShowsAndSeats(List<Movie> movies, List<Theatre> theatres) {
        if (showRepository.count() > 0) {
            System.out.println("ℹ️  [DATALOADER] Shows already loaded — skipping.");
            return;
        }

        // 3 timings (Morning / Afternoon / Evening) — Night 9:30 removed
        record Timing(String time, double price) {}
        List<Timing> timings = List.of(
            new Timing("10:00", 150),   // Morning
            new Timing("14:00", 200),   // Afternoon
            new Timing("18:00", 250)    // Evening
        );

        // Today + next 2 days (3 days total)
        List<LocalDate> dates = List.of(
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        );

        List<Show>  showsBatch = new ArrayList<>();
        List<Seat>  seatsBatch = new ArrayList<>();

        for (Movie movie : movies) {
            for (Theatre theatre : theatres) {
                for (LocalDate date : dates) {
                    for (Timing timing : timings) {
                        // Create Show
                        Show show = new Show();
                        show.setMovieId(movie.getId());
                        show.setTheatreId(theatre.getId());
                        show.setShowDate(date);
                        show.setShowTime(LocalTime.parse(timing.time()));
                        show.setAvailableSeats(150);
                        show.setBookedSeats(new ArrayList<>());
                        show.setPrice(timing.price());
                        show.setActive(true);
                        showsBatch.add(show);
                    }
                }
            }
        }

        // Save all shows first to get IDs
        List<Show> savedShows = showRepository.saveAll(showsBatch);
        System.out.println("✅ [DATALOADER] Inserted " + savedShows.size() + " shows.");

        // Need a map from showId → theatreId for seat generation
        // savedShows already have IDs, and we know their theatreId from the show object
        char[] rows = {'A','B','C','D','E','F','G','H','I','J'};

        for (Show show : savedShows) {
            double basePrice = show.getPrice();
            for (int r = 0; r < rows.length; r++) {
                char rowChar = rows[r];
                String seatType;
                double seatPrice;

                if (r < 2) {                    // rows A-B: VIP
                    seatType  = "VIP";
                    seatPrice = basePrice * 2.0;
                } else if (r < 5) {             // rows C-E: PREMIUM
                    seatType  = "PREMIUM";
                    seatPrice = basePrice * 1.5;
                } else {                         // rows F-J: REGULAR
                    seatType  = "REGULAR";
                    seatPrice = basePrice * 1.0;
                }

                for (int col = 1; col <= 15; col++) {
                    Seat seat = new Seat();
                    seat.setShowId(show.getId());
                    seat.setTheatreId(show.getTheatreId());
                    seat.setSeatNumber(rowChar + String.valueOf(col));
                    seat.setRow(String.valueOf(rowChar));
                    seat.setSeatType(seatType);          // ← correct field name
                    seat.setPrice(seatPrice);
                    seat.setBooked(false);
                    seat.setBookedByUserId(null);
                    seatsBatch.add(seat);
                }
            }
        }

        // Save all seats in batch (may be large — split into chunks)
        int chunkSize = 1000;
        for (int i = 0; i < seatsBatch.size(); i += chunkSize) {
            List<Seat> chunk = seatsBatch.subList(i, Math.min(i + chunkSize, seatsBatch.size()));
            seatRepository.saveAll(chunk);
        }
        System.out.println("✅ [DATALOADER] Inserted " + seatsBatch.size() + " seats.");
    }
}
