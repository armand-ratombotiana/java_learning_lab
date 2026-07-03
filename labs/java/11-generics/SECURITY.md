# Generics — Security Implications

## Heap Pollution Vulnerabilities

Heap pollution occurs when a variable of a parameterized type refers to an object not of that type. This can lead to `ClassCastException` at runtime — but more importantly, it can bypass type safety checks:

```java
// Vulnerability: Unchecked cast allows type confusion
@SuppressWarnings("unchecked")
public static <T> List<T> unsafeCast(Object obj) {
    return (List<T>) obj;  // No runtime check!
}

// Exploitation:
List raw = new ArrayList();
raw.add("malicious string");
List<Integer> intList = unsafeCast(raw);
int x = intList.get(0);  // ClassCastException — but before that, could corrupt state
```

## Deserialization Risks

Generic types are erased during deserialization. An attacker can craft a serialized stream that, when deserialized into a generic type, creates heap pollution:

```java
// Vulnerable deserialization:
public <T> T deserialize(byte[] data) {
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
        return (T) ois.readObject();  // Unsafe cast!
    }
}
```

**Mitigation**: Use `Class<T>` type tokens and validate after deserialization.

## Type Confusion via Raw Types

Raw types in public APIs can lead to type confusion attacks:

```java
// Public API with raw type — dangerous:
public class Container {
    private List items = new ArrayList();  // Raw type
    
    public void addItem(Object item) {
        items.add(item);  // Any type accepted
    }
    
    public List getItems() {
        return items;  // Raw return type
    }
}

// Client code (potentially elsewhere in the codebase):
Container c = new Container();
c.addItem("safe");
List<String> strings = c.getItems();  // Unchecked warning ignored
// Now strings might contain non-String objects
```

## Unchecked Overrides

When overriding a generic method with a raw type, the bridge method bypasses type safety:

```java
interface Processor<T> {
    void process(T item);
}

// Malicious implementation:
class BadProcessor implements Processor<String> {
    @Override
    public void process(String item) {  // This is the bridge target
        // OK — typesafe
    }
    
    // Bridge method: process(Object) casts to String
    // If an attacker can call the bridge directly via reflection:
    public void process(Object item) {
        // Bypasses String type check!
    }
}
```

## varags + Generics

Varargs with generics can create hidden heap pollution:

```java
@SafeVarargs  // Without this, call site gets warning
public static <T> List<T> combine(List<T>... lists) {
    // lists is actually List<T>[] — but arrays and generics don't mix well
    Object[] array = lists;  // Valid because arrays are covariant
    array[0] = List.of(42);  // Heap pollution if T is String
    return lists[0];
}
```

`@SafeVarargs` asserts the method doesn't misuse the varargs — only use when the method only reads from the varargs.

## Security Best Practices

1. **Never ignore unchecked warnings** — they flag potential type confusion
2. **Use `Collections.checkedList()`** for critical type boundaries (e.g., deserialized data)
3. **Validate after deserialization** — don't trust generic casts on deserialized objects
4. **Prefer `Class<T>` tokens** over unchecked casts for type recovery
5. **Mark constructors/static factories safe** — avoid exposing raw types in APIs
6. **Use `@SafeVarargs` only when method is read-only** on the varargs
7. **Avoid generic arrays** — use `List<List<T>>` instead
8. **Enable all linting** — treat "unchecked" and "rawtypes" as errors in CI
