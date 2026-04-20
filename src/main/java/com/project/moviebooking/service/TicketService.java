package com.project.moviebooking.service;

import com.project.moviebooking.model.Ticket;
import com.project.moviebooking.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SOLID: S (ticket retrieval/format operations)
 * GRASP: Pure Fabrication for ticket concerns.
 */
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public Ticket getByBookingId(String bookingId) {
        return ticketRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Ticket getByTicketId(String ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }
}
