package com.casey.applyflow.dto;

import com.casey.applyflow.domain.Company;
import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.enums.Status;

public record ApplicationResponseDto(
    Long id,
    String title,
    String url,
    Status status,
    Company company,
    Interview interview
) {}
