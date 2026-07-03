# Mathematical Foundation — Concurrency

## Amdahl's Law
```
Speedup = 1 / ((1 - P) + P/N)
```
Where P = parallel portion, N = cores. Limits scalability.

## Happens-Before
If action X happens-before action Y, then effects of X are visible to Y:
1. **Program order rule** — each action in a thread happens-before every later action in that thread
2. **Monitor lock rule** — unlock happens-before every subsequent lock on the same monitor
3. **Volatile rule** — write to volatile happens-before every subsequent read
4. **Thread start rule** — `Thread.start()` happens-before any action in started thread
5. **Thread join rule** — last action in thread happens-before `Thread.join()` returns

## Consensus Number
| Object | Consensus Number |
|--------|----------------|
| Atomic register | 1 |
| CAS (compare-and-swap) | ∞ |
| Synchronized | ∞ (via CAS) |

## Deadlock Conditions (Coffman)
1. Mutual exclusion
2. Hold and wait
3. No preemption
4. Circular wait
