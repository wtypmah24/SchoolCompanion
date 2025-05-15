package org.back.beobachtungapp.dto.brevo;

import java.util.List;

public record BrevoEmailRequest(
    Sender sender,
    List<To> to,
    String subject,
    String htmlContent,
    String scheduledAt,
    String batchId) {

  public record Sender(String name, String email) {}

  public record To(String email, String name) {}
}
