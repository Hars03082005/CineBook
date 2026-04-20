package com.project.moviebooking.contracts;

import com.project.moviebooking.model.Report;

/**
 * SOLID: I (report-only contract)
 * GRASP: High Cohesion for reporting concerns.
 */
public interface IReportable {
    Report generateReport(String reportType);
    String exportReport(String reportType);
}
