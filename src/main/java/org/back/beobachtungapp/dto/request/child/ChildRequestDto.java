package org.back.beobachtungapp.dto.request.child;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChildRequestDto(
    @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        @Schema(
            description = "First name of the child",
            example = "John",
            minLength = 2,
            maxLength = 50)
        String name,
    @NotBlank(message = "Surname cannot be blank")
        @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        @Schema(
            description = "Last name of the child",
            example = "Doe",
            minLength = 2,
            maxLength = 50)
        String surname,
    @Email(message = "Email should be valid")
        @Schema(description = "Email address of the child", example = "john.doe@example.com")
        String email,
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be valid and contain 7 to 15 digits")
        @Schema(
            description = "Phone number of the child",
            example = "+1234567890",
            pattern = "^\\+?[0-9]{7,15}$")
        String phoneNumber) {}
