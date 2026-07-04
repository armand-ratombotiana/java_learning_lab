# Code Deep Dive: Hash Tables

## Custom hashCode Implementation

```java
public class Person {
    private final String name;
    private final int age;
    private final String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return age == person.age
            && Objects.equals(name, person.name)
            && Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
```

The number `31` is chosen because:
- It's an odd prime (multiplication by odd preserves information)
- `31 * i` can be optimized to `(i << 5) - i` (fast on JVM)

## Linear Probing Hash Table

```java
public class LinearProbingHashMap<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private K[] keys;
    private V[] values;
    private int size;

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap() {
        keys = (K[]) new Object[INITIAL_CAPACITY];
        values = (V[]) new Object[INITIAL_CAPACITY];
    }

    public void put(K key, V value) {
        if (size >= keys.length / 2) resize(keys.length * 2);
        int i;
        for (i = hash(key); keys[i] != null; i = (i + 1) % keys.length) {
            if (keys[i].equals(key)) {
                values[i] = value;  // update
                return;
            }
        }
        keys[i] = key;
        values[i] = value;
        size++;
    }

    public V get(K key) {
        for (int i = hash(key); keys[i] != null; i = (i + 1) % keys.length) {
            if (keys[i].equals(key)) return values[i];
        }
        return null;
    }

    public void delete(K key) {
        int i = hash(key);
        while (!keys[i].equals(key)) i = (i + 1) % keys.length;
        keys[i] = null;
        values[i] = null;
        size--;
        // Reinsert all subsequent keys in cluster
        i = (i + 1) % keys.length;
        while (keys[i] != null) {
            K keyToRehash = keys[i];
            V valToRehash = values[i];
            keys[i] = null;
            values[i] = null;
            size--;
            put(keyToRehash, valToRehash);
            i = (i + 1) % keys.length;
        }
        if (size > 0 && size <= keys.length / 8) resize(keys.length / 2);
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % keys.length;
    }

    private void resize(int capacity) {
        LinearProbingHashMap<K, V> tmp = new LinearProbingHashMap<>();
        tmp.keys = (K[]) new Object[capacity];
        tmp.values = (V[]) new Object[capacity];
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) tmp.put(keys[i], values[i]);
        }
        keys = tmp.keys;
        values = tmp.values;
    }
}
```
