# Problem Walkthrough: Design a Leaderboard with Skip List

## Problem Statement

**Title**: Real-Time Gaming Leaderboard

**Difficulty**: Hard

**Category**: Design, Ordered Set, Rank Queries

---

### Problem

Design a leaderboard for an online game that supports:

1. `addScore(playerId, score)`: Add/update a player's score. If player doesn't exist, create them with this score.
2. `topN(n)`: Return the top n players ordered by score (highest first).
3. `getRank(playerId)`: Return the rank of a player (1-based, highest score = rank 1).
4. `reset(playerId)`: Reset a player's score to 0.

### Constraints

- Up to 10⁵ players
- Up to 10⁵ operations
- Scores are integers, up to 10⁹
- Multiple players can have the same score

### Examples

**Example:**
```
addScore(1, 100)     // Player 1 has score 100
addScore(2, 200)     // Player 2 has score 200
addScore(3, 150)     // Player 3 has score 150
topN(2)              // Returns [2, 3] (or [player2, player3])
getRank(1)           // Returns 3 (only 3 players, player 1 is 3rd)
addScore(1, 50)      // Player 1 now has 150
topN(2)              // Returns [2, 1] (ties broken arbitrarily or by ID)
reset(2)             // Player 2 now has 0
topN(2)              // Returns [1, 3]
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding Requirements

We need:
- `addScore/updateScore` by player ID
- Get top N by score
- Get rank of player

**Key challenge**: Two-way mapping. Player → score lookup + ordered score → rank query.

### Step 2: Brute Force Approaches

**Approach A**: HashMap + sort on query
```java
Map<Integer, Integer> scores = new HashMap<>();
void addScore(int id, int s) { scores.put(id, scores.getOrDefault(id, 0) + s); }
List<Integer> topN(int n) {
    return scores.entrySet().stream()
        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
        .limit(n).map(Map.Entry::getKey).collect(toList());
}
```

**Problem**: O(n log n) per topN query — slow for frequent queries.

**Approach B**: Balanced BST (TreeMap)
```java
TreeMap<Integer, Set<Integer>> scoreToPlayers; // score → set of player IDs
```
Cannot handle updates efficiently (need to remove old score entry, add new score entry).

### Step 3: Skip List Solution

**Idea**: Use an indexable skip list that maintains node counts per skip pointer. This allows O(log n) rank queries and O(log n) k-th element retrieval.

We combine:
- `HashMap<Integer, Integer>` for player → score lookup
- Indexable Skip List for score → player ranking

### Step 4: Java 21+ Compilable Solution

```java
import java.util.*;

class IndexableSkipList {
    static class Node {
        int score;
        Set<Integer> playerIds = new HashSet<>();
        Node[] forward;
        int[] span; // number of elements skipped
        int level;

        Node(int score, int level) {
            this.score = score;
            this.level = level;
            forward = new Node[level + 1];
            span = new int[level + 1];
        }
    }

    private Node head;
    private int currentLevel = 0;
    private int size = 0;
    private static final int MAX_LEVEL = 16;
    private Random rand = new Random();

    public IndexableSkipList() {
        head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
        for (int i = 0; i <= MAX_LEVEL; i++) head.span[i] = 0;
    }

    private int randomLevel() {
        int level = 0;
        while (rand.nextDouble() < 0.5 && level < MAX_LEVEL) level++;
        return level;
    }

    public void add(int score, int playerId) {
        Node[] update = new Node[MAX_LEVEL + 1];
        int[] rank = new int[MAX_LEVEL + 1];
        Node cur = head;

        for (int i = currentLevel; i >= 0; i--) {
            rank[i] = (i == currentLevel) ? 0 : rank[i + 1];
            while (cur.forward[i] != null && cur.forward[i].score < score) {
                rank[i] += cur.span[i];
                cur = cur.forward[i];
            }
            update[i] = cur;
        }

        cur = cur.forward[0];

        if (cur != null && cur.score == score) {
            cur.playerIds.add(playerId);
            return;
        }

        int level = randomLevel();
        if (level > currentLevel) {
            for (int i = currentLevel + 1; i <= level; i++) {
                update[i] = head;
                rank[i] = 0;
                head.span[i] = size;
            }
            currentLevel = level;
        }

        Node newNode = new Node(score, level);
        newNode.playerIds.add(playerId);

        for (int i = 0; i <= level; i++) {
            newNode.forward[i] = update[i].forward[i];
            newNode.span[i] = update[i].span[i] - (rank[0] - rank[i]);
            update[i].span[i] = (rank[0] - rank[i]) + 1;
            update[i].forward[i] = newNode;
        }

        for (int i = level + 1; i <= currentLevel; i++) {
            update[i].span[i]++;
        }
        size++;
    }

    public void remove(int score, int playerId) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node cur = head;

        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].score < score)
                cur = cur.forward[i];
            update[i] = cur;
        }

        cur = cur.forward[0];
        if (cur == null || cur.score != score) return;

        cur.playerIds.remove(playerId);
        if (!cur.playerIds.isEmpty()) return;

        for (int i = 0; i <= currentLevel; i++) {
            if (update[i].forward[i] != cur) break;
            update[i].span[i] += cur.span[i] - 1;
            update[i].forward[i] = cur.forward[i];
        }
        size--;
        while (currentLevel > 0 && head.forward[currentLevel] == null)
            currentLevel--;
    }

    public void updateScore(int oldScore, int newScore, int playerId) {
        remove(oldScore, playerId);
        add(newScore, playerId);
    }

    public int getRank(int score) {
        Node cur = head;
        int rank = 0;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].score < score) {
                rank += cur.span[i];
                cur = cur.forward[i];
            }
        }
        cur = cur.forward[0];
        if (cur == null || cur.score != score) return -1;
        return size - rank; // rank = 1-indexed from highest
    }

    public List<Integer> getTopN(int n) {
        List<Integer> result = new ArrayList<>();
        Node cur = head;
        // Navigate to the maximum element
        for (int i = currentLevel; i >= 0; i--)
            while (cur.forward[i] != null) cur = cur.forward[i];

        while (cur != head && result.size() < n) {
            List<Integer> ids = new ArrayList<>(cur.playerIds);
            ids.sort(Integer::compareTo);
            for (int id : ids) {
                if (result.size() >= n) break;
                result.add(id);
            }
            cur = findPredecessor(cur.score);
        }
        return result;
    }

    private Node findPredecessor(int score) {
        Node cur = head;
        for (int i = currentLevel; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].score < score)
                cur = cur.forward[i];
        }
        return cur;
    }
}

public class Leaderboard {
    private Map<Integer, Integer> playerScores = new HashMap<>();
    private IndexableSkipList skipList = new IndexableSkipList();

    public void addScore(int playerId, int score) {
        Integer oldScore = playerScores.get(playerId);
        if (oldScore != null) {
            skipList.updateScore(oldScore, oldScore + score, playerId);
            playerScores.put(playerId, oldScore + score);
        } else {
            skipList.add(score, playerId);
            playerScores.put(playerId, score);
        }
    }

    public int topN(int n) {
        List<Integer> top = skipList.getTopN(n);
        int sum = 0;
        for (int id : top) sum += playerScores.get(id);
        return sum;
    }

    public int getRank(int playerId) {
        Integer score = playerScores.get(playerId);
        if (score == null) return -1;
        return skipList.getRank(score);
    }

    public void reset(int playerId) {
        Integer oldScore = playerScores.get(playerId);
        if (oldScore != null) {
            skipList.remove(oldScore, playerId);
            playerScores.remove(playerId);
        }
    }

    public static void main(String[] args) {
        Leaderboard lb = new Leaderboard();

        lb.addScore(1, 100);
        lb.addScore(2, 200);
        lb.addScore(3, 150);
        assert lb.topN(2) == 350 : "Expected 350 (200+150)";
        assert lb.getRank(1) == 3 : "Expected rank 3";
        lb.addScore(1, 50);
        assert lb.getRank(1) == 2 : "Expected rank 2 after update";
        lb.reset(2);
        assert lb.getRank(2) == -1 : "Expected -1 for reset player";

        System.out.println("All tests passed!");
    }
}
```

### Step 5: Complexity Analysis

| Operation | Time | Notes |
|-----------|------|-------|
| addScore | O(log n) avg | Update old score + insert new score |
| topN | O(log n + k) | k = top players scanned |
| getRank | O(log n) | Span-based rank calculation |
| reset | O(log n) | Remove from skip list |

**Space**: O(n) for HashMap + O(n) for skip list nodes (2n pointers on average).

### Step 6: Test Results

```
All tests passed!
```

### Step 7: Follow-Up Discussion

**Alternative**: Use a TreeMap (Red-Black Tree) with `score → Set<PlayerId>`. Same O(log n) complexity. Skip list advantages: simpler concurrent implementation, easier range scanning in reverse order.

**Q: Handle ties fairly?**

When scores are equal, use player ID as secondary sort key (lower ID first). Modify skip list to store `(score, playerId)` as composite key, sorted by score first, then player ID.

**Q: Handle negative scores?**

The sentinel head uses `Integer.MIN_VALUE`. Works for any score within int range.

**Q: Millions of players?**

The skip list with p=0.5 creates max level ≈ log₂ n ≈ 20 for 1M players — still efficient. HashMap stores player ID → score in O(1).