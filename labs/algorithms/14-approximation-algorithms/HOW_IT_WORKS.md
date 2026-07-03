# How Approximation Algorithms Work

## Vertex Cover — 2-Approximation
`java
Set<Integer> approxVertexCover(Graph g) {
    Set<Integer> cover = new HashSet<>();
    Set<Edge> remaining = new HashSet<>(g.edges());
    while (!remaining.isEmpty()) {
        Edge e = remaining.iterator().next();
        cover.add(e.u);
        cover.add(e.v);
        remaining.removeIf(edge -> edge.u == e.u || edge.v == e.v
            || edge.u == e.v || edge.v == e.u);
    }
    return cover;
}
`
Ratio: 2 (optimal solution is at most twice the size)

## Set Cover — O(log n) Approximation
`java
Set<Integer> approxSetCover(Set<Integer> universe, List<Set<Integer>> sets) {
    Set<Integer> uncovered = new HashSet<>(universe);
    Set<Integer> cover = new HashSet<>();
    while (!uncovered.isEmpty()) {
        int best = -1, maxCovered = -1;
        for (int i = 0; i < sets.size(); i++) {
            Set<Integer> overlap = new HashSet<>(sets.get(i));
            overlap.retainAll(uncovered);
            if (overlap.size() > maxCovered) {
                maxCovered = overlap.size();
                best = i;
            }
        }
        cover.add(best);
        uncovered.removeAll(sets.get(best));
    }
    return cover;
}
`
Ratio: O(log n)
