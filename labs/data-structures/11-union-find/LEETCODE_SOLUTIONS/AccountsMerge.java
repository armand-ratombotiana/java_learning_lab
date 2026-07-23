package com.leetcode.unionfind;

import java.util.*;

/**
 * LeetCode 721: Accounts Merge
 * https://leetcode.com/problems/accounts-merge/
 *
 * Merge accounts that share common email addresses.
 *
 * Time Complexity: O(A log A) where A is total number of emails
 * Space Complexity: O(A)
 */
public class AccountsMerge {

    public static class UnionFind {
        int[] parent, rank;
        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        void union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;
            if (rank[rx] < rank[ry]) parent[rx] = ry;
            else if (rank[rx] > rank[ry]) parent[ry] = rx;
            else { parent[ry] = rx; rank[rx]++; }
        }
    }

    /**
     * Approach: Union-Find
     * Map each email to an index and its owner. Union emails in the same account.
     * Time: O(A log A), Space: O(A)
     */
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int id = 0;

        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                emailToId.putIfAbsent(email, id++);
                emailToName.put(email, name);
            }
        }

        UnionFind uf = new UnionFind(id);
        for (List<String> account : accounts) {
            if (account.size() > 2) {
                int firstId = emailToId.get(account.get(1));
                for (int i = 2; i < account.size(); i++) {
                    uf.union(firstId, emailToId.get(account.get(i)));
                }
            }
        }

        Map<Integer, List<String>> rootToEmails = new HashMap<>();
        for (String email : emailToId.keySet()) {
            int root = uf.find(emailToId.get(email));
            rootToEmails.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
        }

        List<List<String>> result = new ArrayList<>();
        for (List<String> emails : rootToEmails.values()) {
            Collections.sort(emails);
            List<String> account = new ArrayList<>();
            account.add(emailToName.get(emails.get(0)));
            account.addAll(emails);
            result.add(account);
        }
        return result;
    }

    public static void main(String[] args) {
        AccountsMerge am = new AccountsMerge();

        List<List<String>> accounts = Arrays.asList(
            Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"),
            Arrays.asList("John", "johnnybravo@mail.com"),
            Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"),
            Arrays.asList("Mary", "mary@mail.com")
        );

        List<List<String>> merged = am.accountsMerge(accounts);
        System.out.println("Merged accounts:");
        for (List<String> acc : merged) {
            System.out.println("  " + acc);
        }
    }
}
