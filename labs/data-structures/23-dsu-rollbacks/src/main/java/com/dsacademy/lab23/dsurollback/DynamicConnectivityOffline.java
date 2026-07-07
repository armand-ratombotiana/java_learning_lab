package com.dsacademy.lab23.dsurollback;

import java.util.*;

public class DynamicConnectivityOffline {

    private final int n;
    private final List<Query> queries;
    private final DSUWithRollback dsu;
    private final List<String> results;

    public enum QueryType { ADD, REMOVE, QUERY }

    public static class Query {
        public final QueryType type;
        public final int u, v;
        Query(QueryType type, int u, int v) {
            this.type = type; this.u = u; this.v = v;
        }
    }

    public DynamicConnectivityOffline(int n, List<Query> queries) {
        this.n = n;
        this.queries = queries;
        this.dsu = new DSUWithRollback(n);
        this.results = new ArrayList<>();
    }

    public List<String> process() {
        Map<String, List<Integer>> edgeTimeline = new HashMap<>();
        int q = queries.size();
        for (int i = 0; i < q; i++) {
            Query qu = queries.get(i);
            if (qu.type == QueryType.ADD) {
                String key = edgeKey(qu.u, qu.v);
                edgeTimeline.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            } else if (qu.type == QueryType.REMOVE) {
                String key = edgeKey(qu.u, qu.v);
                List<Integer> times = edgeTimeline.get(key);
                if (times != null && !times.isEmpty()) {
                    times.remove(times.size() - 1);
                }
            }
        }
        solve(0, q - 1);
        return results;
    }

    private void solve(int l, int r) {
        if (l > r) return;
        dsu.snapshot();
        if (l == r) {
            Query qu = queries.get(l);
            if (qu.type == QueryType.QUERY) {
                results.add(dsu.connected(qu.u, qu.v) ? "YES" : "NO");
            }
            dsu.rollbackToSnapshot();
            return;
        }
        int mid = (l + r) / 2;
        solve(l, mid);
        solve(mid + 1, r);
        dsu.rollbackToSnapshot();
    }

    private static String edgeKey(int u, int v) {
        return u < v ? u + "-" + v : v + "-" + u;
    }
}
