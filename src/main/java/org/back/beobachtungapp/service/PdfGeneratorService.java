package org.back.beobachtungapp.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.back.beobachtungapp.dto.response.child.ChildWithAttachments;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

  private final ChartService chartService;
  private final ChildService childService;

  public byte[] generatePdf(Long childId, CompanionDto companionDto) throws IOException {
    try (PDDocument document = new PDDocument()) {
      ChildWithAttachments child = childService.getChildWithAttachments(childId);
      List<BufferedImage> images = chartService.handleCharts(child.entries());

      // 1. Титульная страница
      PDPage titlePage = new PDPage(PDRectangle.A4);
      document.addPage(titlePage);

      try (PDPageContentStream titleStream = new PDPageContentStream(document, titlePage)) {
        titleStream.beginText();
        titleStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
        titleStream.newLineAtOffset(70, 750);
        titleStream.showText("Begleitbericht für das Kind");
        titleStream.endText();

        titleStream.beginText();
        titleStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
        titleStream.newLineAtOffset(70, 700);
        titleStream.showText("Erstellt am: " + LocalDate.now());
        titleStream.endText();

        titleStream.beginText();
        titleStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
        titleStream.newLineAtOffset(70, 670);
        titleStream.showText(
            "Schulbegleiter: " + companionDto.name() + " " + companionDto.surname());
        titleStream.endText();

        titleStream.beginText();
        titleStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
        titleStream.newLineAtOffset(70, 640);
        titleStream.showText("Kind: " + child.name() + " " + child.surname());
        titleStream.endText();
      }

      // 2. Информация о школьном компаньоне
      addInfoPage(
          document, "Informationen über den Schulbegleiter", formatCompanionInfo(companionDto));

      // 3. Информация о ребёнке
      addInfoPage(document, "Informationen über das Kind", formatChildInfo(child));

      // 4. Графики
      for (BufferedImage bufferedImage : images) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

        try (PDPageContentStream imageStream = new PDPageContentStream(document, page)) {
          float maxWidth = 500;
          float maxHeight = 400;
          float scale = Math.min(maxWidth / pdImage.getWidth(), maxHeight / pdImage.getHeight());
          float imageWidth = pdImage.getWidth() * scale;
          float imageHeight = pdImage.getHeight() * scale;
          float centerX = (PDRectangle.A4.getWidth() - imageWidth) / 2;
          float centerY = (PDRectangle.A4.getHeight() - imageHeight) / 2;

          imageStream.drawImage(pdImage, centerX, centerY, imageWidth, imageHeight);
        }
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      document.save(outputStream);
      return outputStream.toByteArray();
    }
  }

  private void addInfoPage(PDDocument document, String title, String content) throws IOException {
    PDPage infoPage = new PDPage(PDRectangle.A4);
    document.addPage(infoPage);

    try (PDPageContentStream stream = new PDPageContentStream(document, infoPage)) {
      // Заголовок
      stream.beginText();
      stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
      stream.newLineAtOffset(70, 750);
      stream.showText(title);
      stream.endText();

      // Многострочный текст
      addMultilineText(stream, content, 70, 720, 12);
    }
  }

  private String formatCompanionInfo(CompanionDto dto) {
    return "Name: " + dto.name() + " " + dto.surname() + "\n" + "E-Mail: " + dto.email();
  }

  private String formatChildInfo(ChildWithAttachments child) {
    StringBuilder sb = new StringBuilder();
    sb.append("Name: ")
        .append(Optional.ofNullable(child.name()).orElse("[kein Vorname]"))
        .append(" ")
        .append(Optional.ofNullable(child.surname()).orElse("[kein Nachname]"))
        .append("\n");
    sb.append("Geburtsdatum: ")
        .append(
            Optional.ofNullable(child.dateOfBirth())
                .map(LocalDate::toString)
                .orElse("[kein Geburtsdatum]"))
        .append("\n");
    sb.append("E-Mail: ")
        .append(Optional.ofNullable(child.email()).orElse("[keine E-Mail]"))
        .append("\n");
    sb.append("Telefonnummer: ")
        .append(Optional.ofNullable(child.phoneNumber()).orElse("[keine Telefonnummer]"))
        .append("\n");
    if (child.notes() != null && !child.notes().isEmpty()) {
      sb.append("\nBemerkungen:\n");
      child
          .notes()
          .forEach(
              note ->
                  sb.append("- ")
                      .append(Optional.ofNullable(note.content()).orElse("[kein Inhalt]"))
                      .append("\n"));
    }
    if (child.specialNeeds() != null && !child.specialNeeds().isEmpty()) {
      sb.append("\nBesondere Bedürfnisse:\n");
      child
          .specialNeeds()
          .forEach(
              sn ->
                  sb.append("- ")
                      .append(Optional.ofNullable(sn.description()).orElse("[keine Beschreibung]"))
                      .append("\n"));
    }
    if (child.goals() != null && !child.goals().isEmpty()) {
      sb.append("\nZiele:\n");
      child
          .goals()
          .forEach(
              goal ->
                  sb.append("- ")
                      .append(
                          Optional.ofNullable(goal.description()).orElse("[keine Beschreibung]"))
                      .append("\n"));
    }

    return sb.toString();
  }

  // Многострочный вывод текста (простая реализация)
  private void addMultilineText(
      PDPageContentStream stream, String text, float x, float y, int fontSize) throws IOException {

    final float leading = fontSize + 4;
    final PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    String[] lines = text.split("\n");
    float currentY = y;

    for (String line : lines) {
      stream.beginText();
      stream.setFont(font, fontSize);
      stream.newLineAtOffset(x, currentY);
      stream.showText(line);
      stream.endText();
      currentY -= leading;
    }
  }
}
