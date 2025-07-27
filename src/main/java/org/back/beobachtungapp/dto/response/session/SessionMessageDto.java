package org.back.beobachtungapp.dto.response.session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionMessageDto(
    WorkSessionResponseDto workSessionResponseDto, long companionId, String tgId, String email) {}
