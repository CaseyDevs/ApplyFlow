package com.casey.applyflow.dto;

public record CompanyResponseDto(
    String name,
    String location,
    Double rating
    // TODO: Add list of contact response dto's
) {}
