package com.project.moviebooking.patterns.report;

import com.project.moviebooking.model.Report;

/**
 * Pattern: Strategy (report type generation)
 */
public interface ReportGenerator {
    String type();
    Report generate();
}
