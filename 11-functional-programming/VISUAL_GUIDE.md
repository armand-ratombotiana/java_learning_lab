# Visual Guide to Functional Programming

## Core Operations

```
┌─────────────────────────────────────────────────────────────┐
│                    STREAM PIPELINE                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Source ──► Intermediate ──► Terminal                     │
│             Operations       Operations                   │
│                                                             │
│   .stream()  .filter()      .collect()                     │
│             .map()          .reduce()                      │
│             .flatMap()      .forEach()                     │
│             .sorted()       .findFirst()                   │
│             .distinct()     .count()                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Pure Function Diagram

```
┌─────────────────────────────────────────────────────┐
│            PURE FUNCTION                            │
├─────────────────────────────────────────────────────┤
│                                                     │
│   Input ─────────┐                                  │
│                  ▼                                  │
│              ┌─────────┐                             │
│              │ f(x)    │  No side effects!          │
│              └─────────┘                             │
│                  │                                  │
│                  ▼                                  │
│              Output                                 │
│                                                     │
│   Same Input → Same Output (always)                │
└─────────────────────────────────────────────────────┘
```

## Imperative vs Functional

```
IMPERATIVE:                    FUNCTIONAL:
─────────────                  ────────────
Set result to empty       →    result = list.stream()
For each element          →    .filter(e -> e > 5)
  If element > 5          →    .map(e -> e * 2)
    result.add(e * 2)     →    .collect(toList())
Return result                                       
```

## Function Composition

```
f(x) = x * 2
g(x) = x + 1

h = f.compose(g)  →  h(x) = f(g(x)) = (x+1) * 2
h = f.andThen(g)  →  h(x) = g(f(x)) = x * 2 + 1

Input 5:
- compose: (5+1) * 2 = 12
- andThen: 5 * 2 + 1 = 11
```

## Transformation Pipeline

```
Original:   [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

filter(x > 3)   → [4, 5, 6, 7, 8, 9, 10]

map(x * 2)      → [8, 10, 12, 14, 16, 18, 20]

reduce(0, +)   → 98
```