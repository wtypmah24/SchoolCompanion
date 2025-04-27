package org.back.beobachtungapp.dto.response.child;

public record ChildResponseDto(
        String name,
        String surname,
        String email,
        String phoneNumber
) {
}
