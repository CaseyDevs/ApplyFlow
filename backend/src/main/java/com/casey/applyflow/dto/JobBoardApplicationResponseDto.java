package com.casey.applyflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public record JobBoardApplicationResponseDto(
    Long id,
    ApplicationResponseDto application,
    LocalDateTime addedAt,
    String addedByEmail,  
    List<JobBoardStatusResponseDto> statusList
) {}