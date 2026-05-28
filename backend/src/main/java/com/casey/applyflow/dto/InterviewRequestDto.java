package com.casey.applyflow.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InterviewRequestDto(
    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    LocalDateTime date,

    @Nullable
    @NotBlank(message = "Interview type cannot be blank")
    @Size(min = 2, max = 32, message = "Type must be between 2 and 32 characters")
    String type,

    @Nullable
    @Min(value = 1, message = "Interviewer ID must be greater than 0")
    Long interviewerId
) {}
