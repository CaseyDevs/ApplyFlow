package com.casey.applyflow.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record CompanyRequestDto (
    @NotBlank(message = "Please add the company's name.") 
    String name,
    
    @Nullable
    String location,
    
    @Nullable
    Double rating
) {}
