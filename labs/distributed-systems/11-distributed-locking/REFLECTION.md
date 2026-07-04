# Distributed Locking: Reflection

## Key Insights
- Fencing tokens are non-negotiable for safety
- Leases prevent deadlocks but introduce timing complexity
- Redlock is controversial; understand the tradeoffs
- Prefer optimistic concurrency over distributed locks when possible

## Questions
1. Do your distributed operations have fencing tokens?
2. What's the worst-case GC pause in your system?
3. Could CRDTs eliminate the need for locking?
4. Are you using distributed locks where simpler solutions would work?

## Personal Notes
- Martin Kleppmann's critique of Redlock changed my approach to locking
- ZooKeeper locks are simpler than they first appear
- Most distributed locking bugs I've seen involve missing fencing tokens
