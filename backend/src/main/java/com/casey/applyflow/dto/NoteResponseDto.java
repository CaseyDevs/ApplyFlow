package com.casey.applyflow.dto;

public record NoteResponseDto(
    Long noteId,
    String description,
    Long interviewId    
) {}
