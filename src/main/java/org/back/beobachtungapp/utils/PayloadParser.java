package org.back.beobachtungapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PayloadParser {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> T parse(String payload, Class<T> clazz) {
    try {
      return objectMapper.readValue(payload, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse payload", e);
    }
  }

  public JsonNode parseJson(String payload) throws JsonProcessingException {
    return objectMapper.readTree(payload);
  }
}
