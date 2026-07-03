# Mental Models for Pattern Matching

## The Lock and Key Model

Think of pattern matching as a **lock and key** mechanism. Each pattern is a lock with a specific shape, and the value is a key:

- `String s` is a lock that only accepts keys that are `String` instances
- `Integer i when i > 0` is a lock that accepts positive `Integer` keys
- `Point(int x, int y)` is a lock that accepts `Point` keys and splits them into two sub-keys

When a key fits a lock, it opens the door (executes the branch) and the key's internal pins (components) are revealed as bound variables.

## The Shape Sorter Model

Imagine a **shape sorter** toy for children — a box with holes of different shapes:
- Circle holes only accept circular blocks
- Square holes only accept square blocks
- Star holes only accept star blocks

Pattern matching works the same way. Each `case` is a hole in the sorter:

```java
switch (obj) {
    case Circle c -> ...     // Round hole
    case Rectangle r -> ...  // Square hole
}
```

If you try to sort a triangle, none of the holes fit, and the toy needs a default slot (or tells you it doesn't fit / compile error if the set of shapes is exhaustively specified).

## The Dismantling Model

Record patterns are like having a **dismantling station** that automatically takes objects apart into their component pieces:

```java
if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    // Automatically dismantled into x1, y1, x2, y2
}
```

Without pattern matching, you would manually dismantle:
```java
if (obj instanceof Line) {
    Line line = (Line) obj;
    Point start = line.start();
    int x1 = start.x();
    int y1 = start.y();
    Point end = line.end();
    int x2 = end.x();
    int y2 = end.y();
}
```

Pattern matching does all this in one step.

## The Guarded Door Model

A guard (`when` clause) is like having a **security guard** at the door. The shape fits, but the guard also checks your ID:

```java
case Person p when p.age() >= 18 -> grantAccess(p);
// The person fits through the "Person" door, then the guard checks if they're 18+
```

The guard only asks for ID after the door opens (the type matches). This is more efficient than having the guard at the front of the door.

## The Nested Doll Model

Nested record patterns are like **Matryoshka dolls** — each doll contains another doll inside:

```
Line(Point(int x1, int y1), Point(int x2, int y2))
```

Opening the `Line` reveals two `Point` dolls. Opening each `Point` reveals two `int` values. Pattern matching opens all layers at once.

## The Exhaustiveness Model

Think of exhaustiveness as the **logical complement** of a set. For a sealed type `Shape = Circle | Rectangle | Triangle`, the complement of `{Circle, Rectangle}` is `{Triangle}`. The compiler calculates this complement and ensures it's empty (all cases covered) or flags an error.

## The Pattern Ordering Model

Patterns are like a **waterfall** — the value flows down through each case and stops at the first one that matches:

```
Value: Integer(42)
  ↓
  case String s → (doesn't match, flows down)
  ↓
  case Integer i when i > 0 → (matches! executed here)
  ↓
  case Integer i → (skipped, already matched)
  ↓
  default → (skipped, already matched)
```

The ordering matters because patterns are checked in declaration order. More specific patterns (with guards) must precede more general patterns.
