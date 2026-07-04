# Common Mistakes with Failure Detection

## 1. Fixed Timeout in Dynamic Networks
Network latency varies. Fixed timeouts cause false positives in slow networks and false negatives in fast ones.

## 2. Too-Short Heartbeat Interval
Frequent heartbeats increase network traffic. Trade off detection speed vs overhead.

## 3. Ignoring GC Pauses
Java GC pauses can last seconds. Heartbeat timeout < max GC pause causes false positives.

## 4. Single Point of Detection
One node making failure decisions can be wrong. Use distributed consensus.

## 5. No Suspicion Phase
Immediate failure declaration without suspicion increases false positives.

## 6. Unbounded Heartbeat Storage
Keeping all heartbeat history causes memory leaks. Window or decay old data.
