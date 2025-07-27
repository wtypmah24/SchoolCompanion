package org.back.beobachtungapp.controller;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.auth.TokenService;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.request.companion.LoginRequest;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.UpdatePasswordDto;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {
  private final CompanionService companionService;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody CompanionRequestDto companion) {
    companionService.save(companion);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

    String token = tokenService.generateToken(authentication);
    return ResponseEntity.ok(token);
  }

  @GetMapping("/me")
  public ResponseEntity<CompanionDto> me(@CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body((companionDto));
  }

  @PostMapping("change-password")
  public ResponseEntity<Void> updatePassword(
      @RequestBody UpdatePasswordDto dto, @CurrentCompanion CompanionDto companionDto) {
    companionService.updatePassword(dto, companionDto);
    return ResponseEntity.ok().build();
  }
}
