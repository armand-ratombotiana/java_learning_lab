# Common Mistakes with Consistency Models

## 1. Assuming Eventual Consistency is Fast
Eventual consistency can actually mean unbounded inconsistency windows depending on network conditions.

## 2. Confusing Sequential with Linearizable
Sequential consistency ignores real-time ordering - it only guarantees program order.

## 3. Ignoring Client-Side Caching
Client caches can violate read-your-writes guarantees if not properly invalidated.

## 4. Not Handling Concurrent Writes
Without conflict resolution, concurrent writes in eventually consistent systems cause data loss.

## 5. Over-Engineering Consistency
Using strong consistency when eventual is sufficient adds unnecessary latency and complexity.
