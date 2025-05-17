package org.back.beobachtungapp.service;

import static org.back.beobachtungapp.utils.PersonUtils.calculateAge;
import static org.back.beobachtungapp.utils.TexTemplatesUtil.loadTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.OpenAiProperties;
import org.back.beobachtungapp.dto.openai.*;
import org.back.beobachtungapp.dto.response.child.ChildWithAttachments;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.feign.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for interacting with the OpenAI Assistant API. Responsible for managing chat threads,
 * sending prompts, and retrieving responses.
 */
@Slf4j
@Service
public class OpenAiService {

  private final OpenAiProperties openAiProperties;
  private final OpenAiClient openAiClient;
  private final CompanionService companionService;
  private final ChildService childService;

  @Autowired
  public OpenAiService(
      OpenAiProperties openAiProperties,
      OpenAiClient openAiClient,
      CompanionService companionService,
      ChildService childService) {
    this.openAiProperties = openAiProperties;
    this.openAiClient = openAiClient;
    this.companionService = companionService;
    this.childService = childService;
  }

  /**
   * Handles a user prompt by either starting a new thread or continuing an existing one.
   *
   * @param promptDto the incoming user message
   * @param companionDto the companion related to the conversation
   * @param childId ID of the child the context is built around
   * @param threadId optional ID of an existing chat thread
   * @return list of responses suitable for frontend display
   */
  @Transactional
  public List<ChatResponseDto> ask(
      ChatRequest promptDto, CompanionDto companionDto, Long childId, String threadId) {
    String prompt = promptDto.message();
    boolean isNewThread = (threadId == null || threadId.isBlank());

    if (isNewThread) {
      threadId = createNewThreadWithContext(childId);
      companionService.addChatIdToCompanion(companionDto.id(), threadId);
    }

    return sendPromptAndGetResponse(threadId, prompt);
  }

  /**
   * Creates a new chat thread and sends initial context based on the child's data.
   *
   * @param childId ID of the child used to build the context
   * @return newly created thread ID
   */
  private String createNewThreadWithContext(Long childId) {
    String threadId = openAiClient.createThread().id();
    log.info("Created new threadId: {}", threadId);

    String context = buildContextForCompanion(childId);
    log.info("Companion context: {}", context);

    openAiClient.addMessage(threadId, new MessageRequest("user", context));
    return threadId;
  }

  /**
   * Sends a user prompt to the assistant and waits for the response.
   *
   * @param threadId ID of the thread to continue
   * @param prompt user message to send
   * @return list of assistant messages formatted for frontend
   */
  private List<ChatResponseDto> sendPromptAndGetResponse(String threadId, String prompt) {
    log.info("Using threadId: {}", threadId);
    openAiClient.addMessage(threadId, new MessageRequest("user", prompt));

    RunResponse run = openAiClient.startRun(threadId, new RunRequest(openAiProperties.getId()));
    log.info("Started run: {}", run);

    RunResponse completedRun = checkRunStatusAsync(threadId, run.id()).join();
    log.info("Completed run: {}", completedRun);

    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    return getMessagesForFrontend(messages);
  }

  /**
   * Retrieves all messages in a given thread, formatted for frontend display.
   *
   * @param threadId ID of the thread
   * @return list of messages from the assistant
   */
  public List<ChatResponseDto> getChatByThreadId(String threadId) {
    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    return getMessagesForFrontend(messages);
  }

  /**
   * Builds a context message for the assistant using the child's data.
   *
   * @param childId ID of the child
   * @return formatted context string
   */
  private String buildContextForCompanion(Long childId) {
    ChildWithAttachments child = childService.getChildWithAttachments(childId);
    int age = calculateAge(child.dateOfBirth());

    String template = loadTemplate("templates/context-template.txt");
    return String.format(
        template, child.name(), age, child.specialNeeds(), child.goals(), child.entries());
  }

  /**
   * Asynchronously checks the status of a running assistant task until it is completed.
   *
   * @param threadId ID of the thread
   * @param runId ID of the current run
   * @return completed RunResponse object
   */
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

  /**
   * Filters and maps assistant messages to a DTO format used on the frontend. Context messages
   * (prefixed with [CONTEXT]) are excluded.
   *
   * @param messages list of raw messages from OpenAI
   * @return list of processed messages
   */
  private List<ChatResponseDto> getMessagesForFrontend(List<RootDTO.Message> messages) {
    return messages.stream()
        .filter(
            msg ->
                msg.content().stream()
                    .noneMatch(
                        c -> c.type().equals("text") && c.text().value().startsWith("[CONTEXT]")))
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
