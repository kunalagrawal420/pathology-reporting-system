package com.agrawal.pathology.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PatientRequest(
    @NotBlank String patientName,
    String sex,
    LocalDate dateOfBirth,
    String phone
) {}
