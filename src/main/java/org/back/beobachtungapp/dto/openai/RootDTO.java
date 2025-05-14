package org.back.beobachtungapp.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RootDTO(
    String object, List<Message> data, String first_id, String last_id, boolean has_more) {
  public record Message(
      String id,
      String object,
      long created_at,
      String assistant_id,
      String thread_id,
      String run_id,
      String role,
      List<Content> content,
      List<Object> attachments,
      Map<String, Object> metadata) {
    public record Content(String type, Text text) {
      public record Text(String value, List<Object> annotations) {}
    }
  }
}
