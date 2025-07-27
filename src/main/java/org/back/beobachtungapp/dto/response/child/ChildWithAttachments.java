package org.back.beobachtungapp.dto.response.child;

import java.time.LocalDate;
import java.util.List;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;

public record ChildWithAttachments(
    Long id,
    String name,
    String surname,
    String email,
    String phoneNumber,
    LocalDate dateOfBirth,
    boolean active,
    List<NoteResponseDto> notes,
    List<SpecialNeedResponseDto> specialNeeds,
    List<GoalResponseDto> goals,
    List<EventResponseDto> events,
    List<MonitoringEntryResponseDto> entries) {}
