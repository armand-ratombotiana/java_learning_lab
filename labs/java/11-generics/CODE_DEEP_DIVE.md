# Generics — Code Deep Dive

## Generic Stack Implementation

```java
public class GenericStack<T> {
    private static final int INITIAL_CAPACITY = 10;
    private Object[] elements;  // Cannot create T[]
    private int size = 0;

    public GenericStack() {
        elements = new Object[INITIAL_CAPACITY];
    }

    public void push(T item) {
        ensureCapacity();
        elements[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T item = (T) elements[--size];  // Unchecked cast — safe because we only push T
        elements[size] = null;          // Help GC
        return item;
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) elements[size - 1];
    }

    public boolean isEmpty() { return size == 0; }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }
}
```

Key points:
- Internal array is `Object[]` — `new T[INITIAL_CAPACITY]` is illegal due to erasure
- Cast `(T)` in `pop()` and `peek()` produce unchecked warnings — suppressed with `@SuppressWarnings("unchecked")`
- Safety relies on invariant: only `T` values are ever stored

## Type Token Pattern

```java
public class TypeToken<T> {
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public TypeToken(Class<?> type) {
        this.type = (Class<T>) type;
    }

    public Class<T> getType() { return type; }
}

// Usage:
TypeToken<String> token = new TypeToken<>(String.class);
```

## Generic Builder Pattern

```java
public class GenericBuilder<T> {
    private final Supplier<T> constructor;
    private final List<Consumer<T>> setters = new ArrayList<>();

    public GenericBuilder(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public GenericBuilder<T> with(Consumer<T> setter) {
        setters.add(setter);
        return this;
    }

    public T build() {
        T instance = constructor.get();
        setters.forEach(s -> s.accept(instance));
        return instance;
    }
}

// Usage:
Order order = new GenericBuilder<>(Order::new)
    .with(o -> o.setId(1L))
    .with(o -> o.setCustomer("Alice"))
    .build();
```

## Wildcard-Guarded Method

```java
public class CollectionsUtil {
    // Safe copy with wildcards
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (T item : src) {
            dest.add(item);
        }
    }

    // Read-only view via wildcard
    public static <T> List<T> unmodifiableUnion(
            List<? extends T> a, List<? extends T> b) {
        List<T> result = new ArrayList<>(a.size() + b.size());
        result.addAll(a);
        result.addAll(b);
        return Collections.unmodifiableList(result);
    }
}
```

## Recursive Type Bound

```java
public class Entity<T extends Entity<T>> {
    private Long id;

    @SuppressWarnings("unchecked")
    public T withId(Long id) {
        this.id = id;
        return (T) this;  // Return concrete subtype
    }
}

class User extends Entity<User> {
    private String name;

    public User withName(String name) {
        this.name = name;
        return this;  // Already User
    }
}

// Fluid API:
User user = new User()
    .withId(1L)     // Returns User (not Entity)
    .withName("Alice");
```
