package org.back.beobachtungapp.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

public class CachedBodyServletInputStream extends ServletInputStream {

  private final ByteArrayInputStream byteArrayInputStream;

  public CachedBodyServletInputStream(byte[] cachedBody) {
    this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
  }

  @Override
  public int read() {
    return byteArrayInputStream.read();
  }

  @Override
  public boolean isFinished() {
    return byteArrayInputStream.available() == 0;
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(ReadListener readListener) {}
}
