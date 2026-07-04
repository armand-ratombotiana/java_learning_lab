# Time and Ordering: Flashcards

## Front: What is happens-before (→)?
**Back**: Causal ordering: same process order or message send/receive.

## Front: What does a Lamport clock guarantee?
**Back**: If a → b then L(a) < L(b) (not vice versa).

## Front: What does a vector clock guarantee?
**Back**: a → b iff VC(a) < VC(b); detects concurrency too.

## Front: What is an HLC?
**Back**: Hybrid Logical Clock = physical + logical components.

## Front: How does TrueTime work?
**Back**: Returns time interval [earliest, latest]; wait ε for consistency.

## Front: What is causal broadcast?
**Back**: Deliver only when all causally prior messages delivered.

## Front: What is clock drift?
**Back**: Physical clock accuracy error (ρ ≈ 10⁻⁶ for quartz).

## Front: Vector clock size grows with?
**Back**: Number of processes (O(N) metadata per message).
