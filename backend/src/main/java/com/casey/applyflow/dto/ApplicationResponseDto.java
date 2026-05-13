package com.casey.applyflow.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.casey.applyflow.model.enums.Status;

public record ApplicationResponseDto(
    Long id,
    String title,
    String url,
    Status status,
    Long companyId,
    List<Long> interviewIds,
    LocalDateTime createdAt
) {}
