# Why Push-Relabel Flow Algorithms Exist

Push-relabel algorithms exist because augmenting-path algorithms (Ford-Fulkerson, Edmonds-Karp, Dinic) have limitations on certain graph topologies. Dinic requires O(E * sqrt(V)) time for unit-capacity networks and O(V^2 * E) in general, but push-relabel can achieve O(V^3) and often outperforms Dinic on dense graphs.

Push-relabel exists because it takes a fundamentally different approach: instead of maintaining a valid flow and finding augmenting paths, it allows temporary overflows (preflow) and uses local operations. This local nature makes it suitable for parallel and distributed implementations.

Push-relabel exists because of the need for more efficient maximum flow computation in practice. Applications in computer vision (image segmentation via graph cuts), network routing, airline scheduling, and bipartite matching often require solving large flow problems with thousands or millions of edges, where push-relabel's performance characteristics are advantageous.

Min-cost max flow exists because many real-world optimization problems require not just maximum flow but minimum-cost flow: transportation networks, logistics, resource allocation, and assignment problems.