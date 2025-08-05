package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.DeletePhotoRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildPhotoResponseDto;
import org.back.beobachtungapp.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("child")
@RequiredArgsConstructor
public class ChildPhotosController {
  private final PhotoService photoService;

  @PostMapping("{childId}/upload")
  public ResponseEntity<String> uploadPhotos(
      @PathVariable Long childId,
      @RequestParam("file") MultipartFile file,
      @RequestParam String description) {
    photoService.uploadChildPhoto(childId, file, description);
    return ResponseEntity.ok("Photos uploaded successfully.");
  }

  @GetMapping("{childId}/photos")
  public ResponseEntity<List<ChildPhotoResponseDto>> getPhotos(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(photoService.getPhotos(childId));
  }

  @DeleteMapping("photo")
  public ResponseEntity<String> deletePhoto(@RequestBody DeletePhotoRequestDto dto) {
    photoService.removeChildPhoto(dto);
    return ResponseEntity.ok("Photos uploaded successfully.");
  }
}
