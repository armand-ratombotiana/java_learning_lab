# Collections — Code Deep Dive

## Building a Custom Collection

```java
public class RingBuffer<T> implements Iterable<T> {
    private final Object[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    public RingBuffer(int capacity) {
        buffer = new Object[capacity];
    }

    public boolean offer(T item) {
        if (isFull()) return false;
        buffer[tail] = item;
        tail = (tail + 1) % buffer.length;
        count++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) return null;
        T item = (T) buffer[head];
        buffer[head] = null;
        head = (head + 1) % buffer.length;
        count--;
        return item;
    }

    public boolean isFull() { return count == buffer.length; }
    public boolean isEmpty() { return count == 0; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;
            @Override public boolean hasNext() { return cursor < count; }
            @SuppressWarnings("unchecked")
            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T item = (T) buffer[(head + cursor) % buffer.length];
                cursor++;
                return item;
            }
        };
    }
}
```

## LRU Cache with LinkedHashMap

```java
public class LRUCache<K, V> {
    private final LinkedHashMap<K, V> map;

    public LRUCache(int maxSize) {
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public synchronized V get(K key) {
        return map.get(key);
    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    public synchronized int size() { return map.size(); }

    public synchronized Map<K, V> getSnapshot() {
        return new LinkedHashMap<>(map);  // Copy for safe iteration
    }
}
```

## Thread-Safe Collection Wrapper

```java
public class ThreadSafeList<E> {
    private final List<E> backing = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void add(E item) {
        lock.writeLock().lock();
        try { backing.add(item); }
        finally { lock.writeLock().unlock(); }
    }

    public E get(int index) {
        lock.readLock().lock();
        try { return backing.get(index); }
        finally { lock.readLock().unlock(); }
    }

    public boolean contains(E item) {
        lock.readLock().lock();
        try { return backing.contains(item); }
        finally { lock.readLock().unlock(); }
    }

    public int size() {
        lock.readLock().lock();
        try { return backing.size(); }
        finally { lock.readLock().unlock(); }
    }
}
```

## Using Custom Comparators

```java
public record Person(String name, int age) {}

// Multi-field sorting:
List<Person> people = new ArrayList<>();
people.add(new Person("Alice", 30));
people.add(new Person("Bob", 25));
people.add(new Person("Alice", 25));

// Comparator chain:
people.sort(Comparator.comparing(Person::name)
    .thenComparing(Person::age)
    .reversed());

// TreeMap with custom ordering:
Map<String, Person> treeMap = new TreeMap<>(
    Comparator.naturalOrder().reversed()
);
```

## Bulk Operations with retainAll/addAll/removeAll

```java
Set<String> activeUsers = new HashSet<>(List.of("a", "b", "c"));
Set<String> bannedUsers = Set.of("b");

// Remove all banned:
activeUsers.removeAll(bannedUsers);  // {a, c}

// Keep only premium users:
Set<String> premiumUsers = Set.of("a", "d");
activeUsers.retainAll(premiumUsers);  // {a}

// Add all new users:
activeUsers.addAll(Set.of("e", "f"));  // {a, e, f}
```

## Iterating Collections Safely

```java
// ConcurrentModificationException prone:
for (String s : list) {
    if (s.startsWith("x")) list.remove(s);  // ❌
}

// Safe removal with Iterator:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("x")) it.remove();  // ✅
}

// Safe removal with removeIf (Java 8+):
list.removeIf(s -> s.startsWith("x"));  // ✅

// ConcurrentHashMap allows modification during iteration:
ConcurrentHashMap<String, Integer> cmap = new ConcurrentHashMap<>();
for (String key : cmap.keySet()) {
    cmap.put("new", 1);  // ✅ — ConcurrentHashMap is safe
}
```
