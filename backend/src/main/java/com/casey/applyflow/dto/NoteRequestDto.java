package com.casey.applyflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteRequestDto(
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 2000, message = "Description must be between 5 and 2000 characters")
    String description,

    @NotNull(message = "Interview ID is required")
    @Min(value = 1, message = "Interview ID must be greater than 0")
    Long interviewId
) {}
