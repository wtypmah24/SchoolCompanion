package org.back.beobachtungapp.filter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  public CachedBodyHttpServletRequest(HttpServletRequest request) {
    super(request);
    this.cachedBody = readRequestBody(request);
  }

  private byte[] readRequestBody(HttpServletRequest request) {
    try (InputStream inputStream = request.getInputStream()) {
      return inputStream.readAllBytes();
    } catch (IOException e) {
      log.warn("Error during reading req. body: {}", e.getMessage(), e);
      return new byte[0];
    }
  }

  @Override
  public ServletInputStream getInputStream() {
    return new CachedBodyServletInputStream(cachedBody);
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
  }

  public byte[] getCachedBody() {
    return Arrays.copyOf(cachedBody, cachedBody.length);
  }
}
