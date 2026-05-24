package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.model.JobBoardMember;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobBoardRequestDto(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 64, message = "Title must be between 3 and 64 characters")
    String title,

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be greater than 0")
    Long userId,

    @Nullable
    List<JobBoardMember> members
) {}
