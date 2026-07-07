package com.algo.lab38;

import java.util.*;

/**
 * Minimum Steiner Tree approximation using the MST-based 2-approximation.
 * Given a set of terminal vertices, finds a tree connecting them that may
 * include non-terminal (Steiner) vertices. The algorithm computes all-pairs
 * shortest paths, builds a complete graph on terminals, finds MST, then
 * expands edges back to original paths.
 */
public class SteinerTreeApprox {
    private final int n;
    private final double[][] dist;

    public SteinerTreeApprox(int n) {
        this.n = n;
        this.dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            dist[i][i] = 0;
        }
    }

    public void addEdge(int u, int v, double w) {
        dist[u][v] = Math.min(dist[u][v], w);
        dist[v][u] = Math.min(dist[v][u], w);
    }

    private void floydWarshall() {
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] + dist[k][j] < dist[i][j])
                        dist[i][j] = dist[i][k] + dist[k][j];
    }

    public Set<Integer> approximateSteiner(Set<Integer> terminals) {
        floydWarshall();
        List<Integer> termList = new ArrayList<>(terminals);
        int t = termList.size();
        boolean[][] inMst = new boolean[t][t];
        double[] minDist = new double[t];
        int[] parent = new int[t];
        Arrays.fill(minDist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[t];
        minDist[0] = 0;

        for (int i = 0; i < t; i++) {
            int u = -1;
            for (int j = 0; j < t; j++) if (!visited[j] && (u == -1 || minDist[j] < minDist[u])) u = j;
            if (u == -1) break;
            visited[u] = true;
            if (parent[u] != -1) {
                inMst[u][parent[u]] = inMst[parent[u]][u] = true;
            }
            for (int v = 0; v < t; v++) {
                if (!visited[v] && dist[termList.get(u)][termList.get(v)] < minDist[v]) {
                    minDist[v] = dist[termList.get(u)][termList.get(v)];
                    parent[v] = u;
                }
            }
        }

        Set<Integer> steinerNodes = new HashSet<>(terminals);
        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {
                if (inMst[i][j]) {
                    int a = termList.get(i), b = termList.get(j);
                    steinerNodes.add(a);
                    steinerNodes.add(b);
                    for (int k = 0; k < n; k++) {
                        if (dist[a][k] + dist[k][b] == dist[a][b] && !terminals.contains(k)) {
                            steinerNodes.add(k);
                        }
                    }
                }
            }
        }
        return steinerNodes;
    }
}
