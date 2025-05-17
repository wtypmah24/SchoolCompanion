package org.back.beobachtungapp.dto.response.child;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;

public record ChildWithAttachments(
    @Schema(description = "Unique identifier of the child", example = "1") Long id,
    @Schema(description = "First name of the child", example = "John") String name,
    @Schema(description = "Surname of the child", example = "Doe") String surname,
    @Schema(description = "Email address of the child", example = "john.doe@example.com")
        String email,
    @Schema(description = "Phone number of the child", example = "+1234567890") String phoneNumber,
    @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @Schema(description = "Date of birth of the child", example = "18-02-2012")
        LocalDate dateOfBirth,
    @Schema(description = "Notes associated with the child") Set<NoteResponseDto> notes,
    @Schema(description = "Special needs of the child") Set<SpecialNeedResponseDto> specialNeeds,
    @Schema(description = "Goals assigned to the child") Set<GoalResponseDto> goals,
    @Schema(description = "Events assigned to the child") Set<EventResponseDto> events,
    @Schema(description = "Monitoring entries assigned to the child")
        Set<MonitoringEntryResponseDto> entries) {}
