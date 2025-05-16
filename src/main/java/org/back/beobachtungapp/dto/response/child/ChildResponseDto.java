package org.back.beobachtungapp.dto.response.child;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@SuppressFBWarnings
@Schema(description = "Response payload for a child object")
public record ChildResponseDto(
    @Schema(description = "Unique identifier of the child", example = "1") Long id,
    @Schema(description = "First name of the child", example = "John") String name,
    @Schema(description = "Surname of the child", example = "Doe") String surname,
    @Schema(description = "Email address of the child", example = "john.doe@example.com")
        String email,
    @Schema(description = "Phone number of the child", example = "+1234567890") String phoneNumber,
    @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @Schema(description = "Date of birth of the child", example = "18-02-2012")
        LocalDate dateOfBirth) {}
