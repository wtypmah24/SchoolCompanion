package org.back.beobachtungapp.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.openai.*;
import org.back.beobachtungapp.feign.OpenAiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAiService {
  @Value("${openai.assistant.id}")
  private String assistantId;

  private final OpenAiClient openAiClient;

  public OpenAiService(OpenAiClient openAiClient) {
    this.openAiClient = openAiClient;
  }

  public List<ChatResponseDto> ask(String prompt) {
    String threadId = openAiClient.createThread().id();
    log.info("threadId: {}", threadId);
    openAiClient.addMessage(threadId, new MessageRequest("user", prompt));

    RunResponse run = openAiClient.startRun(threadId, new RunRequest(assistantId));
    log.info("run: {}", run);
    CompletableFuture<RunResponse> runStatusFuture = checkRunStatusAsync(threadId, run.id());

    RunResponse completedRun = runStatusFuture.join();
    log.info("Run completed: {}", completedRun);

    List<RootDTO.Message> messages = openAiClient.listMessages(threadId).data();
    return getMessagesForFrontend(messages);
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
                        .collect(Collectors.joining("\n"))))
        .collect(Collectors.toList());
  }
}
