# Architecture — Functional Programming

## Core Abstractions
```
Data → Function → Data → Function → Data
(Pure)           (Pure)
```

## Three-Layer Architecture
```
[Data Sources] → [Pure Domain Logic] → [Effects / Output]
                     │
            ┌────────┴────────┐
            ▼                 ▼
       Composable         Testable
       Functions          in isolation
```

## Monad Usage in Architecture
| Monad | Role |
|-------|------|
| `Optional` | Absent values |
| `Stream` | Multiple values |
| `CompletableFuture` | Async values |
| `Either` (Vavr) | Error handling |

## Combinator Architecture
```
Validator<String> nonEmpty
Validator<String> minLength
Validator<String> maxLength
        │
        ▼
Validator<String> full = nonEmpty.and(minLength).and(maxLength)
```

## Avoiding the "Functional Core, Imperative Shell"
Wrap pure domain logic with thin imperative layers for I/O, database, and UI:
```
[Imperative Shell] → [Functional Core] → [Imperative Shell]
     (I/O)              (Pure logic)        (I/O)
```
