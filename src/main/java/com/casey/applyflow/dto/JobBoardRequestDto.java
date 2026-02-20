package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.domain.JobBoardMember;

import jakarta.annotation.Nullable;

public record JobBoardRequestDto(
    @Nullable
    String title,

    Long userId,

    @Nullable
    List<JobBoardMember> members
) {}
