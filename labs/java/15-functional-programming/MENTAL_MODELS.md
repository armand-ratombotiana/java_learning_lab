# Mental Models — Functional Programming

## Function as Data Pipeline
```
Input → [pure fn] → Intermediate → [pure fn] → Output
```
No side passages — what goes in determines what comes out.

## Immutability as Snapshot
An immutable object is a snapshot of data at a point in time. New snapshots replace old ones; the old ones never change.

## Optional as a Box
`Optional<T>` is a box that either holds a value or is empty. You can only interact with the value by providing a function to `map` or `flatMap`.

## Combinator as Lego Brick
Small functions snap together to build larger structures:
```java
var pipeline = getData
    .andThen(validate)
    .andThen(enrich)
    .andThen(format);
```

## No Side Effects as Receipt
The function gives you a receipt (the return value). If you ignore it, nothing happens. No implicit I/O, no database writes.
