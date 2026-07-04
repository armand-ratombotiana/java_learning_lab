# Mathematics of Consistent Hashing

## 📉 The Rebalancing Metric
Let $N$ be the number of servers and $K$ be the total number of keys.

In standard modulo hashing (`hash(key) % N`), when a server is added or removed, the number of keys that must be remapped is:
$$ \text{Keys Remapped} \approx K \times \frac{N}{N+1} $$
For large $N$, this approaches $K$. Meaning almost 100% of the data moves.

In **Consistent Hashing**, when a server is added or removed, the number of keys remapped is only:
$$ \text{Keys Remapped} \approx \frac{K}{N} $$
This is the theoretical minimum amount of data movement required to maintain balance.

## 🎲 Variance and Virtual Nodes
If we place $N$ nodes randomly on a ring of size $M$, the size of the interval between adjacent nodes varies significantly. 

By introducing $V$ virtual nodes per physical server, the total number of points on the ring becomes $N \times V$. 
According to statistical theory, as $V$ increases, the standard deviation of the load assigned to each physical server decreases proportionally to $\frac{1}{\sqrt{V}}$.

In practice, systems like Cassandra and Dynamo use $V \approx 256$ to ensure load distribution variance is within a few percentage points.