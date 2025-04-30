package org.back.beobachtungapp.listener;

import java.util.List;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.event.ChildCacheEvictEvent;
import org.back.beobachtungapp.event.ChildCacheUpdateEvent;
import org.back.beobachtungapp.mapper.ChildMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ChildCacheEventListener {

  private final CacheManager cacheManager;
  private final ChildRepository childRepository;
  private final ChildMapper childMapper;

  @Autowired
  public ChildCacheEventListener(
      CacheManager cacheManager, ChildRepository childRepository, ChildMapper childMapper) {
    this.cacheManager = cacheManager;
    this.childRepository = childRepository;
    this.childMapper = childMapper;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onChildCacheUpdate(ChildCacheUpdateEvent event) {
    Long childId = event.childId();
    Long companionId = event.companionId();

    childRepository
        .findById(childId)
        .ifPresent(
            child -> {
              ChildResponseDto dto = childMapper.childToChildResponseDto(child);
              getRequiredCache("child").put(childId, dto);
            });

    List<Child> updatedChildren = childRepository.findAllBySchoolCompanionId(companionId);
    List<ChildResponseDto> dtoList = childMapper.childToChildResponseDtoList(updatedChildren);
    getRequiredCache("children").put(companionId, dtoList);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onChildCacheEvict(ChildCacheEvictEvent event) {
    Long childId = event.childId();
    Long companionId = event.companionId();

    getRequiredCache("child").evict(childId);
    getRequiredCache("children").evict(companionId);
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
