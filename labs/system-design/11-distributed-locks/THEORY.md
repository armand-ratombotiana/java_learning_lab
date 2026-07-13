# Distributed Locks Theory & Intuition

## 💡 The Problem: Crossing the JVM Boundary
In a monolithic application, if you want to ensure only one thread executes a critical piece of code (like processing a payment), you use `synchronized` or `ReentrantLock`.

However, in a Microservices architecture, you might have 5 instances of the Payment Service running on 5 different physical servers. 
If Instance A acquires a `ReentrantLock`, Instance B has no idea. They are running in completely different JVMs. Both instances could process the same payment simultaneously, resulting in a double-charge.

## 🌐 The Solution: Distributed Locking
To lock a resource across multiple physical servers, the lock state must be stored in a centralized, highly available external system that all instances can access.

Common external systems used for distributed locking:
1. **Relational Database**: Using a row lock (`SELECT ... FOR UPDATE`). Slow, but reliable.
2. **Redis**: Using atomic operations (`SETNX`). Extremely fast, but has edge cases during failover.
3. **Zookeeper / etcd / Consul**: Consensus-based systems. Highly reliable, designed specifically for distributed coordination.

## 🏎️ Redis-Based Locking (The Basics)
The most common approach is using Redis due to its high performance.

**Acquiring the Lock**:
A node sends the command: `SET resource_name my_unique_id NX PX 30000`
- `NX`: "Not eXists" - Only set the key if it doesn't already exist.
- `PX 30000`: Set a Time-To-Live (TTL) of 30 seconds.

If Redis returns `OK`, the node acquired the lock. If it returns `NULL`, another node holds the lock.

**Releasing the Lock**:
The node sends a Lua script to Redis: "If the value of `resource_name` equals `my_unique_id`, delete the key."
(We must check the ID first so we don't accidentally delete a lock held by a different node if our lock expired).

## ⚠️ The Danger: Clock Drift and GC Pauses
Distributed locks are incredibly dangerous if not implemented carefully.
Imagine Node A acquires a lock with a 10-second TTL.
1. Node A experiences a 12-second Garbage Collection (STW) pause.
2. The lock expires in Redis.
3. Node B acquires the lock and starts processing the payment.
4. Node A wakes up from its GC pause. It still thinks it holds the lock! It also processes the payment.

This violates mutual exclusion. To solve this, we need Fencing Tokens (see `INTERNALS.md`).