package com.casey.applyflow.dto;

import com.casey.applyflow.domain.enums.Role;

public record JobBoardMemberDto(
    Long id,
    UserResponseDto user,
    Role role
) {}
