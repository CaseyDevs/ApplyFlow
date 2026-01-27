package com.casey.applyflow.dto;

import com.casey.applyflow.domain.enums.Status;

public record ApplicationResponseDto(
    String title,
    String url,
    Status status
    // TODO: ADD NOTES, APPLICANTS (TRACK WITH FRIENDS)
) {}
