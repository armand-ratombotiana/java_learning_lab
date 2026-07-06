# Challenge Solution: Deadlock-Free Resize

The challenge asked for a resize operation that acquires all segment locks
without deadlock and rehashes across new segments.

**Approach**: Lock ordering (always acquire segments in ascending index order)
ensures deadlock freedom. Each segment is locked, drained, and rehashed into
a new segment array. While a segment is locked, no other thread can access it,
so linearizability is maintained.

```java
void resize() {
    int oldLen = segments.length;
    int newLen = oldLen * 2;
    Lock[] newLocks = new ReentrantLock[newLen];
    Segment<K, V>[] newSegs = new Segment[newLen];
    // Acquire all old locks in order
    for (int i = 0; i < oldLen; i++) segments[i].lock().lock();
    try {
        for (int i = 0; i < newLen; i++) {
            newLocks[i] = new ReentrantLock();
            newSegs[i] = new Segment<>(newLocks[i]);
        }
        for (int i = 0; i < oldLen; i++) {
            for (Node<K, V> n : segments[i].nodes()) {
                int newIdx = hash(n.key) & (newLen - 1);
                newSegs[newIdx].put(n.key, n.value);
            }
        }
        segments = newSegs;
        locks = newLocks;
    } finally {
        for (int i = oldLen - 1; i >= 0; i--) segments[i].lock().unlock();
    }
}
```
