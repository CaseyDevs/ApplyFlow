package com.casey.applyflow.dto;

import java.util.List;

import com.casey.applyflow.domain.JobBoardMember;

public record JobBoardStatsDto(
    JobBoardMember owner,
    int applicationCount, 
    List<JobBoardMember> members
) {}
