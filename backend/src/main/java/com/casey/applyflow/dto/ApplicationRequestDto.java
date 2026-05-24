package com.casey.applyflow.dto;

import org.hibernate.validator.constraints.URL;

import com.casey.applyflow.model.enums.Status;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequestDto(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters")
    String title,
    
    @NotBlank(message = "URL is required")
    @URL(message = "URL must be valid")
    String url,

    @Nullable
    @Size(max = 256, message = "Location cannot exceed 256 characters")
    String location,

    @NotNull(message = "Company ID is required")
    @Min(value = 1, message = "Company ID must be greater than 0")
    Long companyId,
    
    @Nullable
    @Min(value = 1, message = "Interview ID must be greater than 0")
    Long interviewId,

    @NotNull(message = "Status is required")
    Status status    
) {}
