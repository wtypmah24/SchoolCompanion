package org.back.beobachtungapp.service;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.feign.BrevoClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrevoService {
  private final BrevoClient brevoClient;

  public void scheduleEmail(BrevoEmailRequest request) {
    brevoClient.sendEmail(request);
  }

  public void cancelScheduledEmail(String batchIdOrMessageId) {
    brevoClient.cancelEmail(batchIdOrMessageId);
  }
}
