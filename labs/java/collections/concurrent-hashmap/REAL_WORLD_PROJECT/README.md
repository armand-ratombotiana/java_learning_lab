# Real-World Project: Session Store

Implement a distributed session store backed by a `ConcurrentHashMap`-like
structure that supports:

- Session creation, lookup, and expiry
- Atomic session attribute updates
- Periodic scavenger thread for expired sessions
- JMX monitoring (active sessions, hit rate)

Study the production `ConcurrentHashMap` in OpenJDK to understand:
- `sizeCtl` volatility and resizing
- `ForwardingNode` and `TransferQueue` during resizing
- `TreeBin` for bin treeification
