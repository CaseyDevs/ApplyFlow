package com.casey.applyflow.dto;

public record ApplicationResponseDto(
    String title,
    String url,
    String status
    // TODO: ADD NOTES, APPLICANTS (TRACK WITH FRIENDS)
) {}
