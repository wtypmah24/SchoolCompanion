package org.back.beobachtungapp.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@SuppressFBWarnings
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Around("execution(* org.back.beobachtungapp.controller..*(..))")
  public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    Object[] args = joinPoint.getArgs();

    String filteredParams =
        Arrays.stream(args)
            .map(this::maskSensitiveObject)
            .collect(Collectors.joining(", ", "[", "]"));

    log.info(
        "In class: {}, calling method: {} with params: {}", className, methodName, filteredParams);

    Object result = joinPoint.proceed();

    String filteredResult = maskSensitiveObject(result);
    log.info("In class: {}, method {} returned: {}", className, methodName, filteredResult);

    return result;
  }

  private String maskSensitiveObject(Object obj) {
    if (obj == null) return "null";
    try {
      String json = objectMapper.writeValueAsString(obj);
      return maskSensitiveFields(json);
    } catch (Exception e) {
      return obj.toString();
    }
  }

  private String maskSensitiveFields(String jsonOrRaw) {
    try {
      JsonNode root = objectMapper.readTree(jsonOrRaw);
      if (root.isObject()) {
        ObjectNode objectNode = (ObjectNode) root;
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

        while (fields.hasNext()) {
          Map.Entry<String, JsonNode> entry = fields.next();
          JsonNode value = entry.getValue();

          if ("password".equals(entry.getKey())) {
            objectNode.put(entry.getKey(), "***");
          }

          if (value.isTextual()) {
            String text = value.asText();
            if (isJwtToken(text)) {
              objectNode.put(entry.getKey(), "***.***.***");
            }
          }
        }

        return objectMapper.writeValueAsString(objectNode);
      }
    } catch (Exception e) {
      log.warn("Failed to mask sensitive fields", e);
      return jsonOrRaw;
    }

    return maskJwtTokens(jsonOrRaw);
  }

  private boolean isJwtToken(String text) {
    return text.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
  }

  private String maskJwtTokens(String input) {
    return input.replaceAll(
        "([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)", "***.***.***");
  }
}
