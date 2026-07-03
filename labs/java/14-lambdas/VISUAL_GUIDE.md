# Visual Guide — Lambdas

```
┌───────────────────────────────────────────────────────────┐
│  Lambda Anatomy                                           │
│                                                           │
│  (parameters)  ->  { body statements }                    │
│  ──┬───────┘    │    ───┬─────────────┘                  │
│    │             │        │                               │
│    │             │        └── expression or block         │
│    │             │                                        │
│    │             └── arrow token (required)               │
│    │                                                      │
│    └── parameter list (optional parens for single param) │
└───────────────────────────────────────────────────────────┘

┌──────────────┐     ┌──────────────────────┐
│ Anonymous    │ ──► │ Lambda               │
│ Inner Class  │     │                      │
│              │     │  More concise        │
│ Verbose      │     │  Captures variables  │
│ Generates    │     │  No .class file      │
│ .class file  │     │  (invokedynamic)     │
└──────────────┘     └──────────────────────┘

Functional Interface
    Predicate<T>  ────  test(T) : boolean
    Function<T,R> ────  apply(T) : R
    Consumer<T>   ────  accept(T) : void
    Supplier<T>   ────  get() : T
```
