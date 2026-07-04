# Distributed Consensus: Mathematical Foundation

## FLP Impossibility Theorem

**Theorem**: In an asynchronous system where processes can crash, no deterministic consensus algorithm can guarantee both safety and liveness.

**Proof Sketch**:
1. Assume algorithm A solves consensus
2. Show there exists an admissible run where no decision is reached
3. Use bivalence argument: always possible to keep configuration bivalent

## Paxos Safety Proof

**Theorem**: If a value v is decided, no other value v' can be decided.

**Proof** (by induction on rounds):
1. For the first round that proposes a value, only one value can get majority
2. Subsequent rounds: if value v was decided with majority in round r, any propose in round r' > r must propose v
3. This follows from the invariant: if a value is accepted by a majority, all future proposers learn it

## Raft Safety

**Election Safety**: At most one leader per term.
**Leader Append-Only**: A leader never overwrites or deletes log entries.
**Log Matching**: If two logs have same term+index, they are identical.
**Leader Completeness**: Committed entries are present in all future leaders.
