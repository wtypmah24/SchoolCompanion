package org.back.beobachtungapp.listener;

import org.back.beobachtungapp.event.CacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CacheEventListener {

  private final CacheManager cacheManager;

  @Autowired
  public CacheEventListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onCacheEvict(CacheEvent event) {
    String[] cacheNames = event.cacheNames();
    Object[] keys = event.keys();

    if (cacheNames.length != keys.length) {
      throw new IllegalArgumentException("Cache names and keys must be of the same length");
    }

    for (int i = 0; i < cacheNames.length; i++) {
      getRequiredCache(cacheNames[i]).evict(keys[i]);
    }
  }

  private Cache getRequiredCache(String name) {
    Cache cache = cacheManager.getCache(name);
    if (cache == null) {
      throw new IllegalStateException(
          "Cache '" + name + "' is not configured in RedisCacheManager");
    }
    return cache;
  }
}
