package com.casey.applyflow.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequestDto(
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 128, message = "Company name must be between 2 and 128 characters")
    String name,
    
    @Nullable
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating cannot exceed 5")
    Double rating
) {}
