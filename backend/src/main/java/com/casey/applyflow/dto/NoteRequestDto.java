package com.casey.applyflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteRequestDto(
    @NotBlank(message = "Description can not be empty")
    String description,

    @NotNull
    Long interviewId
) {}
