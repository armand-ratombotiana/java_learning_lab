# AMAZON INTERVIEW PREPARATION PACK
## 20+ Problems Optimized for Amazon Leadership Principles

---

## 🎯 AMAZON INTERVIEW PHILOSOPHY

Amazon's 14 Leadership Principles drive every interview:

1. **Customer Obsession** - Solve real problems
2. **Ownership** - Take responsibility for your design
3. **Invent and Simplify** - Elegant solutions
4. **Are Right, A Lot** - Correct, efficient code
5. **Learn and Be Curious** - Explain your thinking
6. **Hire and Develop** - Help others understand

**Interview Format**:
- **Coding Interview**: Medium-hard problems (not the hardest)
- **System Design**: Scalability focused
- **Behavioral**: Leadership principles heavily weighted
- **Focus**: Practicality, not just algorithm theory

**Amazon's Unique Focus**:
- **Scalability Over Perfection**: Working system > perfect code
- **Real-World Problems**: Queue systems, distributed caching, APIs
- **Leadership in Code**: Comments, design decisions, code readability

---

## 🔴 LEVEL 1: WARM-UP PROBLEMS (5 problems - 15 min each)

### Problem 1: Implement Queue using Stacks

**Difficulty**: Easy-Medium  
**Topics**: Queue, Stack, Data Structure Design  
**Amazon Focus**: Practical implementation  

```java
public class QueueUsingStacks {
    private Stack<Integer> pushStack;
    private Stack<Integer> popStack;
    
    public QueueUsingStacks() {
        pushStack = new Stack<>();
        popStack = new Stack<>();
    }
    
    public void push(int x) {
        pushStack.push(x);
    }
    
    public int pop() {
        moveIfNeeded();
        return popStack.pop();
    }
    
    public int peek() {
        moveIfNeeded();
        return popStack.peek();
    }
    
    public boolean empty() {
        return pushStack.empty() && popStack.empty();
    }
    
    private void moveIfNeeded() {
        if (popStack.isEmpty()) {
            while (!pushStack.isEmpty()) {
                popStack.push(pushStack.pop());
            }
        }
    }
    
    // Time: O(1) amortized, Space: O(n)
    // Why Amazon loves this: Practical queue behavior
}
```

---

### Problem 2: Rotting Oranges (Multi-Source BFS)

**Difficulty**: Medium  
**Topics**: BFS, Queue, Strategy  
**Amazon Focus**: Scalability over single source  

```java
public class RottingOranges {
    public int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        Queue<int[]> rotten = new LinkedList<>();
        int freshCount = 0;
        
        // Find initial rotten oranges and count fresh
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    rotten.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    freshCount++;
                }
            }
        }
        
        if (freshCount == 0) return 0;
        
        int minutes = 0;
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!rotten.isEmpty() && freshCount > 0) {
            int size = rotten.size();
            
            for (int i = 0; i < size; i++) {
                int[] pos = rotten.poll();
                
                for (int[] dir : dirs) {
                    int x = pos[0] + dir[0];
                    int y = pos[1] + dir[1];
                    
                    if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == 1) {
                        grid[x][y] = 2;
                        freshCount--;
                        rotten.offer(new int[]{x, y});
                    }
                }
            }
            
            if (freshCount > 0) minutes++;
        }
        
        return freshCount == 0 ? minutes : -1;
    }
    
    // Time: O(m*n), Space: O(min(m*n, rotten))
    // Real-world analogy: Cascade effects in distributed systems
}
```

---

### Problem 3: Design Cache with Get Put

**Difficulty**: Medium  
**Topics**: HashMap + LinkedList, LRU  
**Amazon Focus**: Core Amazon interview pattern  

```java
public class LRUCache {
    private int capacity;
    private Map<Integer, Node> map;
    private Node head, tail;
    
    private class Node {
        int key, value;
        Node prev, next;
        Node(int k, int v) {
            key = k;
            value = v;
        }
    }
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        
        Node node = map.get(key);
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            if (map.size() == capacity) {
                Node removed = removeTail();
                map.remove(removed.key);
            }
            
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addToHead(newNode);
        }
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void addToHead(Node node) {
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        node.prev = head;
    }
    
    private Node removeTail() {
        Node node = tail.prev;
        removeNode(node);
        return node;
    }
    
    // Time: O(1) for get and put
    // Space: O(capacity)
}
```

---

### Problem 4: Contains Duplicate II (Sliding Window)

**Difficulty**: Easy  
**Topics**: HashMap, Sliding Window  
**Amazon Focus**: K-parameter optimization  

```java
public class ContainsDuplicateII {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();
        
        for (int i = 0; i < nums.length; i++) {
            if (window.contains(nums[i])) {
                return true;
            }
            
            window.add(nums[i]);
            
            if (window.size() > k) {
                window.remove(nums[i - k]);
            }
        }
        
        return false;
    }
    
    // Time: O(n), Space: O(min(n, k))
    // Why Amazon: Parametric constraints
}
```

---

### Problem 5: Product of Array Except Self

**Difficulty**: Medium  
**Topics**: Array, Prefix/Suffix  
**Amazon Focus**: Optimal space usage  

```java
public class ProductExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // Calculate prefix products
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i-1] * nums[i-1];
        }
        
        // Calculate suffix products and multiply
        int suffix = 1;
        for (int i = n-1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix *= nums[i];
        }
        
        return result;
    }
    
    // Time: O(n), Space: O(1) excluding output
    // Why Amazon: No division, O(1) extra space - clever!
}
```

---

## 🟡 LEVEL 2: CORE AMAZON PROBLEMS (8 problems)

### Problem 6: Reorder Log Files

**Difficulty**: Medium  
**Topics**: Custom Sorting, Comparator  
**Amazon Focus**: Practical real-world problem  

```java
public class ReorderLogFiles {
    public String[] reorderLogFiles(String[] logs) {
        return Arrays.stream(logs)
            .sorted((log1, log2) -> {
                String[] parts1 = log1.split(" ", 2);
                String[] parts2 = log2.split(" ", 2);
                
                boolean isDigit1 = Character.isDigit(parts1[1].charAt(0));
                boolean isDigit2 = Character.isDigit(parts2[1].charAt(0));
                
                if (isDigit1 && isDigit2) {
                    return 0;  // Keep order for digit logs
                }
                
                if (isDigit1) return 1;
                if (isDigit2) return -1;
                
                int cmp = parts1[1].compareTo(parts2[1]);
                if (cmp != 0) return cmp;
                
                return parts1[0].compareTo(parts2[0]);
            })
            .toArray(new String[0]);
    }
    
    // Time: O(n*log(n)*k) where k is log length
    // Space: O(1)
}
```

---

### Problem 7: Time Based Key-Value Store

**Difficulty**: Medium  
**Topics**: HashMap, Binary Search  
**Amazon Focus**: Temporal data patterns  

```java
public class TimeMap {
    Map<String, TreeMap<Integer, String>> map;
    
    public TimeMap() {
        map = new HashMap<>();
    }
    
    public void set(String key, String value, int timestamp) {
        if (!map.containsKey(key)) {
            map.put(key, new TreeMap<>());
        }
        map.get(key).put(timestamp, value);
    }
    
    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) return "";
        
        TreeMap<Integer, String> tree = map.get(key);
        Integer t = tree.floorKey(timestamp);
        
        return t == null ? "" : tree.get(t);
    }
    
    // Time: O(log n) per operation
    // Space: O(n)
    // Real-world: Version control, time-series data
}
```

---

### Problem 8: Auction Highest Bid (Priority Queue)

**Difficulty**: Medium  
**Topics**: Priority Queue, Design  
**Amazon Focus**: E-commerce real problem  

```java
public class AuctionBid {
    public class Bid {
        int userId;
        int amount;
        
        Bid(int userId, int amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }
    
    private PriorityQueue<Bid> bids = new PriorityQueue<>(
        (a, b) -> b.amount - a.amount  // Max heap
    );
    
    public void placeBid(int userId, int amount) {
        // Amazon: Check current highest
        if (!bids.isEmpty() && amount <= bids.peek().amount) {
            throw new IllegalArgumentException("Bid too low");
        }
        bids.offer(new Bid(userId, amount));
    }
    
    public int getHighestBid() {
        return bids.isEmpty() ? -1 : bids.peek().amount;
    }
    
    // Time: O(log n) per bid
    // Space: O(n) for all bids
}
```

---

### Problem 9: Number of Islands (DFS + Union-Find)

**Difficulty**: Medium  
**Topics**: DFS, Union-Find, Grid  
**Amazon Focus**: Graph traversal applications  

```java
public class NumberOfIslands {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int count = 0;
        int m = grid.length, n = grid[0].length;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    dfs(grid, i, j, m, n);
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private void dfs(char[][] grid, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == '0') {
            return;
        }
        
        grid[i][j] = '0';
        dfs(grid, i+1, j, m, n);
        dfs(grid, i-1, j, m, n);
        dfs(grid, i, j+1, m, n);
        dfs(grid, i, j-1, m, n);
    }
    
    // Time: O(m*n), Space: O(m*n) recursion depth
    // Why Amazon: Practical geographic/cluster data
}
```

---

### Problem 10: Two Sum II - Input Array Is Sorted

**Difficulty**: Easy  
**Topics**: Two Pointers  
**Amazon Focus**: Simple but critical  

```java
public class TwoSumII {
    public int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            
            if (sum == target) {
                return new int[]{left + 1, right + 1};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{-1, -1};
    }
    
    // Time: O(n), Space: O(1)
    // Why Amazon: Simple, optimal, production-ready
}
```

---

### Problem 11: Longest Substring with At Most K Distinct

**Difficulty**: Medium  
**Topics**: Sliding Window  
**Amazon Focus**: String processing at scale  

```java
public class KDistinct {
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (k < 0) return 0;
        
        Map<Character, Integer> count = new HashMap<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count.put(c, count.getOrDefault(c, 0) + 1);
            
            while (count.size() > k) {
                char leftChar = s.charAt(left);
                count.put(leftChar, count.get(leftChar) - 1);
                if (count.get(leftChar) == 0) {
                    count.remove(leftChar);
                }
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    // Time: O(n), Space: O(k)
}
```

---

## 🔥 LEVEL 3: ADVANCED AMAZON PROBLEMS (7 problems)

### Problem 12: Analyze User Website Visit Pattern

**Difficulty**: Medium  
**Topics**: HashMap, Sorting, Patterns  
**Amazon Focus**: Real analytics problem  

```java
public class AnalyzeUserWebsite {
    public List<String> mostVisitedPattern(
        String[] username, int[] timestamp, String[] website) {
        
        Map<String, List<String>> userWebsites = new HashMap<>();
        Map<String, Integer> patterns = new HashMap<>();
        
        // Group websites by user and time
        List<int[]> visits = new ArrayList<>();
        for (int i = 0; i < username.length; i++) {
            visits.add(new int[]{i, timestamp[i]});
        }
        
        visits.sort((a, b) -> a[1] - b[1]);
        
        for (int[] visit : visits) {
            int idx = visit[0];
            userWebsites.putIfAbsent(username[idx], new ArrayList<>());
            userWebsites.get(username[idx]).add(website[idx]);
        }
        
        // Generate 3-sequences
        for (List<String> sites : userWebsites.values()) {
            Set<String> seen = new HashSet<>();
            
            for (int i = 0; i < sites.size(); i++) {
                for (int j = i + 1; j < sites.size(); j++) {
                    for (int k = j + 1; k < sites.size(); k++) {
                        String pattern = sites.get(i) + "," + 
                                       sites.get(j) + "," + 
                                       sites.get(k);
                        if (!seen.contains(pattern)) {
                            patterns.put(pattern, 
                                patterns.getOrDefault(pattern, 0) + 1);
                            seen.add(pattern);
                        }
                    }
                }
            }
        }
        
        return patterns.entrySet().stream()
            .max((a, b) -> a.getValue() == b.getValue() ? 
                a.getKey().compareTo(b.getKey()) : a.getValue() - b.getValue())
            .map(e -> Arrays.asList(e.getKey().split(",")))
            .orElse(new ArrayList<>());
    }
    
    // Time: O(n^3) for patterns, Space: O(n)
}
```

---

### Problem 13: Lowest Common Ancestor of BST

**Difficulty**: Medium  
**Topics**: BST, Tree Navigation  
**Amazon Focus**: Tree problems are key  

```java
public class LowestCommonAncestorBST {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }
        
        return root;
    }
    
    // Time: O(log n) average, O(n) worst
    // Space: O(log n) recursion depth
    // Why: Binary search property of BST
}
```

---

### Problem 14: Alien Dictionary

**Difficulty**: Hard  
**Topics**: Topological Sort, Graph  
**Amazon Focus**: Order from constraints  

```java
public class AlienDictionary {
    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();
        
        // Initialize graph
        for (String word : words) {
            for (char c : word.toCharArray()) {
                indegree.putIfAbsent(c, 0);
                graph.putIfAbsent(c, new HashSet<>());
            }
        }
        
        // Build graph
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            int minLen = Math.min(w1.length(), w2.length());
            
            if (w1.length() > w2.length() && 
                w1.substring(0, minLen).equals(w2.substring(0, minLen))) {
                return "";
            }
            
            for (int j = 0; j < minLen; j++) {
                char c1 = w1.charAt(j);
                char c2 = w2.charAt(j);
                
                if (c1 != c2) {
                    if (graph.get(c1).add(c2)) {
                        indegree.put(c2, indegree.get(c2) + 1);
                    }
                    break;
                }
            }
        }
        
        // Topological sort
        Queue<Character> queue = new LinkedList<>();
        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                queue.offer(c);
            }
        }
        
        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);
            
            for (char neighbor : graph.get(c)) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        if (result.length() != indegree.size()) return "";
        return result.toString();
    }
    
    // Time: O(n*k + u + v) where n=words, k=length, u,v=graph
    // Space: O(u + v) for graph
}
```

---

### Problem 15: Account Merge (Union-Find)

**Difficulty**: Hard  
**Topics**: Union-Find, Graph  
**Amazon Focus**: Real customer data problem  

```java
public class AccountMerge {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, String> parent = new HashMap<>();
        Map<String, String> owner = new HashMap<>();
        
        // Find operation
        java.util.function.Function<String, String> find = 
            new java.util.function.Function<String, String>() {
            public String apply(String x) {
                if (!parent.containsKey(x)) {
                    parent.put(x, x);
                }
                if (!parent.get(x).equals(x)) {
                    parent.put(x, apply(parent.get(x)));
                }
                return parent.get(x);
            }
        };
        
        // Union operation
        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                owner.put(email, name);
                
                String p = find.apply(email);
                String q = find.apply(account.get(1));
                
                if (!p.equals(q)) {
                    parent.put(p, q);
                }
            }
        }
        
        // Group by root
        Map<String, Set<String>> result = new HashMap<>();
        for (String email : owner.keySet()) {
            String root = find.apply(email);
            result.putIfAbsent(root, new TreeSet<>());
            result.get(root).add(email);
        }
        
        // Build result
        List<List<String>> finalResult = new ArrayList<>();
        for (String root : result.keySet()) {
            List<String> account = new ArrayList<>(result.get(root));
            account.add(0, owner.get(root));
            finalResult.add(account);
        }
        
        return finalResult;
    }
    
    // Time: O(n*k*α(n)) where α is inverse Ackermann
    // Space: O(n*k) for parent map
}
```

---

## 📊 AMAZON LEADERSHIP PRINCIPLES IN CODE

### How to Show Leadership in Interviews

**1. Customer Obsession**
```java
// Bad: Generic solution
// Good: Think about edge cases
if (nums == null || nums.length == 0) return 0;
if (nums.length == 1) return nums[0];
```

**2. Ownership**
```java
// Add comments explaining your choices
// I'm using HashMap instead of TreeMap because...
// We need O(1) lookups more than sorted order
Map<Integer, Integer> map = new HashMap<>();
```

**3. Invent and Simplify**
```java
// Show simpler approach first, then optimize
// V1: Brute force O(n^2) - clear and correct
// V2: Optimized O(n log n) - using insights from V1
```

**4. Are Right, A Lot**
```java
// Verify your solution with test cases
public void verifyAndTest(int[] nums, int expected) {
    int result = solution(nums);
    assert result == expected : "Test failed";
}
```

---

## 🎯 AMAZON-SPECIFIC TIPS

### Interview Day Checklist

- [ ] **Ask Clarifications**: "Can the array have duplicates? Empty?"
- [ ] **State Assumptions**: "I'm assuming inputs are valid, not null"
- [ ] **Explain Trade-offs**: "HashMap is faster but uses more memory"
- [ ] **Code in Real-time**: Show your debugging process
- [ ] **Test Edge Cases**: Empty, single element, duplicates, nulls
- [ ] **Discuss Scalability**: "How would this work with 1B items?"

### Amazon Wants You To Say

✅ "Let me think about the simpler case first..."  
✅ "I want to optimize for this use case..."  
✅ "The trade-off here is..."  
✅ "Let me trace through with an example..."  
✅ "How would we scale this to..."  

### Amazon Doesn't Want

❌ "I'll just use a library function..."  
❌ "This should work..."  
❌ Silence while coding  
❌ Not testing your code  
❌ Ignoring edge cases  

---

## 📈 PROGRESS TRACKING

**Daily Practice Plan**:
- Day 1-3: Problems 1-5 (Warm-up)
- Day 4-7: Problems 6-11 (Core)
- Day 8-12: Problems 12-17 (Advanced)
- Day 13-14: Problems 18-20 (System Design)
- Day 15: Mock interviews, timed practice

**Success Metrics**:
- Can solve each problem in 60% of allocated time
- Can explain trade-offs without prompting
- Write clean, commented code
- Ask clarifying questions naturally

---

**Created**: March 6, 2026  
**Problems**: 20+ with Amazon focus  
**Difficulty**: Easy (5), Medium (8), Hard (7)  
**Time Needed**: 20-30 hours  
**Unique Focus**: Scalability, Leadership Principles, Real-world problems

Get Amazon to say "Let's move forward" 🚀
