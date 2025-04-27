package org.back.beobachtungapp.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class AbstractCacheService<T, ID> {
    private final JpaRepository<T, ID> repository;
    private final String cacheName;

    protected AbstractCacheService(JpaRepository<T, ID> repository, String cacheName) {
        this.repository = repository;
        this.cacheName = cacheName;
    }

    @Cacheable(value = "#root.target.cacheName", key = "#id")
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    @Cacheable(value = "#root.target.cacheName")
    public List<T> findAll() {
        return repository.findAll();
    }

    @CachePut(value = "#root.target.cacheName", key = "#id")
    public T save(ID id, T entity) {
        return repository.save(entity);
    }

    @CacheEvict(value = "#root.target.cacheName", key = "#id")
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
