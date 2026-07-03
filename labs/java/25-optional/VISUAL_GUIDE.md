# Visual Guide to Optional

## State Machine

```
     Optional.of(x)           Optional.empty()
          │                         │
          ▼                         ▼
  ┌──────────────┐          ┌──────────────┐
  │  Present     │          │  Empty       │
  │  value = x   │          │  value = null│
  └──────┬───────┘          └──────┬───────┘
         │                         │
         │ map(f)                  │ map(f)
         ▼                         ▼
  ┌──────────────┐          ┌──────────────┐
  │  Optional    │          │  Optional    │
  │  of f(x)    │          │  empty       │
  └──────────────┘          └──────────────┘
```

## Decision Flow

```
                    ┌─────────┐
                    │  Start  │
                    └────┬────┘
                         │
              ┌──────────▼──────────┐
              │   Value exists?     │
              └──────┬──────┬───────┘
                     │      │
                   YES     NO
                     │      │
                     ▼      ▼
              ┌─────────┐ ┌──────────┐
              │ Present │ │  Empty   │
              └────┬────┘ └────┬─────┘
                   │           │
      ┌────────────┤           ├──────────────┐
      │            │           │              │
      ▼            ▼           ▼              ▼
  ┌───────┐  ┌─────────┐ ┌────────┐  ┌──────────────┐
  │ map() │  │orElse() │ │filter()│  │ orElseThrow()│
  │apply f│  │return d │ │pred    │  │ throw ex     │
  └───────┘  └─────────┘ └────────┘  └──────────────┘
```

## Optional Chaining Pipeline

```
Optional.of("hello")
    │
    ├── map(String::length) ────────────────── Optional.of(5)
    │
    ├── filter(n -> n > 3) ────────────────── Optional.of(5)
    │    filter(n -> n > 10) ──────────────── Optional.empty()
    │
    ├── or(() -> Optional.of(0)) ──────────── Optional.of(5) (not evaluated)
    │    (if empty) ────────────────────────── Optional.of(0)
    │
    ├── orElse(0) ─────────────────────────── 5
    │
    ├── orElseGet(() -> compute()) ────────── 5 (not evaluated)
    │
    ├── orElseThrow() ─────────────────────── 5
    │    (if empty) ───────────────────────── throws NoSuchElementException
    │
    └── ifPresent(System.out::println) ────── prints "hello"
         ifPresentOrElse(cons, run) ────────── runs consumer
```

## Optional Creation

```
          value
        ╱       ╲
    non-null    null
       │         │
       ▼         ▼
  Optional.of  Optional.empty()
    (present)    (empty)

  Optional.ofNullable(value):
    ├─ non-null → Optional.of(value)
    └─ null     → Optional.empty()
```

## Optional Flow with Streams

```
List<Optional<String>> list = [opt("A"), empty, opt("B")]
    │
    ├── .stream()
    │
    ├── .flatMap(Optional::stream)  ── Stream("A", "B")
    │
    ├── .filter(Optional::isPresent) ── Stream("A", "B")
    │    .map(Optional::get)
    │
    └── .toList() ────────────────── ["A", "B"]
```

## orElse vs orElseGet

```
orElse(defaultValue):
    ┌───────────────┐
    │ defaultValue  │◄── ALWAYS evaluated, even if present
    │ is computed   │
    └───────┬───────┘
            │
            ├─ present → return value
            └─ empty   → return defaultValue

orElseGet(supplier):
    ┌───────────────┐
    │ supplier      │◄── Only called if empty
    │ is lazy       │
    └───────┬───────┘
            │
            ├─ present → return value (supplier NOT called)
            └─ empty   → compute and return
```
