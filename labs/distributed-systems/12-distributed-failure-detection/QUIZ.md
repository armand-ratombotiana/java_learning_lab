# Failure Detection: Quiz

## Questions
1. What is the difference between crash and omission failure?
2. How does phi-accrual detection work?
3. What is the SWIM protocol?
4. How does gossip propagate failure information?
5. What is a false positive in failure detection?
6. How does Raft detect leader failure?
7. What is the suspicion phase in SWIM?
8. How does network latency affect failure detection?
9. What is the convergence time of gossip?
10. What is the FLP impossibility's implication for failure detection?

## Answers
1. Crash: node stops. Omission: node fails to respond to some messages.
2. Computes suspicion level (phi) based on heartbeat history statistics
3. Scalable failure detection with indirect probing and suspicion
4. Nodes periodically exchange membership state; information spreads exponentially
5. Declaring a live node as failed
6. Election timeout: if no AppendEntries heartbeat within timeout
7. Before declaring failure, spread suspicion to allow recovery
8. Higher latency → longer detection time, more false positives
9. O(log N) rounds for information to reach all N nodes
10. Perfect detection is impossible in async systems; practical detectors are probabilistic
