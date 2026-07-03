# Arrays & Strings — Theoretical Foundation

## Arrays

An array is a contiguous block of memory holding elements of the same type. Arrays are objects in Java — they have a `length` field and inherit from `Object`.

### Array Types

```java
int[] numbers = new int[5];         // Single-dimensional array of 5 ints
int[][] matrix = new int[3][4];     // 2D array: 3 rows, 4 columns
int[][] jagged = new int[3][];      // Jagged array: rows can have different lengths
int[][][] cube = new int[3][4][5];  // 3D array
```

### Array Initialization

```java
int[] a = {1, 2, 3};                    // Anonymous array initializer
int[] b = new int[]{1, 2, 3};          // Equivalent
int[] c = new int[3];                   // All elements default to 0
```

### Array Characteristics

- Fixed size after creation — cannot grow or shrink
- Zero-indexed: elements at positions 0 through length-1
- Bounds checking: `ArrayIndexOutOfBoundsException` for invalid indices
- `length` is a field, not a method
- Covariant: `String[]` IS-A `Object[]` (but array store check prevents type violations)

## Strings

`String` is immutable — any operation that appears to modify a String creates a new one.

### String Pool

String literals are interned in the String pool (heap memory):

```java
String s1 = "hello";
String s2 = "hello";
System.out.println(s1 == s2); // true — same object in the pool

String s3 = new String("hello");
System.out.println(s1 == s3); // false — different object on heap
```

### String Immutability Benefits

- Thread-safe without synchronization
- Allows String pool for memory efficiency
- Secure (passwords should be `char[]`, not `String`)
- Hash code can be cached: `String` caches its hash code after first computation

### Common String Operations

| Method | Description |
|--------|-------------|
| `length()` | Number of characters |
| `charAt(i)` | Character at index i |
| `substring(begin, end)` | Substring range |
| `indexOf(s)`, `lastIndexOf(s)` | Search for substring |
| `equals(s)`, `equalsIgnoreCase(s)` | Content comparison |
| `compareTo(s)` | Lexicographic comparison |
| `toLowerCase()`, `toUpperCase()` | Case conversion |
| `trim()`, `strip()` | Whitespace removal |
| `split(regex)` | Split into array |
| `join(delimiter, elements)` | Join array into string |
| `format(format, args)` | Formatted string |
| `matches(regex)` | Regex match |
| `replace(old, new)`, `replaceAll(regex, s)` | Replacement |
| `valueOf(x)` | Convert primitive to String |

## StringBuilder and StringBuffer

Both are mutable character sequences. `StringBuilder` is faster (no synchronization). `StringBuffer` is thread-safe (synchronized methods).

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
String result = sb.toString();  // "Hello World"
```

| Feature | String | StringBuilder | StringBuffer |
|---------|--------|---------------|--------------|
| Immutable | Yes | No | No |
| Thread-safe | Yes (immutable) | No | Yes |
| Performance | Slow for modifications | Fast | Moderate |
| Introduced | Java 1.0 | Java 5 | Java 1.0 |
