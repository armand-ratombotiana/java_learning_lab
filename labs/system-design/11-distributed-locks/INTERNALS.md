# Distributed Locks Internals

## 🔴 The Redlock Algorithm
A single Redis instance is a single point of failure. If the Redis node crashes, your distributed locking system goes down.
If you add a Redis Replica, you introduce a new problem: Redis replication is asynchronous.
1. Node A acquires a lock on the Redis Master.
2. The Redis Master crashes *before* replicating the lock to the Replica.
3. The Replica is promoted to the new Master.
4. Node B requests the lock from the new Master. The Master grants it.
5. Node A and Node B both hold the lock. Mutual exclusion is violated.

To solve this, Redis creator Salvatore Sanfilippo proposed the **Redlock Algorithm**.
1. You run $N$ (e.g., 5) completely independent Redis Masters (no replication).
2. To acquire a lock, a client tries to acquire the lock on all 5 instances sequentially, with a very short timeout.
3. The client considers the lock acquired ONLY if it successfully locked a **majority** (at least 3 out of 5) of the instances.
4. If it fails to get a majority, it unlocks all the instances it did manage to lock.

## 🛡️ Fencing Tokens
Even with Redlock, Martin Kleppmann (author of *Designing Data-Intensive Applications*) pointed out a fatal flaw: GC Pauses.
If a node acquires a Redlock, goes to sleep for 10 seconds (GC pause), the lock expires, and another node acquires it, both nodes will eventually try to write to the database.

The only mathematically sound solution is a **Fencing Token**.
1. The lock service (e.g., Zookeeper) must grant an ever-increasing integer (the Fencing Token) every time it grants a lock.
2. Node A acquires the lock and gets Token 33.
3. Node A goes to sleep (GC pause). The lock expires.
4. Node B acquires the lock and gets Token 34. Node B writes to the database, passing its token (34). The database accepts it and records "Last Token = 34".
5. Node A wakes up and tries to write to the database, passing its token (33).
6. The database rejects Node A's write because $33 < 34$.

**Conclusion**: The database (or storage system) itself must participate in the locking protocol by enforcing fencing tokens. You cannot rely purely on the lock service.