package org.back.beobachtungapp.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

@SuppressFBWarnings()
public record CacheEvent(String[] cacheNames, Object[] keys) {

  public String[] cacheNames() {
    return Arrays.copyOf(cacheNames, cacheNames.length);
  }

  public Object[] keys() {
    return Arrays.copyOf(keys, keys.length);
  }
}
