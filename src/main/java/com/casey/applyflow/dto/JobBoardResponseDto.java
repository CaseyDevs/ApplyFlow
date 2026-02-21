package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.domain.JobBoardMember;

public record JobBoardResponseDto(
    Long id,
    String title,
    Long ownerId,
    List<JobBoardMember> members // TODO: CHANGE TO DTO
) {}
