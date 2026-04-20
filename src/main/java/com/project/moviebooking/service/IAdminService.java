package com.project.moviebooking.service;

import com.project.moviebooking.model.Report;

/**
 * SOLID: D abstraction for admin use cases.
 */
public interface IAdminService {
    Report getSummaryReport();
}
