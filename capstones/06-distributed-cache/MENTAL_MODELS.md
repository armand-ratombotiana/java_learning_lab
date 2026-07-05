# Mental Models: Distributed Cache

- **Cache = Speed Layer**: Sits between application and slow storage; trades consistency for speed.
- **Consistent Hashing = Ring Router**: Keys are routed to nodes on a ring; adding nodes doesn't reshuffle everything.
- **Gossip = Rumor Spreading**: Nodes share what they know; eventually everyone knows everyone.
- **Replication = Insurance Copies**: Extra copies of data prevent loss when a node fails.
- **Eviction = Making Room**: When memory fills, someone gets evicted. LRU evicts the laziest.
- **TTL = Self-Destruct Timer**: Data automatically expires after a certain time.
