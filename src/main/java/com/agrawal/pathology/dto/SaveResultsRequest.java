package com.agrawal.pathology.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveResultsRequest(
    @NotEmpty List<@Valid ResultRequest> results
) {}
