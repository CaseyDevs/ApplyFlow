package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.model.JobBoardMember;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobBoardRequestDto(
    @NotBlank
    String title,

    @NotNull
    Long userId,

    @Nullable
    List<JobBoardMember> members
) {}
