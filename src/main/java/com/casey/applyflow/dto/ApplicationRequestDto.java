package com.casey.applyflow.dto;

import org.hibernate.validator.constraints.URL;

import com.casey.applyflow.domain.enums.Status;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRequestDto(
    @NotBlank String title,
    @NotBlank @URL String url,
    @NotNull Long companyId,
    @Nullable Long interviewId,
    @NotNull Status status    
) {}
