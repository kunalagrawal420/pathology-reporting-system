package com.agrawal.pathology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestDefinitionRequest(
    @NotBlank String testName,
    @NotNull Long sectionId,
    String unit,
    String normalRange,
    Integer displayOrder,
    String resultType,
    Boolean active
) {}
