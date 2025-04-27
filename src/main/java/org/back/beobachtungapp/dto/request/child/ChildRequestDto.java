package org.back.beobachtungapp.dto.request.child;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChildRequestDto(

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Surname cannot be blank")
        @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        String surname,

        @Email(message = "Email should be valid")
        String email,

        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Phone number must be valid and contain 7 to 15 digits"
        )
        String phoneNumber
) {
}

