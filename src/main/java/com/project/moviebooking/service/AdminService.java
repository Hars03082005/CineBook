package com.project.moviebooking.service;

import com.project.moviebooking.model.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SOLID: S (admin aggregate workflow)
 * GRASP: Indirection
 */
@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final ReportService reportService;

    @Override
    public Report getSummaryReport() {
        return reportService.generateReport("summary");
    }
}
