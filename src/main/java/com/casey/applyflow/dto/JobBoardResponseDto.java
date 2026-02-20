package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.domain.JobBoardMember;

public record JobBoardResponseDto(
    String title,
    Long userId,
    List<JobBoardMember> members // TODO: CHANGE TO DTO
) {}
