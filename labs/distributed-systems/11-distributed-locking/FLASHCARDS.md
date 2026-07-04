# Distributed Locking: Flashcards

## Front: What is a fencing token?
**Back**: Monotonically increasing number proving lock ownership.

## Front: How does ZooKeeper implement locks?
**Back**: Ephemeral sequential nodes; lowest ID holds lock.

## Front: What is a lease?
**Back**: Time-bounded lock that auto-releases on expiry.

## Front: What is Redlock?
**Back**: Redis-based distributed lock requiring majority acquisition.

## Front: What is the purpose of leases?
**Back**: Prevent deadlocks from crashed lock holders.

## Front: Why are fencing tokens essential?
**Back**: Prevent stale/delayed lock holders from corrupting data.

## Front: What is the ZooKeeper lock algorithm?
**Back**: Create sequential node, watch predecessor, get lock when first.

## Front: What does Redlock majority prevent?
**Back**: Split-brain (two clients holding same lock).
