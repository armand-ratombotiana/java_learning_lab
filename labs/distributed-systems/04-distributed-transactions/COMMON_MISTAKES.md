# Common Mistakes with Distributed Transactions

## 1. Coordinator Single Point of Failure
2PC coordinator is a single point of failure. Use high-availability for coordinator or switch to SAGA.

## 2. Blocking in 2PC
If coordinator fails after participants prepared, participants block until coordinator recovers.

## 3. Non-Idempotent Compensations
SAGA compensations must be idempotent - they may be called multiple times.

## 4. Neglecting Timeouts
Without proper timeouts, 2PC participants can hold locks indefinitely.

## 5. Mixing Synchronous and Asynchronous
SAGA works best with asynchronous communication. Mixing with sync calls creates coupling.

## 6. Not Handling Partial Failures
Assume every message can be lost, every service can fail.
