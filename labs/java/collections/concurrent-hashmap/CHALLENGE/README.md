# Challenge: Optimize a Striped Lock Map

**Difficulty**: Hard

Implement a resize operation for `StripedLockMap` that:
1. Acquires all segment locks (without deadlock)
2. Doubles the segment count
3. Rehashes all entries across the new segments
4. Releases all locks

Constraints:
- Use a consistent lock ordering to prevent deadlock
- Minimise the time any single segment is locked
- Ensure linearizability during resize

See `SOLUTION/` for a reference implementation.
