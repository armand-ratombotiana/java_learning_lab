# Distributed Consensus: Flashcards

## Front: What is consensus?
**Back**: Multiple nodes agree on a single value despite failures.

## Front: What are consensus properties?
**Back**: Agreement, Validity, Termination, Integrity.

## Front: What is Raft's state machine?
**Back**: Follower → Candidate → Leader, with timeouts driving transitions.

## Front: How many nodes for f fault tolerance?
**Back**: 2f+1 (Raft, Paxos). For BFT: 3f+1.

## Front: What prevents split-brain in Raft?
**Back**: At most one leader per term; majority requirement for election.

## Front: What is the FLP impossibility?
**Back**: In async systems, no deterministic consensus guarantees termination with even one crash.

## Front: What is a log entry in Raft?
**Back**: (term, index, command) — replicated to followers, decided when committed.

## Front: How does Multi-Paxos differ from Paxos?
**Back**: Elects stable leader to skip Phase 1 for subsequent proposals.
