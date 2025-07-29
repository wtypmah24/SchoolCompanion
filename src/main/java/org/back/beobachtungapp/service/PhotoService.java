package org.back.beobachtungapp.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.AvatarProperties;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
  private final CompanionDao companionDao;

  private final AvatarProperties avatarProperties;

  @Transactional
  public void uploadAvatar(CompanionDto companionDto, MultipartFile file) {
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
      Path filePath = Paths.get(avatarProperties.getUpload_dir(), filename);
      Files.createDirectories(filePath.getParent());

      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new IllegalArgumentException("Invalid image file.");
      }

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
      log.info("Uploaded image {}", filename);
      companionDao.addAvatar(companionDto.id(), filename);

    } catch (IllegalArgumentException e) {
      throw e;
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to process and save the image.", e);
    }
  }

  private String getFileExtension(String filename) {
    int index = filename.lastIndexOf('.');
    return index > 0 ? filename.substring(index + 1) : "";
  }
}
