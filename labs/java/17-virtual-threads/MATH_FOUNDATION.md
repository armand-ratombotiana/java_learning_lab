# Mathematical Foundation — Virtual Threads

## M:N Scheduling Model
```
M virtual threads  →  N carrier threads  →  P CPU cores
(M >> N ≥ P)
```

## Capacity Formula
```
Max concurrent virtual threads ≈ Available RAM / Continuation size
Continuation size ≈ Stack depth × Frame size (tens of KB)
```

## Amdahl's Law with Pinning
If a fraction P of execution is pinned (cannot yield), speedup is limited:
```
Speedup = 1 / ((1 - P) + P) = 1 // pinned = serial
```
**Implication:** Minimise `synchronized` in virtual thread code — use `ReentrantLock`.

## Structured Concurrency Guarantee
A `StructuredTaskScope` ensures:
- If a subtask fails, other subtasks are cancelled (short-circuit)
- Scope blocks until all subtasks complete
- Subtask lifetime is nested within scope lifetime
