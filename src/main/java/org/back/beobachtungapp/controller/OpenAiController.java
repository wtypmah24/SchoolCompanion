package org.back.beobachtungapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.openai.ChatRequest;
import org.back.beobachtungapp.dto.openai.ChatResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.integration.openai.OpenAiService;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat", description = "Chat interaction with OpenAI Assistant")
public class OpenAiController {
  private final OpenAiService assistantService;
  private final CompanionService companionService;

  @Autowired
  public OpenAiController(OpenAiService assistantService, CompanionService companionService) {
    this.assistantService = assistantService;
    this.companionService = companionService;
  }

  @Operation(
      summary = "Ask a question in a new chat thread",
      description = "Starts a new thread and sends a prompt to the assistant.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat response from assistant"),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "500", description = "Internal server error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  @PostMapping("child/{childId}")
  public List<ChatResponseDto> askNewChat(
      @PathVariable() Long childId,
      @RequestBody ChatRequest prompt,
      @CurrentCompanion CompanionDto companionDto) {
    return assistantService.ask(prompt, companionDto, childId, null);
  }

  @Operation(
      summary = "Ask a question in an existing chat thread",
      description = "Continues an existing thread with a new prompt.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Chat response from assistant"),
    @ApiResponse(responseCode = "400", description = "Invalid thread or input"),
    @ApiResponse(responseCode = "500", description = "Internal server error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  @PostMapping("child/{childId}/thread/{threadId}")
  public List<ChatResponseDto> ask(
      @PathVariable() Long childId,
      @PathVariable() String threadId,
      @RequestBody ChatRequest prompt,
      @CurrentCompanion CompanionDto companionDto) {
    return assistantService.ask(prompt, companionDto, childId, threadId);
  }

  @Operation(
      summary = "Get all chat thread IDs",
      description = "Returns a set of thread IDs associated with the current companion.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "List of thread IDs"),
    @ApiResponse(responseCode = "500", description = "Internal server error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  @GetMapping("threads")
  public Set<String> getChatIds(@CurrentCompanion CompanionDto companionDto) {
    return companionService.getThreadIds(companionDto.id());
  }

  @Operation(
      summary = "Get chat messages by thread ID",
      description = "Fetches all messages in the specified thread.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "List of chat messages"),
    @ApiResponse(responseCode = "500", description = "Internal server error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
  })
  @GetMapping("thread/{threadId}")
  public List<ChatResponseDto> getChatByThreadId(@PathVariable String threadId) {
    return assistantService.getChatByThreadId(threadId);
  }

  @DeleteMapping("thread/{threadId}")
  @Operation(
      summary = "Delete a chat thread",
      description = "Removes the specified chat thread for the current companion.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Thread deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Thread not found")
  })
  public void removeChatByThreadId(
      @CurrentCompanion CompanionDto companionDto, @PathVariable String threadId) {
    companionService.deleteThreadIds(companionDto, threadId);
  }
}
