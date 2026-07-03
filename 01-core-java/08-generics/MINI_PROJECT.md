# Module 08: Generics - Mini Project

**Project Name**: Generic In-Memory Cache System  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Design a strongly-typed, thread-safe, generic caching utility that can store, retrieve, and evict objects of any type, demonstrating advanced generic type constraints and wildcard usage.

## 📝 Requirements

### Core Features
1. **Generic Cache Interface**:
   - Create an interface `Cache<K, V>` with methods:
     - `void put(K key, V value, long timeToLiveMillis)`
     - `Optional<V> get(K key)`
     - `void evict(K key)`
     - `void clear()`

2. **Generic Implementation**:
   - Implement `InMemoryCache<K, V> implements Cache<K, V>`.
   - Back it with a `ConcurrentHashMap`.
   - Wrap the values in an internal generic private record/class `CacheEntry<V>(V value, long expirationTime)`.

3. **Type Constraints (Bounded Generics)**:
   - Create a specific subclass `NumericCache<K, V extends Number>`.
   - Add a method `double getAverage()` that iterates through all non-expired entries in the cache, sums their `doubleValue()`, and returns the average.

4. **Wildcards (PECS Rule)**:
   - In the `Cache` interface, add a method `void putAll(Map<? extends K, ? extends V> map, long timeToLiveMillis)`.
   - Add a method `void getAllPresent(Iterable<? extends K> keys, Map<? super K, ? super V> destination)`. This reads keys from the Iterable, pulls the active values from the cache, and writes them into the destination map.

5. **Eviction Logic**:
   - In the `get` method, check if `System.currentTimeMillis() > entry.expirationTime()`. If so, remove it from the map and return `Optional.empty()`.

---

## 💡 Solution Blueprint

1. **Interface**:
   ```java
   public interface Cache<K, V> {
       void put(K key, V value, long ttl);
       Optional<V> get(K key);
       void putAll(Map<? extends K, ? extends V> map, long ttl);
       void getAllPresent(Iterable<? extends K> keys, Map<? super K, ? super V> dest);
   }
   ```
2. **Implementation**:
   ```java
   public class InMemoryCache<K, V> implements Cache<K, V> {
       private final Map<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
       
       private record CacheEntry<V>(V value, long expiry) {
           boolean isExpired() { return System.currentTimeMillis() > expiry; }
       }
       
       // Implement get with eviction check
       public Optional<V> get(K key) {
           CacheEntry<V> entry = map.get(key);
           if (entry != null && entry.isExpired()) {
               map.remove(key);
               return Optional.empty();
           }
           return Optional.ofNullable(entry).map(CacheEntry::value);
       }
       
       // Implement putAll (Producer Extends)
       public void putAll(Map<? extends K, ? extends V> source, long ttl) {
           source.forEach((k, v) -> put(k, v, ttl));
       }
   }
   ```