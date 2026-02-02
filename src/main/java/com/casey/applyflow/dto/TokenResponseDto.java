package com.casey.applyflow.dto;

public record TokenResponseDto(
    String accessToken,
    String tokenType,
    long expiresIn
) {
    public TokenResponseDto(String accessToken, long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
