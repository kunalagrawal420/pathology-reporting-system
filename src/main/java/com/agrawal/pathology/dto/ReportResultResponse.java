package com.agrawal.pathology.dto;

public record ReportResultResponse(
    Long id,
    Long testId,
    String testName,
    String sectionName,
    String unit,
    String normalRange,
    String resultValue,
    String remarks,
    Integer displayOrder
) {}
