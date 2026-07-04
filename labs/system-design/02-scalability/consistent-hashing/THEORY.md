# Consistent Hashing Theory

## 💡 The Problem: Standard Modulo Hashing
Imagine you have a distributed cache (like Memcached) running on 4 servers. You want to store a key-value pair. A naive approach is to hash the key and take the modulo of the number of servers:
`server_index = hash(key) % 4`

This works perfectly until you need to scale. If traffic spikes and you add a 5th server, the formula becomes:
`server_index = hash(key) % 5`

Suddenly, almost every single key hashes to a *different* server index than it did before. You suffer a massive cache miss storm, all requests hit the database simultaneously, and the system crashes.

## 🔄 The Solution: The Hash Ring
Consistent Hashing solves this by changing the topology. Instead of a linear array of servers, we imagine the output space of the hash function as a **ring** (e.g., from 0 to $2^{32}-1$, where the end wraps around to 0).

1. **Hash the Servers**: We hash the IP address (or name) of each server and place it on the ring.
2. **Hash the Keys**: We hash the data key and place it on the ring.
3. **Routing**: To find which server holds a key, we move clockwise around the ring from the key's position until we find the first server.

Now, if we add a new server, it only takes over the keys located between itself and the previous server on the ring. The rest of the keys remain completely unaffected.

## 🎭 The Imbalance Problem & Virtual Nodes
Placing a few physical servers randomly on a massive hash ring often leads to uneven data distribution. One server might handle 60% of the keys, while another handles 5%.

**Solution: Virtual Nodes (vnodes)**. 
Instead of hashing a server once, we hash it multiple times with different labels (e.g., `ServerA-01`, `ServerA-02`, ..., `ServerA-100`). This places 100 virtual nodes for Server A uniformly around the ring. This guarantees a balanced distribution of keys across the physical servers.