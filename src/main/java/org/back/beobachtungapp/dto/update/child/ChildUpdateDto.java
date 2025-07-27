package org.back.beobachtungapp.dto.update.child;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ChildUpdateDto(
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name,
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        String surname,
    @Email(message = "Email should be valid") String email,
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be valid and contain 7 to 15 digits")
        String phoneNumber,
    @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
    boolean active) {}
