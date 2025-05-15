package org.back.beobachtungapp.controller;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.service.BrevoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

  private final BrevoService emailService;

  @PostMapping("/send")
  public ResponseEntity<String> sendEmail(@RequestBody BrevoEmailRequest request) {
    emailService.scheduleEmail(request);
    return ResponseEntity.ok("Email scheduled");
  }
}
