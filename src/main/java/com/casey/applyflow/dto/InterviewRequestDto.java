package com.casey.applyflow.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record InterviewRequestDto (
    @NotNull(message = "Interview must have an assigned date.")
    LocalDateTime date,

    @Nullable
    String type,

    Long interviewerId

    // Add notes
) {}
