# Edge Cases & Pitfalls: Specialized Collections

Specialized collections are powerful, but their unique behaviors can cause severe bugs if developers assume they act like standard `HashMap`s or `HashSet`s.

## 1. The WeakHashMap Value Trap
*   **The Scenario**: You use a `WeakHashMap` to cache data. You put a key `K` and a value `V` into the map.
*   **The Pitfall**: `WeakHashMap` only makes the *keys* weak. The values are strongly referenced by the map. If the value `V` contains a strong reference back to the key `K` (either directly or indirectly), it creates a circular reference. Because the map holds a strong reference to `V`, and `V` holds a strong reference to `K`, the key `K` will *never* be garbage collected, defeating the entire purpose of the `WeakHashMap` and causing a memory leak.
*   **Mitigation**: Ensure that values in a `WeakHashMap` do not hold strong references back to their keys. If they must, you need to wrap the values in `WeakReference`s manually.

## 2. WeakHashMap String Literal Keys
*   **The Scenario**: You use a `String` literal as a key in a `WeakHashMap`: `map.put("myKey", data)`.
*   **The Pitfall**: String literals are placed in the String Pool, which is maintained by the JVM. Strings in the pool are strongly referenced by the JVM itself. Therefore, a String literal key will *never* be garbage collected, and the entry will remain in the `WeakHashMap` forever.
*   **Mitigation**: Only use dynamically created objects (e.g., `new String("myKey")` or domain objects) as keys in a `WeakHashMap`.

## 3. IdentityHashMap Contract Violation
*   **The Scenario**: You use `IdentityHashMap` but pass it to a method that expects a standard `Map` and relies on `equals()` behavior.
*   **The Pitfall**: `IdentityHashMap` intentionally violates the `Map` contract. If you do `map.put(new String("A"), 1)` and then `map.get(new String("A"))`, it will return `null`. A standard `HashMap` would return `1`. If external code doesn't know it's interacting with an `IdentityHashMap`, it will fail to find keys it expects to be present.
*   **Mitigation**: Isolate `IdentityHashMap` usage strictly within the bounds of algorithms that require it (like serialization traversal). Do not expose it as a general-purpose `Map` in public APIs.

## 4. EnumSet / EnumMap Null Rejection
*   **The Scenario**: You try to add `null` to an `EnumSet` or use `null` as a key in an `EnumMap`.
*   **The Pitfall**: Both `EnumSet` and `EnumMap` strictly forbid `null` elements/keys. Attempting to use `null` will throw a `NullPointerException`.
*   **Mitigation**: Validate data before adding it to enum-based collections. (Note: `EnumMap` *does* allow `null` values, just not `null` keys).

## 5. EnumMap Initialization
*   **The Scenario**: You declare an `EnumMap` using the default constructor interface: `Map<State, String> map = new EnumMap<>();`
*   **The Pitfall**: This will not compile. Because `EnumMap` uses an array internally, it must know the exact `Enum` class at instantiation time so it knows how large to make the array (based on the number of enum constants).
*   **Mitigation**: You must pass the class token to the constructor: `new EnumMap<>(State.class)`.