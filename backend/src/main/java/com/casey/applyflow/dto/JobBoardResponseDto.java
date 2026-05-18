package com.casey.applyflow.dto;

import java.util.List;

public record JobBoardResponseDto(
    Long id,
    String title,
    Long ownerId,
    List<JobBoardMemberDto> members, 
    List<JobBoardApplicationResponseDto> applications
) {}
