package org.back.beobachtungapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.request.companion.LoginRequest;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.dto.update.companion.UpdatePasswordDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.service.CompanionService;
import org.back.beobachtungapp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(
    name = "Authentication and Registration",
    description = "Operations for companion registration and login")
public class CompanionController {
  private final CompanionService companionService;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  @Autowired
  public CompanionController(
      CompanionService companionService,
      AuthenticationManager authenticationManager,
      TokenService tokenService) {
    this.companionService = companionService;
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @Operation(
      summary = "Register a new companion",
      description = "Registers a new companion in the system.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Companion successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data")
      })
  @PostMapping("/register")
  public ResponseEntity<Companion> register(@RequestBody CompanionRequestDto companion) {
    return ResponseEntity.status(HttpStatus.CREATED).body(companionService.save(companion));
  }

  @Operation(
      summary = "Login for an existing companion",
      description =
          "Logs in a companion using email and password and returns an authentication token.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully logged in and token generated"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password")
      })
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

  @Operation(
      summary = "Update companion",
      description = "Update companion record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Companion updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("profile")
  public ResponseEntity<CompanionDto> update(
      @Parameter(description = "Companion details to be updated") @RequestBody
          CompanionUpdateDto updateDto,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(companionService.update(updateDto, companion));
  }

  @Operation(
      summary = "Change password",
      description = "Change the password of the currently authenticated companion.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("change-password")
  public ResponseEntity<Void> updatePassword(
      @RequestBody UpdatePasswordDto dto, @CurrentCompanion CompanionDto companionDto) {
    companionService.updatePassword(dto, companionDto);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Delete account",
      description = "Permanently delete the authenticated companion's account.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("delete-account")
  public ResponseEntity<Void> deleteAccount(@CurrentCompanion CompanionDto companionDto) {
    companionService.deleteAccount(companionDto);
    return ResponseEntity.ok().build();
  }
}
