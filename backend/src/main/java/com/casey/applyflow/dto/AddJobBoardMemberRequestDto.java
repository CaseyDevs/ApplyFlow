package com.casey.applyflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddJobBoardMemberRequestDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {
}
