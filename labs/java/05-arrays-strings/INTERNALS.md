# Arrays & Strings — Internal Mechanics

## Array Object Header

On HotSpot JVM (compressed OOPs):
```
[Mark word: 8 bytes] [Klass pointer: 4 bytes] [Length: 4 bytes] [Elements...]
```
Total overhead per array: 16 bytes minimum.

## Array Covariance Trap

```java
String[] strings = new String[3];
Object[] objects = strings;  // OK — arrays are covariant
objects[0] = 42;  // ArrayStoreException at runtime!
```

The JVM tracks the actual component type and checks at each store.

## Compact Strings (Java 9+)

`String` internal storage changed from `char[]` (2 bytes per char) to `byte[]` with a `coder` flag:
- Latin-1 (ISO-8859-1): 1 byte per char
- UTF-16: 2 bytes per char

```java
// Java 8 String internals:
private final char[] value;

// Java 9+ String internals:
private final byte[] value;
private final byte coder;  // 0 = LATIN1, 1 = UTF16
```

## StringBuilder Internals

Internal `char[]` (Java 8) or `byte[]` (Java 9+) array that grows as needed:
- Default capacity: 16
- Growth: `(oldCapacity * 2) + 2` when exceeded
- `toString()` creates a copy — don't modify StringBuilder after calling toString()

## String.intern()

Returns canonical representation from the String pool. Use sparingly — the pool is a hashtable in heap that can cause performance issues if too large.
