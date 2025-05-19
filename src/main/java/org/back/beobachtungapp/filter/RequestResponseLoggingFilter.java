package org.back.beobachtungapp.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet filter that logs HTTP request and response bodies.
 *
 * <p>Wraps requests and responses to cache their bodies, allowing logging without consuming
 * streams.
 *
 * <p>Sensitive fields like passwords, tokens, secrets, and JWT tokens are masked before logging to
 * protect confidential information.
 *
 * <p>If the response content type is PDF, the response body logging is skipped, and only an info
 * message about the PDF content is logged.
 */
@SuppressFBWarnings
@WebFilter("/*")
public class RequestResponseLoggingFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  /**
   * Filters each request/response pair to log the request URI, masked request body, and masked
   * response body (unless response is a PDF).
   *
   * @param request the incoming servlet request
   * @param response the outgoing servlet response
   * @param chain the filter chain to invoke the next filter or target resource
   * @throws IOException if an I/O error occurs during filtering
   * @throws ServletException if the request cannot be handled
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    CachedBodyHttpServletRequest wrappedRequest =
        new CachedBodyHttpServletRequest((HttpServletRequest) request);
    CachedBodyHttpServletResponse wrappedResponse =
        new CachedBodyHttpServletResponse((HttpServletResponse) response);

    String requestPath = wrappedRequest.getRequestURI();

    chain.doFilter(wrappedRequest, wrappedResponse);

    // Logging the request body with masking
    String requestBody = new String(wrappedRequest.getCachedBody(), StandardCharsets.UTF_8);
    logger.info(
        "Request path: {}, Request body: {}", requestPath, maskSensitiveFields(requestBody));

    // Copy the response body back to the original response
    wrappedResponse.copyBodyToResponse();

    // Getting the Content-Type of the response
    String contentType = wrappedResponse.getContentType();

    if (contentType != null && contentType.startsWith("application/pdf")) {
      // Skip logging the response body, instead just log the fact that the file was sent
      logger.info("Response body for {}: <PDF content, logging skipped>", requestPath);
    } else {
      // Logging the response body (with masking and truncation)
      String responseBody = new String(wrappedResponse.getCachedBody(), StandardCharsets.UTF_8);
      logResponseBody(requestPath, responseBody);
    }
  }

  /**
   * Logs the response body for a request path, truncating it if longer than 1000 characters.
   *
   * @param requestPath the URI path of the request
   * @param body the response body string to log
   */
  private void logResponseBody(String requestPath, String body) {
    if (body.length() > 1000) {
      body = body.substring(0, 1000) + "... (truncated)";
    }
    logger.info("Response body for {}: {}", requestPath, maskSensitiveFields(body));
  }

  /**
   * Masks sensitive fields such as "password", "token", "secret", and "accessToken" in a JSON
   * string. If input is not JSON, tries to mask JWT tokens.
   *
   * @param body the input string potentially containing sensitive fields or JWT tokens
   * @return the string with sensitive fields and tokens masked
   */
  private String maskSensitiveFields(String body) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode root = objectMapper.readTree(body);
      if (root.isObject()) {
        ObjectNode objectNode = (ObjectNode) root;
        List<String> sensitiveFields = Arrays.asList("password", "token", "secret", "accessToken");
        for (String field : sensitiveFields) {
          if (objectNode.has(field)) {
            objectNode.put(field, "***");
          }
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
      }
    } catch (Exception ignored) {
    }

    return maskJwtTokens(body);
  }

  /**
   * Masks all JWT tokens in a string by replacing their three base64 parts with "***".
   *
   * @param input the input string potentially containing JWT tokens
   * @return the string with JWT tokens masked
   */
  private String maskJwtTokens(String input) {
    return input.replaceAll(
        "([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)", "***.***.***");
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
