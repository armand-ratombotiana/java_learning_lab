# CAP Theorem: Mathematical Foundation

## Formal Definition

Let Σ be a distributed system with nodes N = {n₁, n₂, ..., nₖ}.

### Consistency
∀ operations op₁, op₂: if op₁ completes before op₂ begins, then op₂ sees the effects of op₁.

### Availability
∀ request r received by a non-failing node, the node eventually responds.

### Partition Tolerance
The system functions correctly when an arbitrary number of messages are lost.

## Proof

**Theorem**: No distributed system can simultaneously provide all three properties.

**Proof**:
Let the system have two nodes {A, B}. During a partition, messages between A and B are lost.

1. Client writes value `v₁` to node A
2. Client reads value from node B
3. Since partition exists, B doesn't know about `v₁`
4. If B returns any value, it may be stale (violates consistency)
5. If B refuses to respond, availability is violated
6. If system blocks until partition heals, partition tolerance is violated (system stops during partition)

Q.E.D.
