package com.agrawal.pathology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResultRequest(
    @NotNull Long testId,
    @NotBlank String resultValue,
    String remarks
) {}
