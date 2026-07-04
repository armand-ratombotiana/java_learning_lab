# Architecture of Discrete Mathematics

## Logic in Software Architecture

```
Propositional Logic → Boolean expressions, conditions
Predicate Logic     → SQL queries, Java streams with filter()
Modal Logic         → Authentication, permissions
Temporal Logic      → Thread synchronization, protocols
```

## Set Theory in Java Collections

```
Set<T>        → HashSet, TreeSet, LinkedHashSet
Map<K,V>      → function from K to V
List<T>       → ordered sequence
Queue<T>      → FIFO structure (related to partial orders)
```

## Proof Techniques in Development

| Technique | Software Equivalent |
|-----------|-------------------|
| Induction | Loop invariants, recursive correctness |
| Contradiction | Proving impossibility (deadlock, race condition) |
| Case analysis | Switch statements, pattern matching |
| Constructive | Building an algorithm that solves the problem |

## Digital Logic Architecture

```
Input → [Logic Gates] → Output
         AND, OR, NOT, NAND, NOR, XOR, XNOR

Combinational: output depends only on current input
Sequential: output depends on input + state (flip-flops)
```
