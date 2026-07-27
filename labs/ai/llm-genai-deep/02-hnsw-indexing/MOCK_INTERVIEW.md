# Mock Interview: Explain HNSW Algorithm and Implement Search from Scratch

## Scenario
You are interviewing for a search infrastructure role. The team uses HNSW for vector search and wants to test your depth of understanding.

## Interviewer Opening Question
"Can you explain the HNSW algorithm and implement a simplified version of its search procedure?"

## Candidate Response
"HNSW builds a multi-layer graph where upper layers are sparser for long-range navigation and lower layers are denser for fine-grained search. It combines skip-list layering with navigable small-world graphs. Search starts at the top layer and greedily descends, using the current best as entry point for the next layer."

## Interviewer Probing Questions

**Q: How does the construction work?**
"Each element is assigned a random level L = floor(-ln(uniform(0,1)) * mL). It's inserted into layers 0..L, connecting to ef_construction nearest neighbors using a beam search."

**Q: What is ef_search and how does it affect performance?**
"ef_search controls the beam width during search. Higher values improve recall but increase latency. Typical values range from 100-500."

**Q: How does HNSW compare to IVF?**
"HNSW has better recall-latency trade-offs but higher memory usage. IVF is more memory-efficient and simpler to implement."

## Candidate Solution (Python)

```python
import numpy as np
from heapq import heappush, heappop

class HNSW:
    def __init__(self, dim, M=16, ef_construction=200, ef_search=200, mL=0.4):
        self.dim = dim
        self.M = M
        self.ef_construction = ef_construction
        self.ef_search = ef_search
        self.mL = mL
        self.graph = []   # list[list[set]] — per layer, adjacency list
        self.data = []    # vectors
        self.enter_point = None
        self.max_level = -1

    def _random_level(self):
        return int(-np.log(np.random.random()) * self.mL)

    def _distance(self, a, b):
        return np.linalg.norm(self.data[a] - self.data[b])

    def _search_layer(self, query_idx, entry_idx, ef, layer):
        visited = {entry_idx}
        candidates = [(self._distance(query_idx, entry_idx), entry_idx)]
        result = [(self._distance(query_idx, entry_idx), entry_idx)]
        while candidates:
            dist_c, c = heappop(candidates)
            d_farthest = -result[0][0] if result else float("inf")
            if dist_c > d_farthest:
                break
            for neighbor in self.graph[layer][c]:
                if neighbor not in visited:
                    visited.add(neighbor)
                    d = self._distance(query_idx, neighbor)
                    if d_farthest > d or len(result) < ef:
                        heappush(candidates, (d, neighbor))
                        heappush(result, (-d, neighbor))
                        if len(result) > ef:
                            heappop(result)
        return [(d, idx) for d, idx in result]

    def search(self, query, k=10):
        query_idx = len(self.data)
        self.data = np.vstack([self.data, query.reshape(1, -1)]) if len(self.data) else query.reshape(1, -1)
        entry = self.enter_point
        for layer in range(self.max_level, 0, -1):
            result = self._search_layer(query_idx, entry, 1, layer)
            entry = result[0][1]
        result = self._search_layer(query_idx, entry, self.ef_search, 0)
        self.data = self.data[:-1]
        return result[:k]

    def insert(self, vector):
        idx = len(self.data)
        self.data = np.vstack([self.data, vector.reshape(1, -1)]) if len(self.data) else vector.reshape(1, -1)
        level = self._random_level()
        while len(self.graph) <= level:
            self.graph.append([set() for _ in range(len(self.data) - 1)] + [set()])
        if self.enter_point is None:
            self.enter_point = idx
            self.max_level = level
            return
        entry = self.enter_point
        for layer in range(self.max_level, level, -1):
            result = self._search_layer(idx, entry, 1, layer)
            entry = result[0][1]
        for layer in range(min(level, self.max_level), -1, -1):
            result = self._search_layer(idx, entry, self.ef_construction, layer)
            neighbors = [n for _, n in result[:self.M]]
            self.graph[layer][idx].update(neighbors)
            for n in neighbors:
                self.graph[layer][n].add(idx)
                if len(self.graph[layer][n]) > self.M:
                    # prune to M nearest
                    n_neighbors = sorted(
                        [(self._distance(n, m), m) for m in self.graph[layer][n]],
                    key=lambda x: x[0])[:self.M]
                    self.graph[layer][n] = {m for _, m in n_neighbors}
            entry = result[0][1]
        if level > self.max_level:
            self.max_level = level
            self.enter_point = idx
```

## Interviewer Feedback
"Excellent depth — you understand the skip-list analogy, beam search mechanics, and the construction pruning rules. The implementation covers the core algorithm. Well done."

## Key Takeaways
- HNSW combines skip-list layering with navigable small-world graphs
- Search is greedy descent through layers followed by beam search at layer 0
- ef_search and M control the recall-latency trade-off
- Construction is incremental and supports dynamic insertion
- Memory overhead is O(M * N) edges
