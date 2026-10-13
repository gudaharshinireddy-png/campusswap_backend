package com.example.campusswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody Report report) {

        if (report.getItemId() == null)
            return ResponseEntity.badRequest().body("Item ID is required");

        if (report.getReportedBy() == null)
            return ResponseEntity.badRequest().body("Reported by user ID is required");

        if (report.getReason() == null ||
                report.getReason().trim().isEmpty())
            return ResponseEntity.badRequest().body("Reason is required");

        report.setStatus("PENDING");
        report.setCreatedAt(LocalDateTime.now());

        Report savedReport = reportRepository.save(report);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReport);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {

        Optional<Report> report = reportRepository.findById(id);

        if (report.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Report not found");

        return ResponseEntity.ok(report.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody Report updatedReport) {

        Optional<Report> reportOptional =
                reportRepository.findById(id);

        if (reportOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Report not found");

        Report report = reportOptional.get();

        if (updatedReport.getReason() != null)
            report.setReason(updatedReport.getReason());

        if (updatedReport.getDescription() != null)
            report.setDescription(updatedReport.getDescription());

        if (updatedReport.getStatus() != null)
            report.setStatus(updatedReport.getStatus());

        Report savedReport = reportRepository.save(report);

        return ResponseEntity.ok(savedReport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {

        if (!reportRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Report not found");

        reportRepository.deleteById(id);

        return ResponseEntity.ok("Report deleted successfully");
    }
}