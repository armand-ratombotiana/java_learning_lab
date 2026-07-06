# Architecture — Network Flow

## Component Design

`
MaxFlow Library
├── Graph (adjacency list with residual support)
├── Algorithm interface
│   ├── FordFulkerson (DFS)
│   ├── EdmondsKarp (BFS)
│   ├── Dinic (level graph + blocking flow)
│   └── PushRelabel (global relabeling)
└── Applications
    ├── BipartiteMatching
    ├── MinCut
    └── MinCostMaxFlow
`

## Integration with Other Libraries

Use with graph loading from files (DIMACS format), visualization tools (Graphviz), and benchmarking harnesses.

## Test Strategy

- Test on small networks with known max flows
- Test bipartite matching on randomly generated graphs
- Test min-cut recovery by verifying s-t disconnection
