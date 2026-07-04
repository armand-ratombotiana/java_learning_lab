# Why Distributed Locking Matters

## Business Impact
- **Data integrity**: Prevents concurrent write corruption
- **Operation safety**: Ensures single controller for critical operations
- **Job reliability**: Prevents duplicate processing
- **Cost savings**: Avoids duplicate payment processing

## Technical Impact
- **Latency overhead**: Lock acquisition adds RTT delays
- **Availability**: Lock service failure blocks all operations
- **Complexity**: Lease renewal, fencing, and recovery logic
- **Risk**: Improper locking causes subtle bugs

## Key Insight
Distributed locks are a sharp tool: used correctly they ensure safety, used incorrectly they create fragility and performance problems.
