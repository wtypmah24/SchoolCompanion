package org.back.beobachtungapp.dto.message;

public record DelayedTgMessage(String chatId, String message, Long eventId) {}
