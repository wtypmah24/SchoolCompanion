package org.back.beobachtungapp.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.PhotoProperties;
import org.back.beobachtungapp.dao.ChildDao;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.dto.request.child.DeletePhotoRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildPhotoResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

  private final CompanionDao companionDao;
  private final ChildDao childDao;
  private final PhotoProperties photoProperties;

  @Transactional
  public void uploadCompanionAvatar(CompanionDto companionDto, MultipartFile file) {
    String filename = savePhoto(file, photoProperties.getAvatar_dir(), companionDto.id());
    if (companionDto.avatarId() != null && !companionDto.avatarId().isEmpty()) {
      removeCompanionAvatarFromUploads(companionDto.avatarId(), photoProperties.getAvatar_dir());
    }
    companionDao.addAvatarRefToCompanion(companionDto.id(), filename);
  }

  @Transactional
  public void removeAvatar(CompanionDto companionDto) {
    companionDao.removeAvatarRefFromCompanion(companionDto.id());
    removeCompanionAvatarFromUploads(companionDto.avatarId(), photoProperties.getAvatar_dir());
  }

  private void removeCompanionAvatarFromUploads(String photoPath, String photoDirectory) {
    Path absolutePath =
        Paths.get(photoDirectory).resolve(photoPath.replaceFirst("upload/", "")).toAbsolutePath();

    log.info("Removing avatar from: {}", absolutePath);

    try {
      Files.deleteIfExists(absolutePath);
    } catch (IOException e) {
      log.error("Failed to delete avatar: {}", e.getMessage(), e);
    }
  }

  @Transactional
  public void removeChildPhoto(DeletePhotoRequestDto dto) {
    String photoId = dto.photoId();
    childDao.removePhoto(photoId);
    removeCompanionAvatarFromUploads(photoId, photoProperties.getChild_photo());
  }

  @Transactional
  public void uploadChildPhoto(Long childId, MultipartFile file, String description) {
    String filename = savePhoto(file, photoProperties.getChild_photo(), childId);
    childDao.savePhoto(childId, filename, description);
  }

  private String savePhoto(MultipartFile file, String path, Long id) {
    try {
      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null) {
        throw new IllegalArgumentException("Filename is missing.");
      }

      String extension = getFileExtension(originalFilename);
      if (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
        throw new IllegalArgumentException("Only JPG images are supported.");
      }

      String filename = UUID.randomUUID() + "_" + originalFilename;

      Path dirPath = Paths.get(path, String.valueOf(id));
      Path filePath = dirPath.resolve(filename);
      Files.createDirectories(filePath.getParent());

      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new IllegalArgumentException("Invalid image file.");
      }
      image = correctOrientation(file, image);

      try (OutputStream os = Files.newOutputStream(filePath);
          ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.7f);

        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
      }
      return "upload/" + id + "/" + filename;
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to process and save the image.", e);
    }
  }

  private BufferedImage correctOrientation(MultipartFile file, BufferedImage image) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
      ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
      if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
        int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        switch (orientation) {
          case 6: // 90 CW
            return rotateImage(image, 90);
          case 3: // 180
            return rotateImage(image, 180);
          case 8: // 270 CW
            return rotateImage(image, 270);
          default:
            return image;
        }
      }
    } catch (Exception e) {
      log.warn("Failed to read exif data", e);
    }
    return image;
  }

  public List<ChildPhotoResponseDto> getPhotos(Long childId) {
    return childDao.getPhotosByChildId(childId);
  }

  private BufferedImage rotateImage(BufferedImage image, int angle) {
    int width = image.getWidth();
    int height = image.getHeight();

    int newWidth = angle == 90 || angle == 270 ? height : width;
    int newHeight = angle == 90 || angle == 270 ? width : height;

    BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = rotatedImage.createGraphics();

    // Чтобы сохранить качество
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    switch (angle) {
      case 90:
        g2d.translate(newWidth, 0);
        g2d.rotate(Math.toRadians(90));
        break;
      case 180:
        g2d.translate(newWidth, newHeight);
        g2d.rotate(Math.toRadians(180));
        break;
      case 270:
        g2d.translate(0, newHeight);
        g2d.rotate(Math.toRadians(270));
        break;
      default:
        // 0 градусов — без изменений
        break;
    }

    g2d.drawImage(image, 0, 0, null);
    g2d.dispose();

    return rotatedImage;
  }

  private String getFileExtension(String filename) {
    int index = filename.lastIndexOf('.');
    return index > 0 ? filename.substring(index + 1) : "";
  }
}
