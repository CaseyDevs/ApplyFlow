package com.casey.applyflow.dto;

import com.casey.applyflow.model.enums.Role;

public record JobBoardMemberDto(
    Long id,
    UserResponseDto user,
    Role role
) {}
