package com.agrawal.pathology.service;

import com.agrawal.pathology.dto.*;
import com.agrawal.pathology.entity.*;
import com.agrawal.pathology.exception.BadRequestException;
import com.agrawal.pathology.exception.NotFoundException;
import com.agrawal.pathology.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final PatientRepository patientRepository;
    private final TestDefinitionRepository testRepository;
    private final ReportResultRepository resultRepository;

    public ReportService(ReportRepository reportRepository,
                         PatientRepository patientRepository,
                         TestDefinitionRepository testRepository,
                         ReportResultRepository resultRepository) {
        this.reportRepository = reportRepository;
        this.patientRepository = patientRepository;
        this.testRepository = testRepository;
        this.resultRepository = resultRepository;
    }

    @Transactional
    public ReportResponse create(CreateReportRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new NotFoundException("Patient not found: " + request.patientId()));

        reportRepository.findByReferenceNo(request.referenceNo())
            .ifPresent(r -> { throw new BadRequestException("Reference number already exists"); });

        Report report = new Report();
        report.setPatient(patient);
        report.setReferenceNo(request.referenceNo());
        report.setReferredBy(request.referredBy());
        report.setReportDate(request.reportDate() == null ? LocalDate.now() : request.reportDate());
        report.setStatus(request.status() == null ? ReportStatus.DRAFT : request.status());

        return toResponse(reportRepository.save(report));
    }

    public List<ReportResponse> findAll() {
        return reportRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReportResponse findById(Long id) {
        return toResponse(report(id));
    }

    public List<ReportResponse> findByPatient(Long patientId) {
        patientRepository.findById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found: " + patientId));
        return reportRepository.findByPatientIdOrderByReportDateDesc(patientId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReportResponse updateStatus(Long id, ReportStatus status) {
        Report report = report(id);
        report.setStatus(status);
        return toResponse(reportRepository.save(report));
    }

    @Transactional
    public void delete(Long id) {
        Report report = report(id);
        resultRepository.findByReportId(id).forEach(resultRepository::delete);
        reportRepository.delete(report);
    }

    @Transactional
    public ReportResponse saveResults(Long reportId, SaveResultsRequest request) {
        Report report = report(idOrThrow(reportId));

        if (report.getStatus() == ReportStatus.FINAL) {
            throw new BadRequestException("Final reports cannot be edited");
        }

        for (ResultRequest item : request.results()) {
            TestDefinition test = testRepository.findById(item.testId())
                .orElseThrow(() -> new NotFoundException("Test not found: " + item.testId()));

            resultRepository.findByReportIdAndTestId(reportId, item.testId())
                .ifPresent(existing -> {
                    existing.setResultValue(item.resultValue());
                    existing.setRemarks(item.remarks());
                    resultRepository.save(existing);
                });

            if (resultRepository.findByReportIdAndTestId(reportId, item.testId()).isEmpty()) {
                ReportResult result = new ReportResult();
                result.setReport(report);
                result.setTest(test);
                result.setResultValue(item.resultValue());
                result.setRemarks(item.remarks());
                resultRepository.save(result);
            }
        }

        return toResponse(report);
    }

    @Transactional
    public ReportResponse updateResult(Long reportId, Long resultId, ResultRequest request) {
        Report report = report(idOrThrow(reportId));
        if (report.getStatus() == ReportStatus.FINAL) {
            throw new BadRequestException("Final reports cannot be edited");
        }

        ReportResult result = resultRepository.findById(resultId)
            .orElseThrow(() -> new NotFoundException("Result not found: " + resultId));

        if (!result.getReport().getId().equals(reportId)) {
            throw new BadRequestException("Result does not belong to this report");
        }

        TestDefinition test = testRepository.findById(request.testId())
            .orElseThrow(() -> new NotFoundException("Test not found: " + request.testId()));

        result.setTest(test);
        result.setResultValue(request.resultValue());
        result.setRemarks(request.remarks());
        resultRepository.save(result);

        return toResponse(report);
    }

    @Transactional
    public void deleteResult(Long reportId, Long resultId) {
        Report report = report(idOrThrow(reportId));
        if (report.getStatus() == ReportStatus.FINAL) {
            throw new BadRequestException("Final reports cannot be edited");
        }

        ReportResult result = resultRepository.findById(resultId)
            .orElseThrow(() -> new NotFoundException("Result not found: " + resultId));

        if (!result.getReport().getId().equals(reportId)) {
            throw new BadRequestException("Result does not belong to this report");
        }

        resultRepository.delete(result);
    }

    private Report report(Long id) {
        return reportRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Report not found: " + id));
    }

    private Long idOrThrow(Long id) {
        if (id == null) throw new NotFoundException("Report id is required");
        return id;
    }

    private ReportResponse toResponse(Report r) {

        List<ReportResultResponse> results =
                resultRepository
                        .findByReportIdOrderBySectionAndTest(r.getId())
                        .stream()
                        .map(x -> new ReportResultResponse(
                                x.getId(),
                                x.getTest().getId(),
                                x.getTest().getTestName(),
                                x.getTest().getSection().getName(),
                                x.getTest().getUnit(),
                                x.getTest().getNormalRange(),
                                x.getResultValue(),
                                x.getRemarks(),
                                x.getTest().getDisplayOrder()
                        ))
                        .toList();

        Patient p = r.getPatient();

        return new ReportResponse(
                r.getId(),
                p.getId(),
                p.getPatientName(),
                p.getSex(),
                p.getDateOfBirth(),
                p.getPhone(),
                r.getReferenceNo(),
                r.getReferredBy(),
                r.getReportDate(),
                r.getStatus(),
                results
        );
    }}
