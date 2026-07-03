# Methods — Visual Guide

## Method Anatomy

```
 ┌─ Access modifier
 │   ┌─ Optional modifier
 │   │  ┌─ Return type
 │   │  │      ┌─ Method name
 │   │  │      │    ┌─ Parameter list
 │   │  │      │    │
public static int max(int a, int b) throws IllegalArgumentException {
 │   │  │      │    │                              └─ Exception declaration
 │   │  │      │    └─ Parameters: type + name
 │   │  │      └─ camelCase, verb-phrase
 │   │  └─ void/type
 │   └─ static/final/abstract/synchronized
 └─ public/private/protected
```

## Call Stack Visualization

```
main() calls greet() calls format()

Stack (grows down):
┌───────────────────┐
│ format()          │  ← current execution
│ args: ["Hello"]   │
│ local: result     │
├───────────────────┤
│ greet()           │
│ name: "Alice"     │
│ local: formatted  │
├───────────────────┤
│ main()            │
│ args: String[]    │
│ local: message    │
├───────────────────┤
│   (bottom)        │
└───────────────────┘
```

## Method Overloading

```
Method: print
├── print(int x)        → "Printing int: 5"
├── print(String s)     → "Printing string: Hello"
├── print(double d)     → "Printing double: 3.14"
├── print(int x, int y) → "Printing two ints: 5, 10"
└── print(int... nums)  → "Printing array: [1, 2, 3]"

Resolution order: exact match → widening → autoboxing → varargs
```

## Pass-by-Value Diagram

```
Primitive:
main:  int a = 5;
       ┌───┐
       │ 5 │  a
       └───┘
           │
           │ call method(a)
           ▼
method: int x = a;  // x gets COPY of 5
       ┌───┐
       │ 5 │  x
       └───┘

Reference:
main:  Person p = new Person("Alice");
       ┌──────┐     ┌──────────────────┐
       │  ref ├────→│ Person("Alice")  │
       └──────┘     └──────────────────┘
           │
           │ call method(p) — ref is copied
           ▼
method: Person obj = p;
       ┌──────┐     ┌──────────────────┐
       │  ref ├────→│ Person("Alice")  │  (same object!)
       └──────┘     └──────────────────┘
```
