package com.agrawal.pathology.controller;

import com.agrawal.pathology.dto.*;
import com.agrawal.pathology.entity.ReportStatus;
import com.agrawal.pathology.service.PdfReportService;
import com.agrawal.pathology.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;
    private final PdfReportService pdfReportService;

    public ReportController(ReportService service, PdfReportService pdfReportService) { this.service = service;
        this.pdfReportService = pdfReportService;
    }

    @PostMapping
    public ReportResponse create(@Valid @RequestBody CreateReportRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ReportResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ReportResponse findById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/patient/{patientId}")
    public List<ReportResponse> findByPatient(@PathVariable Long patientId) {
        return service.findByPatient(patientId);
    }

    @PatchMapping("/{id}/status")
    public ReportResponse updateStatus(@PathVariable Long id,
                                       @Valid @RequestBody UpdateReportStatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/{reportId}/results")
    public ReportResponse saveResults(@PathVariable Long reportId,
                                      @Valid @RequestBody SaveResultsRequest request) {
        return service.saveResults(reportId, request);
    }

    @PutMapping("/{reportId}/results/{resultId}")
    public ReportResponse updateResult(@PathVariable Long reportId,
                                       @PathVariable Long resultId,
                                       @Valid @RequestBody ResultRequest request) {
        return service.updateResult(reportId, resultId, request);
    }

    @DeleteMapping("/{reportId}/results/{resultId}")
    public void deleteResult(@PathVariable Long reportId, @PathVariable Long resultId) {
        service.deleteResult(reportId, resultId);
    }

    @GetMapping("/{id}/pdf")
    public org.springframework.http.ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id) {

        byte[] pdf = pdfReportService.generatePdf(id);

        return org.springframework.http.ResponseEntity.ok()
                .header(
                        org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=pathology-report-" + id + ".pdf"
                )
                .header(
                        org.springframework.http.HttpHeaders.CONTENT_TYPE,
                        "application/pdf"
                )
                .body(pdf);
    }
}
