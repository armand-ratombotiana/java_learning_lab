# Visual Guide — Best Practices (Lab 40)

## Decision Tree for Design Patterns

```
   ┌─────────────────────────────────────┐
   │  What changes frequently?           │
   └──────────────────┬──────────────────┘
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
   Object creation          Object behavior/algorithm
          │                       │
          ▼                       ▼
   ┌──────────────┐       ┌──────────────┐
   │ Need to hide │       │ Need vary    │
   │ concrete     │       │ algorithm    │
   │ classes?     │       │ at runtime?  │
   └──────┬───────┘       └──────┬───────┘
          │ Yes                  │ Yes
          ▼                      ▼
   ┌──────────────┐       ┌──────────────┐
   │ Factory /    │       │ Strategy     │
   │ Abstract     │       │ Pattern      │
   │ Factory      │       └──────────────┘
   └──────────────┘
          │                       │
          ▼ No                    ▼ No
   ┌──────────────┐       ┌──────────────┐
   │ Need only    │       │ Need chain   │
   │ one instance?│       │ of handlers  │
   └──────┬───────┘       │ or steps?    │
          │ Yes           └──────┬───────┘
          ▼                      │ Yes
   ┌──────────────┐              ▼
   │ Singleton    │       ┌──────────────┐
   └──────────────┘       │ Chain of     │
                          │ Responsibility/
                          │ Template     │
                          └──────────────┘
```

Also consider: **Builder** (complex object construction), **Observer** (1:N notifications), **Decorator** (add behavior at runtime).

## Code Review Checklist

```
□  Functionality
   □  Does the code do what the requirement says?
   □  Are edge cases handled (null, empty, overflow)?
   □  Are there tests covering main paths AND edge cases?

□  Design & Readability
   □  Does it follow SOLID principles (especially SRP)?
   □  Are names meaningful (methods do what they say)?
   □  Are classes/methods small enough to test?

□  Performance
   □  Are there obvious N+1 queries or O(n²) loops?
   □  Are resources (DB connections, files) properly closed?
   □  Is caching applied where appropriate?

□  Security
   □  Input validated and sanitized?
   □  No SQL injection (use parameters, not concatenation)?
   □  Secrets not hard-coded or logged?

□  Style & Conventions
   □  Follows project's code style (formatting, naming)?
   □  No dead code, commented-out blocks, or TODOs without tickets?
   □  Appropriate logging level (debug vs info vs error)?
```
