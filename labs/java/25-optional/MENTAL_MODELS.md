# Mental Models for Optional

## The Gift Box Model

Think of `Optional<T>` as a **gift box**:
- The box can contain a present (the value) or be empty
- Before you can use the present, you must open the box
- The box has safe ways to handle both cases:
  - `map`: Transform the present if it's there, leave the box empty if not
  - `filter`: Check if the present meets criteria, return empty box if not
  - `orElse`: If the box is empty, use this pre-wrapped replacement
  - `orElseGet`: If the box is empty, go buy a replacement
  - `orElseThrow`: If the box is empty, complain to management

The key insight: you NEVER reach into the box without a plan for what to do if it's empty.

## The SQL NULL Model

Think of `Optional` like **SQL's NULL** semantics:
- Any operation on NULL produces NULL (except explicit NULL-handling)
- `COALESCE` / `IFNULL` = `orElse`
- `NULLIF` = `filter`
- `CASE WHEN ... THEN ... ELSE ... END` = `map`/`flatMap` with `orElse`

```sql
-- SQL: Handle missing middle initial
SELECT COALESCE(middle_initial, '') FROM users;

-- Java Optional equivalent:
Optional.ofNullable(user.middleInitial).orElse("");
```

## The Maybe Monad Model

Optional is a **monad** — a design pattern from functional programming:
- It wraps a value (the computation result)
- `map`: Apply a function to the value, keeping the monadic structure
- `flatMap`: Apply a function that returns a new monad, avoiding nesting
- It handles a cross-cutting concern (absence) transparently

The mental model for chaining:
```
Optional.of(x)
    .map(f)     // If x exists, apply f(x); otherwise stay empty
    .filter(p)  // If f(x) exists and passes p, keep it; otherwise empty
    .flatMap(g) // If still exists, apply g(x) which returns Optional; unwrap one level
    .orElse(d)  // If still exists, return it; otherwise return d
```

## The Elevator Model

Think of Optional as an **elevator**:
- The elevator can have a passenger (value) or be empty
- `map` is like the elevator going to a floor — the passenger is transformed
- `flatMap` is like a transfer — the passenger gets out and enters a different elevator system
- `filter` is like a security check — if the passenger doesn't qualify, they're removed
- `orElse` is a waiting replacement passenger
- `orElseThrow` is a trap door if the elevator arrives empty

## The Circuit Breaker Model

Optional can be seen as a **circuit breaker** in an electrical system:
- The circuit is closed (present) when there's a value
- The circuit is open (absent/empty) when there's no value
- Operations (map, filter) only pass through when the circuit is closed
- `orElse` is a bypass switch that provides alternative current
- `orElseThrow` is a fuse that blows when the circuit is open

## The Railway Switch Model

Optional operations are like a **railway switch**:
- A train (the computation) travels along tracks
- At each switch (operation), the train either continues on the main track or gets diverted to an empty track
- `map`: Transform the train's cargo
- `filter`: Divert to empty track if cargo doesn't match
- `flatMap`: Transform the entire train to a different railway system
- `orElse`: If on empty track, teleport to the destination with a default cargo
- `orElseThrow`: If on empty track, derail the train

## The null as Optional.empty() Model

The simplest model: **Optional.empty() is the new null**. Every time you would have written:
```java
if (x != null) {
    doSomething(x);
}
```

You now write:
```java
Optional.ofNullable(x).ifPresent(this::doSomething);
```

Every function that might not return a value should return Optional.
