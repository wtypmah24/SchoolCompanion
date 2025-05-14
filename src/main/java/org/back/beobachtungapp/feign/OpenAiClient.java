package org.back.beobachtungapp.feign;

import org.back.beobachtungapp.config.OpenAiFeignConfig;
import org.back.beobachtungapp.dto.openai.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "openAiAssistantClient",
    url = "https://api.openai.com/v1",
    configuration = OpenAiFeignConfig.class)
public interface OpenAiClient {

  @PostMapping("/threads")
  ThreadResponse createThread();

  @PostMapping("/threads/{threadId}/messages")
  MessageResponse addMessage(
      @PathVariable("threadId") String threadId, @RequestBody MessageRequest message);

  @GetMapping("/threads/{threadId}/messages")
  MessageResponse getMessages(@PathVariable("threadId") String threadId);

  @PostMapping("/threads/{threadId}/runs")
  RunResponse startRun(@PathVariable("threadId") String threadId, @RequestBody RunRequest run);

  @GetMapping("/threads/{threadId}/runs/{runId}")
  RunResponse getRunStatus(
      @PathVariable("threadId") String threadId, @PathVariable("runId") String runId);

  @GetMapping("/threads/{threadId}/messages")
  RootDTO listMessages(@PathVariable("threadId") String threadId);
}
