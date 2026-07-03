# Internals: String Handling

## String Pool (String Table)
The String Pool is a fixed-size hash table (default 60013 buckets, configurable via -XX:StringTableSize) stored in the heap (Java 7+) or permgen (Java 6-).

When you write `String s = "hello"`, the JVM:
1. Checks the String Pool for "hello" (interned string)
2. If found, returns the reference
3. If not found, creates a String object in the heap and adds it to the pool

## String Concatenation Internals

### Java 8 and earlier
```java
String result = a + b + c;
```
Compiles to:
```java
new StringBuilder().append(a).append(b).append(c).toString()
```

### Java 9+
```java
String result = a + b + c;
```
Compiles to `invokedynamic` with `StringConcatFactory.makeConcatWithConstants()`. The JIT generates optimal concatenation code using MethodHandle and possibly inline the entire operation.

## String.intern()
- Returns canonical representation from the string pool
- Pool is GC-safe (strings can be collected if no references)
- Costly operation (hash lookup + potential allocation)
- Use sparingly — the string pool lives forever

## String Compression (Compact Strings, Java 9+)
- Java 6-8: String uses char[] (2 bytes per char, UTF-16)
- Java 9+: String uses byte[] with a coder flag
  - LATIN1 (ISO-8859-1): 1 byte per char (if all chars fit)
  - UTF16: 2 bytes per char
- This reduces memory by ~50% for Latin-1 strings

## StringBuilder Growth
Default capacity: 16 characters
Growth policy: (oldCapacity + 1) * 2, or actual needed size if larger
```java
new StringBuilder(initialCapacity); // Pre-size to avoid reallocation
```
