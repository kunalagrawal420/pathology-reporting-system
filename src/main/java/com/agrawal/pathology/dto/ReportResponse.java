package com.agrawal.pathology.dto;

import com.agrawal.pathology.entity.ReportStatus;
import java.time.LocalDate;
import java.util.List;

public record ReportResponse(
    Long id,
    Long patientId,
    String patientName,
    String sex,
    LocalDate dateOfBirth,
    String phone,
    String referenceNo,
    String referredBy,
    LocalDate reportDate,
    ReportStatus status,
    List<ReportResultResponse> results
) {}
