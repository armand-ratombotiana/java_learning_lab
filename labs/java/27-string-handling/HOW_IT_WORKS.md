# How String Handling Works

## String Pool Internals
```java
String s1 = "hello";      // Pooled
String s2 = "hello";      // Same pooled reference
String s3 = new String("hello");  // Heap, not pooled
String s4 = s3.intern();  // Pooled (same as s1)

s1 == s2  // true (same pool reference)
s1 == s3  // false (heap vs pool)
s1 == s4  // true (interned to pool)
```

## StringBuilder Mechanics
```java
StringBuilder sb = new StringBuilder();  // Default capacity 16
sb.append("Hello ");    // Appends, grows if needed
sb.append("World!");    // Reuses same buffer
String result = sb.toString();  // Creates final String
```

## Text Block Processing
The compiler:
1. Determines common leading whitespace
2. Removes that whitespace from each line
3. Removes trailing whitespace on each line
4. Replaces `\n` with actual newlines
5. Processes escape sequences

## String Concatenation Optimization
The Java compiler optimizes `+` concatenation into StringBuilder chains. However, in loops, each iteration creates a new StringBuilder, making explicit StringBuilder more efficient.
