# Debugging — Network Flow

## Print Residual Capacities

`java
void printGraph() {
    for (int v = 0; v < n; v++) {
        for (Edge e : graph[v]) {
            if (e.cap > 0) {
                System.out.println(v + " -> " + e.to + " : " + e.cap);
            }
        }
    }
}
`

## Verify Flow Conservation

`java
boolean isFlowValid() {
    for (int v = 0; v < n; v++) {
        if (v == s || v == t) continue;
        long net = 0;
        for (Edge e : graph[v]) net += (originalCap - e.cap);
        if (net != 0) return false;
    }
    return true;
}
`

## Common Debug Scenarios

- Flow too low: check for missing edges or wrong capacities
- Infinite loop: check that DFS makes progress each call
- Wrong min cut: verify reachable set from source in residual graph
