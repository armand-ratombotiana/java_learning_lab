# Consistency Models: Flashcards

## Front: What is linearizability?
**Back**: Operations appear to execute atomically in real-time order.

## Front: What is causal consistency?
**Back**: Causally related operations are seen in order; concurrent operations may be reordered.

## Front: What is eventual consistency?
**Back**: Given enough time without updates, all replicas converge.

## Front: What does read-your-writes guarantee?
**Back**: A process always reads its own writes.

## Front: What does monotonic read guarantee?
**Back**: Once a value is read, future reads return same or newer.

## Front: What metadata does causal consistency need?
**Back**: Vector clocks (array of logical timestamps per node).

## Front: Strongest to weakest ordering?
**Back**: Linearizable → Sequential → Causal → PRAM → Eventual

## Front: What is PACELC?
**Back**: In partition: trade A vs C. Else: trade L vs C.
