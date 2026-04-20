package com.project.moviebooking.service;

import com.project.moviebooking.contracts.ISeatManageable;
import com.project.moviebooking.model.Seat;
import com.project.moviebooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SOLID: S (seat availability and locking only)
 * GRASP: Information Expert over seat state
 */
@Service
@RequiredArgsConstructor
public class SeatService implements ISeatManageable {
    private final SeatRepository seatRepository;
    private final SeatLockManager seatLockManager;

    @Override
    public boolean checkAvailability(String showId, List<String> seatNumbers) {
        return seatNumbers.stream().allMatch(seatNo ->
                seatRepository.findByShowIdAndSeatNumber(showId, seatNo)
                        .map(s -> !s.isBooked() && !seatLockManager.isHeld(showId, seatNo))
                        .orElse(false));
    }

    @Override
    public List<Seat> holdSeat(String showId, String userId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        for (Seat seat : seats) {
            if (seat.isBooked() || seatLockManager.isHeld(showId, seat.getSeatNumber())) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is not available");
            }
            seat.setBooked(true);
            seat.setBookedByUserId(userId);
            seatLockManager.holdSeat(showId, seat.getSeatNumber());
        }
        return seatRepository.saveAll(seats);
    }

    @Override
    public void releaseSeat(String showId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        for (Seat seat : seats) {
            seat.setBooked(false);
            seat.setBookedByUserId(null);
            seatLockManager.releaseSeat(showId, seat.getSeatNumber());
        }
        seatRepository.saveAll(seats);
    }
}
