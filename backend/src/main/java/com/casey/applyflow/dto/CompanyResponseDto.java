package com.casey.applyflow.dto;

public record CompanyResponseDto(
    Long id,
    String name,
    Double rating
    // TODO: Add list of contact response dto's
) {}
