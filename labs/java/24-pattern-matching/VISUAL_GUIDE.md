# Visual Guide to Pattern Matching

## instanceof Pattern Matching Flow

```
Object obj = "Hello, World!"

if (obj instanceof String s) {
    │             │        │
    │             │        └── Binding variable (String s)
    │             └── Pattern type (String)
    └── Test value
    
  Step 1: Is obj instanceof String? → YES
  Step 2: Cast obj to String → s = (String) obj
  Step 3: s.length() → 13
}
```

## Switch Pattern Matching Execution

```
switch (obj) {                          obj = "Hello" (String)
    ┌─────────────────────────────────────┐
    │ case Integer i                      │  ← instanceof Integer? No
    │   ↓                                 │
    │ case String s when s.length() > 10  │  ← instanceof String? Yes
    │   → guard: "Hello".length() > 10?   │  ← 5 > 10? No
    │   ↓                                 │
    │ case String s                       │  ← Matched! Execute body
    │   → process(s)                      │
    └─────────────────────────────────────┘
```

## Record Pattern Deconstruction

```
Object obj = new Line(
    new Point(3, 4),
    new Point(10, 20)
)

if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    
    Level 1:    Line
               ╱     ╲
    Level 2: Point    Point
             ╱  ╲    ╱   ╲
    Level 3: x1  y1  x2   y2
              3   4   10   20

    Result: x1=3, y1=4, x2=10, y2=20
}
```

## Pattern Dominance Visualization

```
Order matters! Patterns checked in sequence:

  case String s when s.length() > 5    ── Most specific
  case String s                         ── Covers all strings
  case CharSequence cs                  ── Covers all char sequences
  case Object o                         ── Covers everything
  (default)

  A later pattern must not be dominated
  by an earlier pattern:

  ✓ case String s → case Object o      — OK (String ⊂ Object)
  ✗ case Object o → case String s      — ERROR (String dominated by Object)
```

## Guarded Pattern Decision Tree

```
                   ┌──────┐
                   │ obj  │
                   └──┬───┘
                      │
                  instanceof?
                 ╱           ╲
              YES             NO
               │               │
            Guard?         next case
           ╱       ╲
       TRUE        FALSE
         │           │
    Execute       next case
    case body      (fall through)
```

## Exhaustiveness Check

```
sealed interface Shape
  permits Circle, Rectangle, Triangle {}

Leaf types: {Circle, Rectangle, Triangle}

switch (shape) {
    case Circle c:    ✓
    case Rectangle r: ✓
    case Triangle t:  ✓
    ──────────────────
    All leaf types covered → EXHAUSTIVE ✓

switch (shape) {
    case Circle c:    ✓
    case Rectangle r: ✓
    ──────────────────
    Missing: Triangle → NOT EXHAUSTIVE ✗
}
```

## Null Handling

```
switch (obj) {
    ┌─ case null ──────────────────┐
    │  Matches only null values    │
    │  Must be listed explicitly   │
    └──────────────────────────────┘
    ┌─ case String s ──────────────┐
    │  Matches non-null Strings    │
    └──────────────────────────────┘
    ┌─ default ────────────────────┐
    │  Catch-all (including null   │
    │  if null case absent → NPE)  │
    └──────────────────────────────┘
}
```
