package com.agrawal.pathology.dto;

import jakarta.validation.constraints.NotBlank;

public record TestSectionRequest(
    @NotBlank String name,
    Integer displayOrder,
    Boolean active
) {}
