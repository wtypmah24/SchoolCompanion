package org.back.beobachtungapp.dto.response.child;

import java.time.LocalDate;

public record ChildResponseDto(
    Long id,
    String name,
    String surname,
    String email,
    String phoneNumber,
    LocalDate dateOfBirth,
    boolean active) {}
