package com.project.moviebooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Report model for admin analytics payloads.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    private String reportName;

    private LocalDateTime generatedAt = LocalDateTime.now();

    private Map<String, Object> metrics;
}
