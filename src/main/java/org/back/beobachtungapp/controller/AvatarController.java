package org.back.beobachtungapp.controller;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("avatar")
@RequiredArgsConstructor
public class AvatarController {
  private final PhotoService photoService;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadPhoto(
      @CurrentCompanion CompanionDto companion, @RequestParam("file") MultipartFile file) {
    photoService.uploadCompanionAvatar(companion, file);
    return ResponseEntity.ok("Photo uploaded successfully.");
  }
}
