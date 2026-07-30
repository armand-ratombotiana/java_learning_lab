# CRDTs — Interview Questions

## Beginner
1. What is a CRDT and what problem does it solve?
2. What is the difference between state-based and op-based CRDTs?
3. What properties must a merge function satisfy?

## Intermediate
4. Explain how a PN-Counter works internally.
5. What is the limitation of a 2P-Set? (hint: removed elements)
6. How does LWW-Register handle concurrent writes and what information is lost?

## Advanced
7. How would you implement a multi-value register (MV-Register) that preserves all concurrent writes?
8. Explain the relationship between CRDTs and the CALM theorem (Consistency as Logical Monotonicity).
9. How does Riak's vector clock merge compare to CRDT-based reconciliation?

## System Design
10. Design a collaborative editing system (like Google Docs) using CRDTs for concurrent text editing.