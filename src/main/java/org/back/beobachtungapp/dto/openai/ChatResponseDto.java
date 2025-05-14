package org.back.beobachtungapp.dto.openai;

public record ChatResponseDto(String id, String thread_id, String role, String message) {}
