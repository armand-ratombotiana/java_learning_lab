# Type Erasure

## What is Type Erasure?
Type erasure is the process where generic type information is removed at compile time. The Java compiler replaces generic types with their bounded type or Object.

## How It Works

**Before Compilation:**
```java
List<String> strings = new ArrayList<>();
```

**After Compilation (Runtime):**
```java
List strings = new ArrayList<>();
```

## Erasure Rules

| Generic Type | Erasure |
|--------------|---------|
| `<T>` | `Object` |
| `<T extends Number>` | `Number` |
| `<T extends Comparable>` | `Comparable` |
| `<?>` | `Object` |

## Implications

### 1. Runtime Type Information Lost
```java
List<String> list = new ArrayList<>();
List<Integer> list2 = new ArrayList<>();
// list.getClass() == list2.getClass() at runtime
```

### 2. Cannot Instantiate Generic Types
```java
new T(); // Compile error - impossible at runtime
new T[10]; // Same issue
```

### 3. Cannot Use Primitive Types
```java
List<int> invalid; // Must use Integer
List<double> invalid; // Must use Double
```

### 4. Method Overloading
```java
void process(List<String>) {} // Cannot coexist with
void process(List<Integer>) {} // Due to erasure, both become process(List)
```

## Workarounds

1. **Factory with Class:** `Class<T> type` parameter
2. **TypeToken (Guava):** Captures generic type at runtime
3. **GenericArray creation:** Use reflection with `Array.newInstance()`