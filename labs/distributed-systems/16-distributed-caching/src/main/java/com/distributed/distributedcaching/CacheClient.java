package com.distributed.distributedcaching;

import java.time.Duration;
import java.util.Optional;

public interface CacheClient<K, V> {
    Optional<V> get(K key);
    void put(K key, V value, Duration ttl);
    boolean delete(K key);
    boolean exists(K key);
    void clear();
}
