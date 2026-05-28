package com.casey.applyflow.dto;

import java.time.LocalDateTime;

import com.casey.applyflow.model.enums.Status;

public record ApplicationResponseDto(
    Long id,
    String title,
    String url,
    Status status,
    String location,
    Long companyId,
    LocalDateTime createdAt
) {}
