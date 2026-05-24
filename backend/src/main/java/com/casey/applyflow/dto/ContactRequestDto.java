package com.casey.applyflow.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactRequestDto(
    @NotBlank(message = "Contact must have a name")
    @Size(min = 2, max = 128, message = "Name must be between 2 and 128 characters")
    String name,

    @Nullable
    @Email(message = "Invalid email format")
    String email,

    @Nullable
    @Pattern(regexp = "^[+]?[0-9]{10,15}$|^$", message = "Phone number must be valid (10-15 digits, optional +)")
    String phoneNumber
) {}
