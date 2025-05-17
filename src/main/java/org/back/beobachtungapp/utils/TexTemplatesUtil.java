package org.back.beobachtungapp.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TexTemplatesUtil {
  public static String loadTemplate(String path) {
    try (InputStream is = TexTemplatesUtil.class.getClassLoader().getResourceAsStream(path)) {
      assert is != null;
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    } catch (IOException | NullPointerException e) {
      throw new UncheckedIOException("Could not load template from " + path, new IOException(e));
    }
  }
}
