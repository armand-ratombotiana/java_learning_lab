# How Lambda Expressions Work

## Compilation

At compile time, lambdas are converted to:

1. **Method references** → direct method references
2. **Lambda body** → generated method in containing class
3. **Target type** → functional interface implementation

## Bytecode Generation

```java
// Source code
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.sort((a, b) -> a.compareTo(b));
```

Compiles to roughly:
- A synthetic method in the containing class
- An anonymous class implementing Comparator (pre-Java 8) or
- invokedynamic instruction (Java 8+)

## Type Inference

Java infers types from context:

```java
// Full syntax
Function<String, Integer> f = (String s) -> s.length();

// Type inference
Function<String, Integer> f = s -> s.length();

// Even shorter with target typing
list.stream().map(s -> s.length()) // infers Integer
```

## Capturing Variables

Lambdas can access:
- **Instance variables**: Always allowed
- **Static variables**: Always allowed
- **Local variables**: Must be effectively final

```java
int factor = 2; // effectively final
Function<Integer, Integer> multiply = x -> x * factor; // captures factor
```

## Method References

Four types:

| Type | Example | Equivalent |
|------|---------|------------|
| Static | `Integer::parseInt` | `x -> Integer.parseInt(x)` |
| Instance (specific) | `String::length` | `x -> x.length()` |
| Instance (arbitrary) | `String::compareTo` | `(a, b) -> a.compareTo(b)` |
| Constructor | `ArrayList::new` | `() -> new ArrayList<>()` |