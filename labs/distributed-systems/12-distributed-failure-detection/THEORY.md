# Failure Detection: Theory

## Failure Models

### Crash Failure
Node stops permanently. No response to any request.

### Omission Failure
Node fails to respond to some messages (network issues).

### Timing Failure
Node responds too late (performance degradation).

### Byzantine Failure
Node behaves arbitrarily (malicious or buggy).

## Detection Approaches

### Heartbeats
- Periodic "I'm alive" messages
- Timeout = expected interval × safety margin
- Simple but prone to false positives

### Gossip Protocol
- Nodes periodically exchange membership information
- Failure information propagates through the cluster
- Eventually all nodes detect the failure

### Phi-Accrual (Cassandra)
- Calculates suspicion level (phi) based on heartbeat history
- Adapts to network conditions
- Configurable threshold for declaring failure

### SWIM (Scalable Weakly-consistent Infection-style)
- Piggyback membership updates on failure detection
- Efficient O(log N) detection time
- Used by HashiCorp Serf/Consul
