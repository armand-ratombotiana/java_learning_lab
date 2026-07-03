# Visual Guide — Functional Programming

```
┌────────────────────────────────────────────────────┐
│           Pure Function                            │
│                                                    │
│  Input ──► [ deterministic logic ] ──► Output     │
│              No I/O, no mutation                   │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│              Impure Function                       │
│                                                    │
│  Input ──► [ logic + side effects ] ──► Output    │
│                    │                               │
│          ┌─────────┼─────────┐                     │
│          ▼         ▼         ▼                      │
│        Console   Database   File                    │
└────────────────────────────────────────────────────┘

┌───────── Optional Monad ─────────┐
│                                   │
│  Optional.of(x)                   │
│       │                          │
│       ▼                          │
│  map(f) ───► Optional.of(f(x))   │
│       │          OR              │
│       │          Optional.empty()│
│       ▼                          │
│  flatMap(g) ───► g(x)           │
│       │          (already flat)  │
│       ▼                          │
│  orElse(default) ───► value     │
└───────────────────────────────────┘
```
