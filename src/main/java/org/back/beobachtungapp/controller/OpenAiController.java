package org.back.beobachtungapp.controller;

import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.openai.ChatResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class OpenAiController {
  private final OpenAiService assistantService;

  public OpenAiController(OpenAiService assistantService) {
    this.assistantService = assistantService;
  }

  @PostMapping()
  public List<ChatResponseDto> ask(
      @RequestBody String prompt, @CurrentCompanion CompanionDto companionDto) {
    return assistantService.ask(prompt);
  }
}
