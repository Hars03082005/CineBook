package com.project.moviebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Seat — one physical seat in a show (REGULAR / PREMIUM / VIP)
 * SOLID: S — only seat state, nothing else
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "seats")
public class Seat {

    @Id
    private String id;

    private String showId;       // which show this seat belongs to

    private String theatreId;    // for quick theatre-level queries

    private String seatNumber;   // e.g., "A1", "C5", "J15"

    private String row;          // single char: "A","B","C"...

    private String seatType;     // REGULAR / PREMIUM / VIP

    private double price;        // seat-level price after type multiplier

    private boolean booked = false;

    private String bookedByUserId;

    /** GRASP Information Expert: Seat knows its own availability. */
    public boolean isAvailable() {
        return !booked;
    }

    /** GRASP Information Expert: Seat controls hold operation. */
    public void hold(String userId) {
        this.booked = true;
        this.bookedByUserId = userId;
    }

    /** GRASP Information Expert: Seat controls release operation. */
    public void release() {
        this.booked = false;
        this.bookedByUserId = null;
    }
}
