package org.back.beobachtungapp.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.openai.*;
import org.back.beobachtungapp.dto.response.child.ChildWithAttachments;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.feign.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAiService {
  @Value("${openai.assistant.id}")
  private String assistantId;

  private final OpenAiClient openAiClient;
  private final CompanionService companionService;
  private final ChildService childService;

  @Autowired
  public OpenAiService(
      OpenAiClient openAiClient, CompanionService companionService, ChildService childService) {
    this.openAiClient = openAiClient;
    this.companionService = companionService;
    this.childService = childService;
  }

  public List<ChatResponseDto> ask(
      ChatRequest promptDto, CompanionDto companionDto, Long childId, String threadId) {
    String prompt = promptDto.message();
    boolean isNewThread = (threadId == null || threadId.isBlank());

    if (isNewThread) {
      return askInNewThread(prompt, companionDto, childId);
    } else {
      return askInExistingThread(prompt, companionDto, threadId);
    }
  }

  private List<ChatResponseDto> askInNewThread(
      String prompt, CompanionDto companionDto, Long childId) {
    String threadId = openAiClient.createThread().id();
    log.info("Created new threadId: {}", threadId);

    String context = buildContextForCompanion(childId);
    log.info("Companion context: {}", context);

    // Добавляем контекст как user-сообщение, т.к. system не поддерживается
    openAiClient.addMessage(threadId, new MessageRequest("user", context));
    openAiClient.addMessage(threadId, new MessageRequest("user", prompt));

    RunResponse run = openAiClient.startRun(threadId, new RunRequest(assistantId));
    log.info("run: {}", run);
    RunResponse completedRun = checkRunStatusAsync(threadId, run.id()).join();

    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    companionService.addChatIdToCompanion(companionDto.id(), threadId);
    return getMessagesForFrontend(messages);
  }

  private List<ChatResponseDto> askInExistingThread(
      String prompt, CompanionDto companionDto, String threadId) {
    log.info("Using existing threadId: {}", threadId);
    openAiClient.addMessage(threadId, new MessageRequest("user", prompt));

    RunResponse run = openAiClient.startRun(threadId, new RunRequest(assistantId));
    log.info("run: {}", run);
    RunResponse completedRun = checkRunStatusAsync(threadId, run.id()).join();

    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    return getMessagesForFrontend(messages);
  }

  public List<ChatResponseDto> getChatByThreadId(String threadId) {
    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    return getMessagesForFrontend(messages);
  }

  private String buildContextForCompanion(Long childId) {
    ChildWithAttachments child = childService.getChildWithAttachments(childId);
    int age = calculateAge(child.dateOfBirth());

    return """
      Du bist der Assistent einer Begleitperson für ein Kind. Hier sind die Informationen über das Kind:
      - Name: %s
      - Alter: %d
      - Besondere Bedürfnisse: %s
      - Entwicklungsziele: %s
      - Beobachtungsparameter: %s

      Nutze diese Informationen, um der Begleitperson passende Empfehlungen zu geben.
    """
        .formatted(child.name(), age, child.specialNeeds(), child.goals(), child.entries());
  }

  public int calculateAge(LocalDate birthDate) {
    if (birthDate == null) {
      throw new IllegalArgumentException("Dob can't be null");
    }
    return Period.between(birthDate, LocalDate.now()).getYears();
  }

  private CompletableFuture<RunResponse> checkRunStatusAsync(String threadId, String runId) {
    return CompletableFuture.supplyAsync(
        () -> {
          while (true) {
            RunResponse status = openAiClient.getRunStatus(threadId, runId);
            if ("completed".equals(status.status())) {
              return status;
            }
            try {
              CompletableFuture<Void> delay =
                  CompletableFuture.runAsync(
                      () -> {}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
              delay.join();
            } catch (Exception e) {
              Thread.currentThread().interrupt();
              throw new RuntimeException("Interrupted during waiting for run status", e);
            }
          }
        });
  }

  private List<ChatResponseDto> getMessagesForFrontend(List<RootDTO.Message> messages) {
    return messages.stream()
        .map(
            msg ->
                new ChatResponseDto(
                    msg.id(),
                    msg.thread_id(),
                    msg.role(),
                    msg.content().stream()
                        .filter(content -> "text".equals(content.type()))
                        .map(c -> c.text().value())
                        .collect(Collectors.joining("\n")),
                    msg.created_at()))
        .collect(Collectors.toList());
  }
}
