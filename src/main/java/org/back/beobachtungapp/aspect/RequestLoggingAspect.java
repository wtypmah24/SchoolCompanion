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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging requests and responses in controller classes.
 *
 * <p>Intercepts all method executions within the package {@code org.back.beobachtungapp.controller}
 * and logs method names, input parameters, and returned results.
 *
 * <p>Sensitive data such as passwords and JWT tokens are masked before logging to avoid leaking
 * confidential information.
 *
 * <p>If the response is a file represented as a byte array, the response logging is skipped.
 */
@SuppressFBWarnings
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Around advice that logs the method name, class name, filtered input parameters, and filtered
   * output result of controller methods.
   *
   * @param joinPoint the join point providing access to method execution details
   * @return the original method's return value
   * @throws Throwable if the intercepted method throws any exceptions
   */
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

    if (result instanceof ResponseEntity<?> responseEntity) {
      if (responseEntity.getBody() instanceof byte[]) {
        log.info(
            "In class: {}, method {} returned a file response (byte[]), logging skipped",
            className,
            methodName);
        return result;
      }
    }

    String filteredResult = maskSensitiveObject(result);
    log.info("In class: {}, method {} returned: {}", className, methodName, filteredResult);

    return result;
  }

  /**
   * Converts an object to JSON string and masks sensitive fields such as passwords and JWT tokens.
   *
   * @param obj the object to mask and serialize
   * @return the masked JSON string representation or {@code obj.toString()} if serialization fails
   */
  private String maskSensitiveObject(Object obj) {
    if (obj == null) return "null";
    try {
      String json = objectMapper.writeValueAsString(obj);
      return maskSensitiveFields(json);
    } catch (Exception e) {
      return obj.toString();
    }
  }

  /**
   * Masks sensitive fields in a JSON string. Specifically masks fields named "password" and JWT
   * tokens anywhere in string values.
   *
   * @param jsonOrRaw JSON string or raw string to mask
   * @return JSON string with masked sensitive fields, or original string if parsing fails
   */
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

  /**
   * Checks if a string matches the pattern of a JWT token.
   *
   * @param text the string to check
   * @return {@code true} if the string looks like a JWT token, {@code false} otherwise
   */
  private boolean isJwtToken(String text) {
    return text.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
  }

  /**
   * Masks all JWT tokens in a string by replacing their parts with "***".
   *
   * @param input the input string potentially containing JWT tokens
   * @return the string with JWT tokens masked
   */
  private String maskJwtTokens(String input) {
    return input.replaceAll(
        "([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)", "***.***.***");
  }
}
