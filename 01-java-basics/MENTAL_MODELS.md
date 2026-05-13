# Mental Models for Java Basics

## Variable as Container

```
┌─────────────────┐
│   int age = 25  │
│  ┌─────┬────┐   │
│  │ age │ 25 │   │
│  └─────┴────┘   │
└─────────────────┘
```

Think of variables as labeled boxes that hold values.

## Control Flow as Decision Tree

```
        Start
          │
          ▼
    ┌───────────┐
    │ Condition │────No────► Next Path
    └─────┬─────┘
         Yes
          ▼
    ┌───────────┐
    │ Code Block│────► Continue
    └───────────┘
```

## Method as Recipe

```
Input (Ingredients) → Method (Recipe) → Output (Dish)

add(5, 3)  ──►  [5 + 3]  ──►  8
```

## Array as Row of Lockers

```
Index:  0   1   2   3   4
       ┌───┬───┬───┬───┬───┐
Value: │ A │ B │ C │ D │ E │
       └───┴───┴───┴───┴───┘
```

Each locker has a number (index) and holds one item.

## Stack Memory vs Heap

```
STACK                          HEAP
┌─────────────┐               ┌─────────────┐
│ main()      │               │ String obj  │
│ x = 5       │               │ "hello"     │
│ arr ref ───────────────►    └─────────────┘
└─────────────┘
```

Reference variables point to objects in heap.