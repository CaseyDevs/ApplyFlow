package com.casey.applyflow.dto;

import java.time.LocalDateTime;

import com.casey.applyflow.domain.Contact;

public record InterviewResponseDto(
    LocalDateTime date,
    String type,
    Contact interviewer
    // TODO: RETURN NOTES
) {}
