# Performance in Java Basics

## String Concatenation

```java
// SLOW - creates many intermediate strings
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // O(n²) time complexity
}

// FAST - uses StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

## Array Performance

```java
// Pre-size arrays when possible
int[] arr = new int[1000];  // allocate once

// Avoid resizing in loops
List<String> list = new ArrayList<>(1000);  // initial capacity
```

## Loop Optimization

```java
// SLOW - calls size() every iteration
for (int i = 0; i < list.size(); i++) { }

// FAST - cache size
for (int i = 0, n = list.size(); i < n; i++) { }

// FASTEST - use for-each when possible
for (String s : list) { }
```

## Primitive vs Wrapper Types

```java
// Use primitives for calculations (faster)
int sum = 0;
for (int i = 0; i < 1000; i++) {
    sum += i;
}

// Use wrapper types for collections
List<Integer> numbers = new ArrayList<>();
```

## Stack vs Heap

```java
// Primitives: stored in stack (fast allocation)
int x = 5;

// Objects: stored in heap (slower, needs GC)
String s = "hello";

// Arrays of primitives: data in heap, reference in stack
int[] arr = {1, 2, 3};  // array data in heap
```

## Common Performance Anti-Patterns

1. **Unnecessary autoboxing**: `List<Integer>` with int operations
2. **String concatenation in loops**: Using + instead of StringBuilder
3. **Creating unnecessary objects**: `new String("")` instead of `""`
4. **Not using StringBuilder for complex concatenation