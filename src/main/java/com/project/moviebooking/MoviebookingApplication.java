package com.project.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * CineBook — Movie Ticket Booking System
 * =======================================
 * OOAD Mini Project | PES University
 *
 * @EnableAsync — required for @Async in Observer listeners
 * (EmailNotificationListener and SMSNotificationListener run async)
 * =======================================
 */
@SpringBootApplication
@EnableAsync
public class MoviebookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviebookingApplication.class, args);
        System.out.println("\n" +
            "  ╔═══════════════════════════════════════════════╗\n" +
            "  ║   🎬  CineBook Movie Booking System           ║\n" +
            "  ║   OOAD Project — PES University               ║\n" +
            "  ╠═══════════════════════════════════════════════╣\n" +
            "  ║   Backend:  http://localhost:8080             ║\n" +
            "  ║   Frontend: http://localhost:3000             ║\n" +
            "  ║   MongoDB:  localhost:27017/moviebooking      ║\n" +
            "  ╠═══════════════════════════════════════════════╣\n" +
            "  ║   Patterns: Singleton · Adapter · Observer   ║\n" +
            "  ║             Factory   · Strategy             ║\n" +
            "  ╚═══════════════════════════════════════════════╝\n");
    }
}
