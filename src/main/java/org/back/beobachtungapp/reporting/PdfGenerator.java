package org.back.beobachtungapp.reporting;

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
import org.back.beobachtungapp.dto.telegram.TelegramPdfJob;
import org.back.beobachtungapp.messaging.MessagingQueueManager;
import org.back.beobachtungapp.service.ChildService;
import org.springframework.stereotype.Service;

/**
 * Service for generating PDF reports for children, including title pages, companion information,
 * child information, and charts. The generated PDF can also be enqueued for sending via Telegram.
 *
 * <p>This service depends on {@link ChartGenerator} to generate chart images, {@link ChildService}
 * to fetch child data with attachments, and {@link MessagingQueueManager} to enqueue Telegram PDF
 * sending jobs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerator {

  private final ChartGenerator chartGenerator;
  private final ChildService childService;
  private final MessagingQueueManager messageService;

  /**
   * Generates a PDF report for the given child ID and companion data. The PDF includes a title
   * page, companion and child information page, and one or more pages with charts derived from the
   * child's entries. The generated PDF is also enqueued for sending via Telegram.
   *
   * @param childId the ID of the child for whom the report is generated
   * @param companionDto the data transfer object containing companion information
   * @return a byte array representing the generated PDF document
   * @throws IOException if there is an error while creating or saving the PDF
   */
  public byte[] generatePdf(Long childId, CompanionDto companionDto) throws IOException {
    try (PDDocument document = new PDDocument()) {
      ChildWithAttachments child = childService.getChildWithAttachments(childId);
      List<BufferedImage> images = chartGenerator.handleCharts(child.entries());

      // 1. Title page
      PDPage titlePage = new PDPage(PDRectangle.A4);
      document.addPage(titlePage);

      try (PDPageContentStream titleStream = new PDPageContentStream(document, titlePage)) {
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Params
        float pageWidth = PDRectangle.A4.getWidth();
        float titleFontSize = 32;
        float subtitleFontSize = 18;
        float lineSpacing = 30;

        // Headings
        String titleText = "Begleitbericht für das Kind";
        float titleWidth = boldFont.getStringWidth(titleText) / 1000 * titleFontSize;
        float titleX = (pageWidth - titleWidth) / 2;
        float titleY = 600;

        titleStream.beginText();
        titleStream.setFont(boldFont, titleFontSize);
        titleStream.newLineAtOffset(titleX, titleY);
        titleStream.showText(titleText);
        titleStream.endText();

        // Subheadings
        String[] lines = {
          "Erstellt am: " + LocalDate.now(),
          "Schulbegleiter: " + companionDto.name() + " " + companionDto.surname(),
          "Kind: " + child.name() + " " + child.surname()
        };

        float currentY = titleY - lineSpacing * 2;
        for (String line : lines) {
          float textWidth = regularFont.getStringWidth(line) / 1000 * subtitleFontSize;
          float x = (pageWidth - textWidth) / 2;

          titleStream.beginText();
          titleStream.setFont(regularFont, subtitleFontSize);
          titleStream.newLineAtOffset(x, currentY);
          titleStream.showText(line);
          titleStream.endText();

          currentY -= lineSpacing;
        }
      }

      // 2. School companion section
      PDPage infoPage = new PDPage(PDRectangle.A4);
      document.addPage(infoPage);

      // One page for two sections
      String companionInfo = formatCompanionInfo(companionDto);
      String childInfo = formatChildInfo(child);

      String combinedInfo =
          "Informationen über den Schulbegleiter\n\n"
              + companionInfo
              + "\n\nInformationen über das Kind\n\n"
              + childInfo;

      addInfoPage(document, infoPage, "Zusätzliche Informationen", combinedInfo);

      // 4. Charts
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

      messageService.enqueueTelegramPdfReportJob(
          new TelegramPdfJob(
              companionDto.tgId(), outputStream.toByteArray(), "begleitbericht.pdf"));
      return outputStream.toByteArray();
    }
  }

  /**
   * Adds an information page to the PDF document. This page includes a title and multi-line content
   * text.
   *
   * @param document the PDF document to add the page to
   * @param page the page to add information to
   * @param title the title text displayed at the top of the page
   * @param content the multi-line content text to display under the title
   * @throws IOException if there is an error while writing to the PDF content stream
   */
  private void addInfoPage(PDDocument document, PDPage page, String title, String content)
      throws IOException {
    try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
      // Main page title
      stream.beginText();
      stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
      stream.newLineAtOffset(70, 770);
      stream.showText(title);
      stream.endText();

      // Handling content with subheadings
      addFormattedMultilineText(stream, content, 70, 740);
    }
  }

  /**
   * Writes formatted multiline text to a PDF content stream. Lines matching certain section titles
   * are rendered in bold font; all other lines use the regular font.
   *
   * @param stream the PDF content stream to write to
   * @param text the multi-line string to write (lines separated by '\n')
   * @param x the x coordinate to start the text
   * @param y the y coordinate to start the text
   * @throws IOException if an error occurs writing to the content stream
   */
  private void addFormattedMultilineText(PDPageContentStream stream, String text, float x, float y)
      throws IOException {

    final float regularFontSize = 14;
    final float boldFontSize = 16;
    final float leading = regularFontSize + 6;
    final PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    final PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    String[] lines = text.split("\n");
    float currentY = y;

    for (String line : lines) {
      boolean isSectionTitle =
          line.trim().equals("Informationen über den Schulbegleiter")
              || line.trim().equals("Informationen über das Kind");

      stream.beginText();
      stream.setFont(
          isSectionTitle ? boldFont : regularFont, isSectionTitle ? boldFontSize : regularFontSize);
      stream.newLineAtOffset(x, currentY);
      stream.showText(line);
      stream.endText();

      currentY -= leading;
    }
  }

  /**
   * Formats companion information into a string suitable for display in the PDF.
   *
   * @param dto the companion data transfer object
   * @return a formatted string with the companion's name and email
   */
  private String formatCompanionInfo(CompanionDto dto) {
    return "Name: " + dto.name() + " " + dto.surname() + "\n" + "E-Mail: " + dto.email();
  }

  /**
   * Formats child information including name, birth date, contact details, notes, special needs,
   * and goals into a string for PDF display.
   *
   * @param child the child entity with attachments
   * @return a formatted multi-line string containing child's detailed information
   */
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
}
