package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.openai.ChatRequest;
import org.back.beobachtungapp.dto.openai.ChatResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.integration.openai.OpenAiService;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class OpenAiController {
  private final OpenAiService assistantService;
  private final CompanionService companionService;

  @PostMapping("child/{childId}")
  public List<ChatResponseDto> askNewChat(
      @PathVariable() Long childId,
      @RequestBody ChatRequest prompt,
      @CurrentCompanion CompanionDto companionDto) {
    return assistantService.ask(prompt, companionDto, childId, null);
  }

  @PostMapping("child/{childId}/thread/{threadId}")
  public List<ChatResponseDto> ask(
      @PathVariable() Long childId,
      @PathVariable() String threadId,
      @RequestBody ChatRequest prompt,
      @CurrentCompanion CompanionDto companionDto) {
    return assistantService.ask(prompt, companionDto, childId, threadId);
  }

  @GetMapping("threads")
  public List<String> getChatIds(@CurrentCompanion CompanionDto companionDto) {
    return companionService.getThreadIds(companionDto.id());
  }

  @GetMapping("thread/{threadId}")
  public List<ChatResponseDto> getChatByThreadId(@PathVariable String threadId) {
    return assistantService.getChatByThreadId(threadId);
  }

  @DeleteMapping("thread/{threadId}")
  public void removeChatByThreadId(
      @CurrentCompanion CompanionDto companionDto, @PathVariable String threadId) {
    companionService.deleteThreadIds(companionDto, threadId);
  }
}
