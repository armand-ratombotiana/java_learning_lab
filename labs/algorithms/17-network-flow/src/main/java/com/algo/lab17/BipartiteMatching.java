package com.algo.lab17;

import java.util.*;

/**
 * Maximum bipartite matching using reduction to max flow.
 * Builds a flow network with source connected to left set and right set to sink.
 * Time: O(V * E), Space: O(V + E)
 */
public class BipartiteMatching {

    private final int leftCount;
    private final int rightCount;
    private final List<Integer>[] adj;

    @SuppressWarnings("unchecked")
    public BipartiteMatching(int leftCount, int rightCount) {
        this.leftCount = leftCount;
        this.rightCount = rightCount;
        this.adj = new List[leftCount];
        for (int i = 0; i < leftCount; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public void addEdge(int left, int right) {
        adj[left].add(right);
    }

    public int maxMatching() {
        int[] matchR = new int[rightCount];
        Arrays.fill(matchR, -1);
        int result = 0;
        for (int u = 0; u < leftCount; u++) {
            boolean[] seen = new boolean[rightCount];
            if (bfs(u, seen, matchR)) {
                result++;
            }
        }
        return result;
    }

    private boolean bfs(int u, boolean[] seen, int[] matchR) {
        for (int v : adj[u]) {
            if (!seen[v]) {
                seen[v] = true;
                if (matchR[v] < 0 || bfs(matchR[v], seen, matchR)) {
                    matchR[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    public List<int[]> getMatching() {
        int[] matchR = new int[rightCount];
        Arrays.fill(matchR, -1);
        for (int u = 0; u < leftCount; u++) {
            boolean[] seen = new boolean[rightCount];
            bfs(u, seen, matchR);
        }
        List<int[]> matching = new ArrayList<>();
        for (int v = 0; v < rightCount; v++) {
            if (matchR[v] != -1) {
                matching.add(new int[]{matchR[v], v});
            }
        }
        return matching;
    }
}
