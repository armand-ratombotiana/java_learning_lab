# Failure Detection: Interview Questions

## Q1: How does Cassandra detect node failures?
**A**: Uses phi-accrual failure detector. Tracks heartbeat timing history, computes phi (suspicion level). Configurable threshold. Adapts to network conditions automatically.

## Q2: How does Raft detect leader failure?
**A**: Followers expect periodic AppendEntries heartbeats from leader. If no heartbeat within election timeout (150-300ms randomized), follower becomes candidate and starts election.

## Q3: What's the difference between SWIM and gossip-based detection?
**A**: SWIM uses direct pings and indirect probes for O(1) messages per node. Gossip exchanges full membership state with random nodes. SWIM is more efficient for large clusters.

## Q4: How do you tune a failure detector?
**A**: Adjust: heartbeat interval (detection speed vs overhead), timeout/threshold (false positives vs detection latency), suspicion phase (recovery chance vs detection delay).

## Q5: How do you handle GC pauses in failure detection?
**A**: Set heartbeat timeout > max expected GC pause (e.g., 10-15 seconds), use phi-accrual (adapts to pauses), implement application-level heartbeats from separate threads.
