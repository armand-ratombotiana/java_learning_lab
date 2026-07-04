# Failure Detection: Flashcards

## Front: What is a heartbeat?
**Back**: Periodic "I'm alive" signal between nodes.

## Front: What is phi-accrual?
**Back**: Probabilistic failure detector using heartbeat statistics.

## Front: What is SWIM?
**Back**: Scalable Weakly-consistent Infection-style failure detection.

## Front: What is false positive in FD?
**Back**: Declaring a live node as failed.

## Front: What is false negative in FD?
**Back**: Not detecting an actual failure.

## Front: What is gossip protocol in FD?
**Back**: Nodes exchange membership state periodically.

## Front: How does Raft detect failures?
**Back**: Election timeout with randomized intervals (150-300ms).

## Front: What is suspicion phase?
**Back**: Spread doubt before declaring failure (reduces false positives).
