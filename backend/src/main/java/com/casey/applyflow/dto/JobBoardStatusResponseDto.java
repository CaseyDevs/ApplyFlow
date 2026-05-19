package com.casey.applyflow.dto;

import java.time.LocalDateTime;

import com.casey.applyflow.model.enums.Status;

public record JobBoardStatusResponseDto(
    Long id,
    Long jobBoardApplicationId,
    Long userId,
    String userEmail,
    Status status,
    LocalDateTime updatedAt,
    String updatedBy
) {}
