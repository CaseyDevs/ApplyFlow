package com.casey.applyflow.dto;

public record InvitationDetailsDto(
    Long jobBoardId,
    String jobBoardTitle,
    String inviterName,
    String inviteeEmail
) {}
