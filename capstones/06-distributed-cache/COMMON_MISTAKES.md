# Common Mistakes: Distributed Cache

- **Not handling hash collisions**: MD5 has extremely low collision rate, but should model detect and report collisions rather than silently overwriting.
- **Hotspot keys**: A single key accessed 1000x more than others overwhelms one node. Use client-side local cache for hotspot keys.
- **Split-brain**: Network partition creates two clusters believing they're the only cluster. Use majority quorum for decisions.
- **Replication lag causing stale reads**: Client writes to primary, but reads from stale replica. Use read-your-writes consistency with session tokens.
- **Memory leak from eviction not triggering**: LRU tracking must be lock-free to avoid contention under high throughput.
- **Gossip too aggressive**: Sending full membership list every 1s to 3 peers creates O(N^2) traffic in large clusters. Use partial gossip with delta.
