package org.back.beobachtungapp.dto.message;

public record TelegramMessage(String chatId, String message, Long entityId) {}
