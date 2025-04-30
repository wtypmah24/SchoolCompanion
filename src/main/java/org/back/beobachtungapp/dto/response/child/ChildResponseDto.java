package org.back.beobachtungapp.dto.response.child;

public record ChildResponseDto(
    Long id, String name, String surname, String email, String phoneNumber) {}
