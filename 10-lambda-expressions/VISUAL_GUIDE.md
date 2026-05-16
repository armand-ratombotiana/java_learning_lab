# Visual Guide to Lambda Expressions

## Syntax Variations

```
┌─────────────────────────────────────────────────┐
│             Lambda Expression Syntax             │
├─────────────────────────────────────────────────┤
│                                                 │
│  (parameters) -> expression                     │
│     │        │       │                          │
│     │        │       └── Single expression     │
│     │        │            (no braces)          │
│     │        └── Arrow operator                │
│     └── Formal parameters                       │
│                                                 │
└─────────────────────────────────────────────────┘

Examples:

() -> 42                      // No params, returns 42
x -> x * 2                   // One param, expression
(int x, int y) -> x + y     // Typed params
(String s) -> {             // Block body
    System.out.println(s);
    return s.toLowerCase();
}
```

## Functional Interface Hierarchy

```
java.util.function
│
├── Function<T, R>
│   ├── UnaryOperator<T>
│   └── BinaryOperator<T>
├── Predicate<T>
│   └── BiPredicate<T, U>
├── Consumer<T>
│   └── BiConsumer<T, U>
└── Supplier<T>
```

## Lambda vs Method Reference

```
┌────────────────────────────────────┐
│  Lambda: s -> s.toUpperCase()      │
│  Method Ref: String::toUpperCase   │
│                                     │
│  Equivalent - same behavior        │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│  Lambda: () -> new HashSet<>()     │
│  Method Ref: HashSet::new          │
│                                     │
│  Constructor reference            │
└────────────────────────────────────┘
```

## Stream Operation Pipeline

```
List → stream() → [filter] → [map] → [collect] → Result
              │        │       │
              │        │       └── Transformation (Function)
              │        └── Predicate (filter condition)
              └── Source (Collection)