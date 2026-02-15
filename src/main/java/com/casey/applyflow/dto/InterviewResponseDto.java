package com.casey.applyflow.dto;

import java.time.LocalDateTime;

public record InterviewResponseDto(
    Long id,
    LocalDateTime date,
    String type,
    ContactResponseDto interviewer
    // TODO: RETURN NOTES
) {}
