# Common Mistakes with Consensus

## 1. Not Handling Log Inconsistencies
Followers may diverge. Raft's log matching forces consistency by deleting conflicting entries.

## 2. Split-Brain Without Proper Leader Election
Using heartbeats alone without proper term-based election can cause split-brain.

## 3. Ignoring FLP Impossibility
Asynchronous systems need timeouts, but too-short timeouts cause instability.

## 4. Single Point of Failure
Don't make the leader a SPOF. Clients should be able to discover new leaders.

## 5. Incorrect Quorum Calculation
Majority = N/2 + 1, not just any majority.

## 6. Blocking During Leader Election
Client requests should fail fast or queue, not block indefinitely.
