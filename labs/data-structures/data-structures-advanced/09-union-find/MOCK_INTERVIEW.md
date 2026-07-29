# Mock Interview: Union-Find (Disjoint Set Union)

## Setting

- **Round**: Technical phone screen
- **Duration**: 45 minutes
- **Focus**: Graph connectivity, DSU

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** Implement Union-Find with path compression and union by rank.

**Candidate:**

```java
class UnionFind {
    int[] parent, rank;
    int components;

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) parent[rx] = ry;
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
        components--;
        return true;
    }

    boolean connected(int x, int y) { return find(x) == find(y); }
    int count() { return components; }
}
```

**Interviewer:** What's the time complexity?

**Candidate:** O(α(n)) amortised per operation, where α(n) is the inverse Ackermann function — effectively O(1).

---

### Part 2: Core Problem — Accounts Merge (25 min)

**Interviewer:** Given a list of accounts where each account has a name and email addresses, two accounts belong to the same person if they share at least one email. Merge them and return sorted emails per person.

**Candidate:** Let me restate: each account is a list starting with a name, then emails. If two accounts have even one common email, they represent the same person. We need to merge all their emails, sort them, and return with the name.

**My approach:** DSU over email addresses.
1. Assign each unique email an integer ID
2. For each account, union the first email with all other emails in that account
3. After processing, group emails by their DSU root
4. Sort and output

```java
public List<List<String>> accountsMerge(List<List<String>> accounts) {
    Map<String, Integer> emailToId = new HashMap<>();
    Map<String, String> emailToName = new HashMap<>();
    int id = 0;

    // Assign IDs
    for (List<String> acc : accounts) {
        String name = acc.get(0);
        for (int i = 1; i < acc.size(); i++) {
            String email = acc.get(i);
            emailToName.put(email, name);
            if (!emailToId.containsKey(email))
                emailToId.put(email, id++);
        }
    }

    UnionFind uf = new UnionFind(id);

    // Union emails within each account
    for (List<String> acc : accounts) {
        int firstId = emailToId.get(acc.get(1));
        for (int i = 2; i < acc.size(); i++)
            uf.union(firstId, emailToId.get(acc.get(i)));
    }

    // Group by root
    Map<Integer, List<String>> groups = new HashMap<>();
    for (String email : emailToId.keySet()) {
        int root = uf.find(emailToId.get(email));
        groups.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
    }

    // Build result
    List<List<String>> result = new ArrayList<>();
    for (List<String> emails : groups.values()) {
        Collections.sort(emails);
        List<String> merged = new ArrayList<>();
        merged.add(emailToName.get(emails.get(0)));
        merged.addAll(emails);
        result.add(merged);
    }
    return result;
}
```

**Interviewer:** Walk through the union logic with an example.

**Candidate:** Consider:
```
Account A: [John, a@m.co, b@m.co]
Account B: [John, b@m.co, c@m.co]
```

- Account A: union(a@m.co's ID, b@m.co's ID)
- Account B: union(b@m.co's ID, c@m.co's ID)
- Since b@m.co links both, a, b, c are all in the same DSU component
- find(a) == find(c) → same person
- Merged: [John, a@m.co, b@m.co, c@m.co]

**Interviewer:** What if three accounts form a chain without one shared email across all?

**Candidate:** DSU handles this naturally. If A shares email X with B, B shares Y with C, A and C don't share any email, DSU still correctly groups all three because:
- union(A, B) → same root
- union(B, C) → same root
- find(A) == find(C) via path compression

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you handle 10 million accounts?

**Candidate:**
1. **Memory**: 10M accounts × average 5 emails = 50M unique emails. Each email as a string is ~30 chars. HashMap<String, Integer> would be ~3GB. Use database for email → ID mapping.
2. **DSU**: 50M int arrays = 200MB for parent + 200MB for rank = 400MB — OK for a server.
3. **Distributed DSU**: If single machine isn't enough, partition emails by hash. Union operations that cross partitions are batched and coordinated.
4. **Batch processing**: Use MapReduce. First pass: build graph edges (email → account). Second pass: connected components using DSU within partition, then cross-partition merge.

**Interviewer:** What if you also need to handle account deletion (removing a user)?

**Candidate:** DSU doesn't support deletion. Options:
1. **Offline**: Process deletions as "reverse operations" from the end of timeline. DSU with rollback.
2. **Lazy deletion**: Mark accounts as deleted, filter during result building but don't modify DSU structure.
3. **Recompute**: Periodically rebuild DSU from scratch excluding deleted accounts.

---

### Part 4: System Design (5 min)

**Interviewer:** Design a system that detects fraud by finding connected accounts.

**Candidate:**
1. **Data**: Accounts, devices, IPs, payment methods — all nodes in a graph
2. **Edges**: Same device, same IP, same credit card, shared phone
3. **DSU**: Union accounts connected by any shared identifier
4. **Weighted edges**: Different edge types have different "confidence" (same device = high, same IP = medium)
5. **Threshold**: Only union if combined confidence > threshold
6. **Query**: Is account A connected to known fraudulent account B? → O(α(n))
7. **Scale**: For 100M accounts, in-memory DSU with 400MB. Updates in real-time. Periodic full rebuild for consistency.

---

## Debrief

### What Went Well
- Clean DSU implementation
- Accounts merge: correct algorithm with HashMap-based DSU
- Edge case handling (chain merging without common email)
- Scalability considerations

### Areas for Growth
- Could mention offline D&C for dynamic connectivity
- Fraud detection system was mentioned but not detailed

### Score
| Category | Score (1-5) |
|----------|-------------|
| DSU Knowledge | 5 |
| Problem Solving | 5 |
| Code Quality | 5 |
| Scalability Thinking | 4 |
| Follow-up Handling | 4 |
| **Overall** | **4.6 / 5** |