package org.back.beobachtungapp.dto.telegram;

public record TelegramPdfJob(String telegramId, byte[] pdfData, String fileName) {}
