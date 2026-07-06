# Refactoring for GC

## From Large Objects to Chunks
**Before:** Single large array:
```java
byte[] allData = new byte[100 * 1024 * 1024]; // 100 MB array
```
**After:** Chunked into regions:
```java
List<byte[]> chunks = new ArrayList<>();
for (int i = 0; i < 100; i++) chunks.add(new byte[1024 * 1024]);
```
Benefits: avoids humongous allocations (G1), enables partial collection.

## From Explicit nulling to Scoping
**Before:** Setting null for GC:
```java
Map<String, Data> cache = new HashMap<>();
// ... use cache ...
cache = null; // help GC
```
**After:** Let scoping handle it:
```java
void process() {
    Map<String, Data> cache = new HashMap<>();
    // ... use cache ...
    // cache goes out of scope, eligible for GC
}
```
Benefits: clearer code, GC handles unreachable objects.

## From Object Allocation to Object Pooling
**Before:** Creating objects in a hot loop:
```java
for (int i = 0; i < 1_000_000; i++) {
    MutablePoint p = new MutablePoint(x, y);
    process(p);
}
```
**After:** Object pooling (only if EA doesn't help):
```java
MutablePoint p = new MutablePoint(); // reuse
for (int i = 0; i < 1_000_000; i++) {
    p.set(x, y);
    process(p);
}
```
Benefits: reduces allocation rate, GC pressure.

## From String+ to StringBuilder
**Before:**
```java
String result = a + b + c + d; // creates many intermediate strings
```
**After:**
```java
String result = a + b + c + d; // Javac uses StringBuilder automatically
```
The JIT may also optimize string concatenation using invokedynamic (Java 9+).

## From WeakHashMap to Caffeine Cache
**Before:**
```java
Map<Key, Value> cache = new WeakHashMap<>();
```
**After:**
```java
Cache<Key, Value> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();
```
Benefits: predictable eviction, better GC interaction, configurable policies.
