# Flashcards: Distributed Cache

Front: What is consistent hashing? | Back: A hashing scheme that maps keys to nodes on a ring; adding/removing nodes only affects K/N keys, minimizing reshuffling.

Front: What is the gossip protocol? | Back: A decentralized failure detection and membership protocol where nodes periodically exchange heartbeat information with random peers.

Front: What is LRU? | Back: Least Recently Used — eviction policy that removes the least recently accessed items when memory is full.

Front: What is a quorum? | Back: Minimum number of nodes that must agree for a read (R) or write (W) operation. W + R > N for strong consistency.

Front: What is split-brain? | Back: A network partition causes nodes to form separate clusters, each believing it's the authority. Prevented by majority quorum.

Front: What is a virtual node? | Back: Multiple hash ring positions per physical node to improve load distribution and reduce rebalancing overhead.
