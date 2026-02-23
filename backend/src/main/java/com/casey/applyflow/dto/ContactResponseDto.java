package com.casey.applyflow.dto;

public record ContactResponseDto(
    Long id,
    String name,
    String email,
    String phoneNumber
    // TODO: return interviews
) {
    
}
