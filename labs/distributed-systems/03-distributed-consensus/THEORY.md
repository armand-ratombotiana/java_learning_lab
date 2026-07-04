# Distributed Consensus: Theory

## Problem Definition
Multiple processes must agree on a single value despite failures (crash, network, Byzantine).

## Properties
- **Agreement**: All non-faulty processes decide on the same value
- **Validity**: If all processes propose the same value v, then any decided value is v
- **Termination**: All non-faulty processes eventually decide
- **Integrity**: No process decides more than once

## FLP Impossibility
In an asynchronous system, no deterministic consensus algorithm can guarantee termination with even one crash failure. Real systems use:
- Failure detectors (unreliable but practical)
- Randomized algorithms
- Partial synchrony assumptions
