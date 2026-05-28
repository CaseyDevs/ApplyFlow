package com.casey.applyflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record JobBoardApplicationRequestDto(
    @NotNull(message = "Application ID is required")
    @Min(value = 1, message = "Application ID must be greater than 0")
    Long applicationId,

    @NotNull(message = "Job Board ID is required")
    @Min(value = 1, message = "Job Board ID must be greater than 0")
    Long jobBoardId
) {}
