package com.casey.applyflow.dto;

public record UserResponseDto(
    Long userId,
    String username,
    String email
) {}
