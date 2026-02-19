package com.casey.applyflow.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactRequestDto(
    @NotBlank(message = "Contact must have a name.")
    String name,

    @Nullable 
    String email,

    @Nullable 
    String phoneNumber
) {}
