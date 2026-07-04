# Why Distributed Locking Exists

## Problem
- Multiple services need to coordinate access to shared resources
- Race conditions cause data corruption
- Without locking: concurrent operations produce incorrect results

## Use Cases
1. **Leader election**: Ensure only one active leader
2. **Resource access**: Coordinate writes to shared files/DB
3. **Job scheduling**: Prevent duplicate job execution
4. **Rate limiting**: Coordinated throttling across services
5. **Idempotency**: Prevent duplicate transaction processing

## Without Distributed Locks
- Split-brain scenarios
- Concurrent writes corrupting state
- Duplicate job processing
- Inconsistent configuration changes
