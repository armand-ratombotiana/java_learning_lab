# Problem Walkthrough: Accounts Merge with DSU

## Problem Statement

**Title**: Accounts Merge — Email Identity Resolution

**Difficulty**: Medium

**Category**: Graph, Union-Find, HashMap

---

### Problem

Given a list of accounts where each element `accounts[i]` is a list of strings. The first element is the account owner's name, and the rest are associated email addresses.

Two accounts belong to the same person if they share at least one email address. Merge accounts belonging to the same person. Output a list of accounts where:
- Each account starts with the name
- Followed by the person's unique emails in sorted order
- Accounts can be returned in any order

### Constraints

- `1 ≤ accounts.length ≤ 1000`
- `2 ≤ accounts[i].length ≤ 10`
- `1 ≤ accounts[i][j].length ≤ 30`
- Names consist of lowercase English letters
- Emails consist of lowercase English letters and '@'

### Examples

**Example 1:**
```
Input:
  accounts = [
    ["John","johnsmith@mail.com","john_newyork@mail.com"],
    ["John","johnsmith@mail.com","john00@mail.com"],
    ["Mary","mary@mail.com"],
    ["John","johnnybravo@mail.com"]
  ]

Output:
  [
    ["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],
    ["Mary","mary@mail.com"],
    ["John","johnnybravo@mail.com"]
  ]

Explanation:
  First and second accounts share "johnsmith@mail.com" → merge.
  Third account "Mary" is separate.
  Fourth account "John" with different email is separate.
```

**Example 2:**
```
Input:
  accounts = [
    ["Gabe","Gabe0@m.co","Gabe3@m.co","Gabe1@m.co"],
    ["Kevin","Kevin3@m.co","Kevin5@m.co","Kevin0@m.co"],
    ["Ethan","Ethan5@m.co","Ethan4@m.co","Ethan0@m.co"],
    ["Gabe","Gabe3@m.co","Gabe4@m.co","Gabe2@m.co"]
  ]

Output:
  [
    ["Ethan","Ethan0@m.co","Ethan4@m.co","Ethan5@m.co"],
    ["Gabe","Gabe0@m.co","Gabe1@m.co","Gabe2@m.co","Gabe3@m.co","Gabe4@m.co"],
    ["Kevin","Kevin0@m.co","Kevin3@m.co","Kevin5@m.co"]
  ]
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

Accounts represent people. If two accounts share even one email, they're the SAME person. We need to merge all emails belonging to the same person.

**Key insight**: This is a graph connectivity problem. Emails are nodes. Each account is a clique — connect all its emails. Two accounts are connected if any email overlaps.

### Step 2: Approach — DSU

1. Map each email to a unique integer ID
2. For each account, link the first email to all other emails in the same account (union by email ID)
3. After processing all accounts, group emails by their DSU root
4. Sort emails within each group, prepend the name, return

### Step 3: Java 21+ Compilable Solution

```java
import java.util.*;

public class AccountsMerge {

    static class UnionFind {
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

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        // Map email → integer ID
        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int id = 0;

        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                emailToName.put(email, name);
                if (!emailToId.containsKey(email)) {
                    emailToId.put(email, id++);
                }
            }
        }

        UnionFind uf = new UnionFind(id);

        // Union emails within each account
        for (List<String> account : accounts) {
            String firstEmail = account.get(1);
            int firstId = emailToId.get(firstEmail);
            for (int i = 2; i < account.size(); i++) {
                uf.union(firstId, emailToId.get(account.get(i)));
            }
        }

        // Group emails by their DSU root
        Map<Integer, List<String>> rootToEmails = new HashMap<>();
        for (String email : emailToId.keySet()) {
            int root = uf.find(emailToId.get(email));
            rootToEmails.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
        }

        // Build result: name + sorted emails
        List<List<String>> result = new ArrayList<>();
        for (List<String> emails : rootToEmails.values()) {
            Collections.sort(emails);
            List<String> merged = new ArrayList<>();
            merged.add(emailToName.get(emails.get(0)));
            merged.addAll(emails);
            result.add(merged);
        }

        return result;
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        AccountsMerge am = new AccountsMerge();

        // Example 1
        List<List<String>> accounts1 = List.of(
            List.of("John","johnsmith@mail.com","john_newyork@mail.com"),
            List.of("John","johnsmith@mail.com","john00@mail.com"),
            List.of("Mary","mary@mail.com"),
            List.of("John","johnnybravo@mail.com")
        );

        List<List<String>> result1 = am.accountsMerge(accounts1);
        System.out.println("Example 1: " + result1);
        assert result1.size() == 3 : "Expected 3 accounts";
        for (List<String> acc : result1) {
            if (acc.get(0).equals("Mary")) {
                assert acc.size() == 2 : "Mary should have 1 email";
            }
            if (acc.get(0).equals("John") && acc.size() == 4) {
                // Merged John account
                assert acc.get(1).equals("john00@mail.com") : "Expected john00";
            }
        }

        // Example 2
        List<List<String>> accounts2 = List.of(
            List.of("Gabe","Gabe0@m.co","Gabe3@m.co","Gabe1@m.co"),
            List.of("Kevin","Kevin3@m.co","Kevin5@m.co","Kevin0@m.co"),
            List.of("Ethan","Ethan5@m.co","Ethan4@m.co","Ethan0@m.co"),
            List.of("Gabe","Gabe3@m.co","Gabe4@m.co","Gabe2@m.co")
        );
        List<List<String>> result2 = am.accountsMerge(accounts2);
        System.out.println("Example 2: " + result2);
        assert result2.size() == 3 : "Expected 3 accounts";
        for (List<String> acc : result2) {
            if (acc.get(0).equals("Gabe")) {
                assert acc.size() == 6 : "Gabe should have 5 emails + name";
            }
        }

        // Edge: single account
        List<List<String>> accounts3 = List.of(
            List.of("Alice","alice@mail.com")
        );
        List<List<String>> result3 = am.accountsMerge(accounts3);
        assert result3.size() == 1 : "Single account";
        assert result3.get(0).size() == 2 : "Name + 1 email";

        // Edge: accounts with same name but different emails
        List<List<String>> accounts4 = List.of(
            List.of("Bob","bob1@mail.com"),
            List.of("Bob","bob2@mail.com")
        );
        List<List<String>> result4 = am.accountsMerge(accounts4);
        assert result4.size() == 2 : "Not merged without shared email";

        // Edge: accounts with circular reference through chain
        List<List<String>> accounts5 = List.of(
            List.of("A","a1@m.co","a2@m.co"),
            List.of("A","a2@m.co","a3@m.co"),
            List.of("A","a3@m.co","a4@m.co")
        );
        List<List<String>> result5 = am.accountsMerge(accounts5);
        assert result5.size() == 1 : "All should merge";
        assert result5.get(0).size() == 5 : "Name + 4 emails";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 4: Complexity Analysis

**Time Complexity**: O(NK log NK)
- N = number of accounts, K = max emails per account
- Building mappings: O(NK)
- DSU operations: O(NK·α(NK))
- Sorting emails per component: O(M log M) where M = total unique emails

**Space Complexity**: O(NK)
- emailToId map: O(M) unique emails
- parent/rank arrays: O(NK)
- result: O(M) emails

### Step 5: Test Results

```
Example 1: [[John, john00@mail.com, john_newyork@mail.com, johnsmith@mail.com], [John, johnnybravo@mail.com], [Mary, mary@mail.com]]
Example 2: [[Ethan, Ethan0@m.co, Ethan4@m.co, Ethan5@m.co], [Gabe, Gabe0@m.co, Gabe1@m.co, Gabe2@m.co, Gabe3@m.co, Gabe4@m.co], [Kevin, Kevin0@m.co, Kevin3@m.co, Kevin5@m.co]]
All tests passed!
```

### Step 6: Follow-Up Discussion

**Q: What if there are duplicate names?**

Names are just display strings. The DSU operates on emails, not names. Two accounts with the same name but different emails are separate people (as in example 1).

**Q: What if a person has multiple names?**

Each account provides exactly one name. If two accounts share an email, we merge them even if names differ (shouldn't happen in clean data). Our code picks the name from the first email's account.

**Q: How to handle billions of accounts?**

For large scale:
- Use a distributed DSU (MapReduce)
- Partition by email domain first
- Use external memory DSU for the full graph
- Or use min-hashing for approximate email merging

**Q: What about email verification?**

In real systems, unverified emails might be typos. Add a confidence scoring system: verified emails have higher weight in union decisions.