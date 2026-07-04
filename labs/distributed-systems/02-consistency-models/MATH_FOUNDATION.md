# Consistency Models: Mathematical Foundation

## Formal Definitions

### History
A history H is a sequence of operation invocations and responses.

### Linearizability
∃ a total order ≺ on operations such that:
1. If op₁ completes before op₂ begins, then op₁ ≺ op₂
2. Each read returns the value of the most recent write in ≺

### Sequential Consistency
∃ a total order ≺ on operations such that:
1. Operations from each process appear in program order
2. Each read returns the value of the most recent write in ≺

### Causal Consistency
∃ a partial order → (causal order) such that:
1. If op₁ → op₂ (causally related), all processes see op₁ before op₂
2. Concurrent operations may be ordered differently

### Eventual Consistency
∀ replicas r₁, r₂:
lim(t→∞) [value(r₁, t) = value(r₂, t)]
Given no new updates, all replicas converge to the same value.
