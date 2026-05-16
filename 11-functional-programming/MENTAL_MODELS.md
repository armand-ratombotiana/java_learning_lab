# Mental Models for Functional Programming

## Model 1: Mathematics Functions

```
f(x) = x + 2

- Always produces same output for same input
- No hidden state
- Can't modify input
```

Think of FP functions like mathematical functions.

## Model 2: Pipeline

```
Input → [Step 1] → [Step 2] → [Step 3] → Output
         filter      map         collect
```

Data flows through transformations. Each step transforms and passes to next.

## Model 3: Data as Flow, Not Storage

Traditional: Data stored in objects, methods manipulate

FP: Data flows through functions, transformations applied, new data emerges

## Model 4: Immutability = Safety

```
Immutable:     x = x + 1    // Creates NEW value, original unchanged
Mutable:       x++          // Changes x's value directly

If no one can change data, no race conditions in parallel code
```

## Model 5: Building Blocks

```
Small functions:
- filter: keep what matches
- map: transform each element  
- reduce: combine all into one
- flatMap: expand and combine

Compose these to build complex operations
```

## Key Insight

FP is about treating computation as evaluation of mathematical functions, avoiding changing state and mutable data.