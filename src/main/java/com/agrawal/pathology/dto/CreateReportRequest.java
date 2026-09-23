package com.agrawal.pathology.dto;

import com.agrawal.pathology.entity.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateReportRequest(
    @NotNull Long patientId,
    @NotBlank String referenceNo,
    String referredBy,
    LocalDate reportDate,
    ReportStatus status
) {}
