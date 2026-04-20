package com.project.moviebooking.patterns.report;

import com.project.moviebooking.model.Booking;
import com.project.moviebooking.model.Report;
import com.project.moviebooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pattern: Strategy (concrete report strategy)
 */
@Component
@RequiredArgsConstructor
public class SummaryReportGenerator implements ReportGenerator {
    private final BookingRepository bookingRepository;

    @Override
    public String type() {
        return "summary";
    }

    @Override
    public Report generate() {
        List<Booking> bookings = bookingRepository.findAll();
        long confirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalBookings", bookings.size());
        metrics.put("confirmedBookings", confirmed);
        metrics.put("cancelledBookings", cancelled);
        metrics.put("generatedAt", LocalDateTime.now());

        return new Report("Booking and Revenue Summary", LocalDateTime.now(), metrics);
    }
}
