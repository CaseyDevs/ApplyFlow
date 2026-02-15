package com.casey.applyflow.dto;

import org.hibernate.validator.constraints.URL;
import com.casey.applyflow.domain.enums.Status;
import jakarta.annotation.Nullable;

public record UpdateApplicationFieldRequestDto(
    @Nullable
    String title,
    
    @Nullable @URL String 
    url,

    @Nullable
    Long companyId,
    
    @Nullable
    java.util.List<Long> interviewIds,

    @Nullable
    Status status    
) 
{}
