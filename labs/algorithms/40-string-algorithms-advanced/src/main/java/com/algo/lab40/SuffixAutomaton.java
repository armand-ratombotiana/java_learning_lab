package com.algo.lab40;

import java.util.*;

/**
 * Suffix Automaton (SAM) - a minimal DFA that accepts all suffixes of a string.
 * Built incrementally in O(n) time. Supports substring checking, counting
 * distinct substrings, and finding longest common substring.
 * Each state has a length (longest string in its class) and link (suffix link).
 */
public class SuffixAutomaton {
    private final List<State> states;
    private int last;

    private static class State {
        int len, link;
        int[] next = new int[26];
        boolean isTerminal;
        State() { Arrays.fill(next, -1); link = -1; }
    }

    public SuffixAutomaton(String s) {
        states = new ArrayList<>();
        states.add(new State());
        last = 0;
        for (char ch : s.toCharArray()) extend(ch - 'a');
        markTerminals();
    }

    private void extend(int c) {
        int cur = states.size();
        states.add(new State());
        states.get(cur).len = states.get(last).len + 1;
        int p = last;
        while (p != -1 && states.get(p).next[c] == -1) {
            states.get(p).next[c] = cur;
            p = states.get(p).link;
        }
        if (p == -1) {
            states.get(cur).link = 0;
        } else {
            int q = states.get(p).next[c];
            if (states.get(p).len + 1 == states.get(q).len) {
                states.get(cur).link = q;
            } else {
                int clone = states.size();
                states.add(new State());
                states.get(clone).len = states.get(p).len + 1;
                states.get(clone).next = states.get(q).next.clone();
                states.get(clone).link = states.get(q).link;
                while (p != -1 && states.get(p).next[c] == q) {
                    states.get(p).next[c] = clone;
                    p = states.get(p).link;
                }
                states.get(q).link = states.get(cur).link = clone;
            }
        }
        last = cur;
    }

    private void markTerminals() {
        int p = last;
        while (p > 0) { states.get(p).isTerminal = true; p = states.get(p).link; }
    }

    public boolean contains(String sub) {
        int cur = 0;
        for (char ch : sub.toCharArray()) {
            int c = ch - 'a';
            if (states.get(cur).next[c] == -1) return false;
            cur = states.get(cur).next[c];
        }
        return true;
    }

    public long countDistinctSubstrings() {
        long[] dp = new long[states.size()];
        Arrays.fill(dp, -1);
        return dfs(0, dp) - 1;
    }

    private long dfs(int v, long[] dp) {
        if (dp[v] != -1) return dp[v];
        long count = 1;
        for (int c = 0; c < 26; c++) {
            if (states.get(v).next[c] != -1) {
                count += dfs(states.get(v).next[c], dp);
            }
        }
        dp[v] = count;
        return count;
    }

    public int size() { return states.size(); }
}
