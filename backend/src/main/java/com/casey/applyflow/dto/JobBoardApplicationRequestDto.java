package com.casey.applyflow.dto;

import jakarta.validation.constraints.NotNull;

public record JobBoardApplicationRequestDto (
    @NotNull(message = "Application ID cannot be null")
    Long applicationId, // user application id to get the owner

    @NotNull(message = "Job Board ID cannot be null")
    Long jobBoardId
) {}
