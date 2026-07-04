# Distributed Locking: Theory

## Lock Properties

### Mutual Exclusion
Only one process holds the lock at any time.

### Deadlock Freedom
Eventually some process can acquire the lock.

### Fault Tolerance
Lock service survives process and network failures.

## Lock Types

### Leases
- Lock with time-bound validity
- Must be renewed before expiry
- Automatically released on expiry

### Fencing Tokens
- Monotonically increasing token per lock grant
- Protects against delayed lock holders
- Resource checks token validity

## Implementation Approaches

### ZooKeeper (Sequential Ephemeral Nodes)
- Create sequential ephemeral znode for lock request
- Lowest sequence number holds lock
- Watcher on preceding node for release notification

### etcd (TTL + Revision)
- Create key with lease (TTL)
- Key revision acts as fencing token
- Watch for key deletion

### Redis Redlock
- Acquire lock on majority of Redis nodes
- Must acquire within time bound
- Release by deleting key on all nodes
