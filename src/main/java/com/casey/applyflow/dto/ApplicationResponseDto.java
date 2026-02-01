package com.casey.applyflow.dto;

import com.casey.applyflow.domain.enums.Status;

public record ApplicationResponseDto(
    Long id,
    String title,
    String url,
    Status status,
    Long companyId,
    Long interviewId
) {}
