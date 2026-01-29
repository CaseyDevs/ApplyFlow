package com.casey.applyflow.dto;

import org.hibernate.validator.constraints.URL;
import com.casey.applyflow.domain.enums.Status;
import jakarta.annotation.Nullable;

public record UpdateApplicationFieldRequestDto(
    String title,
    @Nullable @URL String url,
    Long companyId,
    Long interviewId,
    Status status    
) 
{}
