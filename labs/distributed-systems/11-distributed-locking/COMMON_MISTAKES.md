# Common Mistakes with Distributed Locking

## 1. Not Using Fencing Tokens
STOP: Without fencing tokens, a delayed lock holder can corrupt data after the lock expires.

## 2. Too-Short Lease Duration
GC pauses, network delays, or CPU throttling can cause premature lease expiry.

## 3. Not Handling Lease Renewal
Lock operations must renew the lease before expiry or risk losing the lock.

## 4. Non-Atomic Check-And-Set
Checking lock ownership and performing action must be atomic (use Lua scripts or compare-and-delete).

## 5. Using Redis Single Node
Redis single node for locks creates a single point of failure and potential split-brain.

## 6. Ignoring Clock Drift
Redlock assumes synchronized clocks. Clock drift can violate Redlock safety guarantees.
