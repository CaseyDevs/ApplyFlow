package com.casey.applyflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewResponseDto(
    Long id,
    LocalDateTime date,
    String type,
    ContactResponseDto interviewer,
    List<NoteResponseDto> notes
) {}
