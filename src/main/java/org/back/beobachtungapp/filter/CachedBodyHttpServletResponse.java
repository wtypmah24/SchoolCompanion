package org.back.beobachtungapp.filter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.*;

@SuppressFBWarnings
public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

  private final ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
  private ServletOutputStream outputStream;
  private PrintWriter writer;

  public CachedBodyHttpServletResponse(HttpServletResponse response) {
    super(response);
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.outputStream == null) {
      this.outputStream =
          new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
              cachedContent.write(b);
            }

            @Override
            public boolean isReady() {
              return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
              // Not used
            }
          };
    }
    return this.outputStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.writer == null) {
      this.writer =
          new PrintWriter(new OutputStreamWriter(cachedContent, getCharacterEncoding()), true);
    }
    return this.writer;
  }

  public byte[] getCachedBody() {
    return cachedContent.toByteArray();
  }

  public void copyBodyToResponse() throws IOException {
    ServletOutputStream out = super.getOutputStream();
    out.write(getCachedBody());
    out.flush();
  }
}
