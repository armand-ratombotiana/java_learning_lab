# Failure Detection: Mathematical Foundation

## Detection Time

For a heartbeat interval Δt and timeout T:

Maximum detection time = T
Minimum detection time = Δt (heartbeat just missed)

## False Positive Probability

P(false positive) = P(heartbeat delayed > T | node alive)

For exponential inter-arrival with mean μ:
P(false positive) = e^(-T/μ)

As T increases: false positives decrease, detection latency increases.

## Phi-Accrual

Phi = -log₁₀(P(live node hasn't been heard from))

At phi = 1: 10% chance node is still alive
At phi = 2: 1% chance
At phi = 8: 10⁻⁸ chance

## SWIM Protocol Analysis

### Detection Time
O(log N) rounds for convergence.

### Network Load
O(1) messages per node per interval (constant, regardless of cluster size).

### False Positive Rate
With suspicion mechanism: exponentially decreasing in suspicion rounds.

## Perfect Failure Detection

Chandra-Toueg showed:
- Perfect failure detectors cannot be implemented in asynchronous systems
- Eventually perfect detectors can be implemented with partial synchrony
- Practical detectors make probabilistic guarantees
