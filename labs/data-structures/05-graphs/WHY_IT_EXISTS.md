# Why Graphs Exist

Graphs exist to model **relationships between entities** — the most general form of data structure.

## Problems Before Graphs

- Arrays and lists model linear order (one-dimensional)
- Trees model hierarchy (parent-child)
- No structure could represent **arbitrary connections** between items
- Social networks, road maps, and dependency graphs are inherently non-linear and non-hierarchical

## What Graphs Provide

| Need | Graph Solution |
|------|---------------|
| Model connections | Vertices + edges represent any relationship |
| Shortest path | Dijkstra, BFS find optimal routes |
| Structural analysis | Connectivity, cycles, strong components |
| Flow optimization | Max flow / min cut |
| Dependency resolution | Topological sort for DAGs |
| Recommendation | Graph traversal and similarity |

## Real-World Mappings

- **Social networks**: users are vertices, friendships are edges
- **Maps**: intersections are vertices, roads are weighted edges
- **The Web**: pages are vertices, hyperlinks are directed edges
- **Dependencies**: packages are vertices, "depends on" is a directed edge
- **Neural networks**: neurons are vertices, synapses are weighted edges
- **State machines**: states are vertices, transitions are directed edges

Graphs are the **most general data structure** — arrays, linked lists, and trees are all special cases of graphs.
