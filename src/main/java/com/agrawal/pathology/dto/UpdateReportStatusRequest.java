package com.agrawal.pathology.dto;

import com.agrawal.pathology.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReportStatusRequest(@NotNull ReportStatus status) {}
