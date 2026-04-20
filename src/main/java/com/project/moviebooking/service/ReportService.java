package com.project.moviebooking.service;

import com.project.moviebooking.contracts.IReportable;
import com.project.moviebooking.model.Report;
import com.project.moviebooking.patterns.report.ReportGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SOLID: S (report workflow only), O (new report type via new generator)
 * GRASP: Pure Fabrication
 */
@Service
public class ReportService implements IReportable {
    private final Map<String, ReportGenerator> generators;

    public ReportService(List<ReportGenerator> generators) {
        this.generators = generators.stream().collect(Collectors.toMap(ReportGenerator::type, Function.identity()));
    }

    @Override
    public Report generateReport(String reportType) {
        ReportGenerator generator = generators.get(reportType == null ? "summary" : reportType.toLowerCase());
        if (generator == null) {
            throw new RuntimeException("Unsupported report type: " + reportType);
        }
        return generator.generate();
    }

    @Override
    public String exportReport(String reportType) {
        Report r = generateReport(reportType);
        return "REPORT:" + r.getReportName() + " generatedAt=" + r.getGeneratedAt();
    }
}
