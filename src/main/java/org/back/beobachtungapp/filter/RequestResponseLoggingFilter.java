package org.back.beobachtungapp.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@WebFilter("/*")
@Component
public class RequestResponseLoggingFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // Wrap request, to read body more than one time
    CachedBodyHttpServletRequest wrappedRequest =
        new CachedBodyHttpServletRequest((HttpServletRequest) request);

    // Log req body
    String requestBody = new String(wrappedRequest.getCachedBody(), StandardCharsets.UTF_8);
    logger.info("Request body: {}", requestBody);

    // Send by chain further
    chain.doFilter(wrappedRequest, response);
  }

  private void logResponseBody(String body) {
    if (body.length() > 1000) {
      body = body.substring(0, 1000) + "... (truncated)";
    }
    logger.info("Response body: {}", formatJson(body));
  }

  private String formatJson(String json) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Object jsonObj = objectMapper.readTree(json);
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
    } catch (JsonProcessingException e) {
      return json;
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
