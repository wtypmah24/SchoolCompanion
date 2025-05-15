package org.back.beobachtungapp.message;

public record DelayedTgMessage(String chatId, String message, Long eventId) {}
