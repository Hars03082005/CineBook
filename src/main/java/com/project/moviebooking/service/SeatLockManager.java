package com.project.moviebooking.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SOLID: S (seat hold window management only)
 * GRASP: Pure Fabrication
 */
@Component
public class SeatLockManager {
    private static final int HOLD_MINUTES = 5;
    private final Map<String, LocalDateTime> seatLockExpiry = new ConcurrentHashMap<>();

    public void holdSeat(String showId, String seatNo) {
        seatLockExpiry.put(showId + "::" + seatNo, LocalDateTime.now().plusMinutes(HOLD_MINUTES));
    }

    public void releaseSeat(String showId, String seatNo) {
        seatLockExpiry.remove(showId + "::" + seatNo);
    }

    public boolean isHeld(String showId, String seatNo) {
        LocalDateTime expiry = seatLockExpiry.get(showId + "::" + seatNo);
        return expiry != null && expiry.isAfter(LocalDateTime.now());
    }
}
