# Java Coding Templates for Interviews

> **Reusable Java patterns for coding interviews**
> Covers: algorithms, data structures, design patterns, concurrency, streams, testing

---

## Table of Contents
1. [Algorithm Templates](#algorithm-templates)
2. [Data Structure Templates](#data-structure-templates)
3. [LeetCode Java Boilerplate](#leetcode-java-boilerplate)
4. [Design Pattern Templates](#design-pattern-templates)
5. [Concurrency Templates](#concurrency-templates)
6. [Stream Pipeline Templates](#stream-pipeline-templates)
7. [Testing Templates](#testing-templates)

---

## Algorithm Templates

### Binary Search

```java
// Standard binary search — returns index or -1
int binarySearch(int[] arr, int target) {
    int l = 0, r = arr.length - 1;
    while (l <= r) {
        int m = l + (r - l) / 2;  // avoid overflow
        if (arr[m] == target) return m;
        if (arr[m] < target) l = m + 1;
        else r = m - 1;
    }
    return -1;
}

// Lower bound (first >= target)
int lowerBound(int[] arr, int target) {
    int l = 0, r = arr.length;
    while (l < r) {
        int m = l + (r - l) / 2;
        if (arr[m] >= target) r = m;
        else l = m + 1;
    }
    return l;
}

// Upper bound (first > target)
int upperBound(int[] arr, int target) {
    int l = 0, r = arr.length;
    while (l < r) {
        int m = l + (r - l) / 2;
        if (arr[m] > target) r = m;
        else l = m + 1;
    }
    return l;
}

// Binary search on answer space
int binarySearchAnswer(int lo, int hi, IntPredicate feasible) {
    while (lo < hi) {
        int m = lo + (hi - lo) / 2;
        if (feasible.test(m)) hi = m;
        else lo = m + 1;
    }
    return lo;
}
// Usage: binarySearchAnswer(0, n, x -> can(x))
```

### Sorting

```java
// QuickSelect — find kth largest/smallest (O(n) average)
int quickSelect(int[] arr, int k) {
    int l = 0, r = arr.length - 1;
    while (l < r) {
        int p = partition(arr, l, r);
        if (p == k) return arr[p];
        if (p < k) l = p + 1;
        else r = p - 1;
    }
    return arr[l];
}

int partition(int[] arr, int l, int r) {
    int pivot = arr[r];
    int i = l;
    for (int j = l; j < r; j++) {
        if (arr[j] <= pivot) {
            swap(arr, i++, j);
        }
    }
    swap(arr, i, r);
    return i;
}

void swap(int[] arr, int i, int j) {
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
}

// Merge sort
void mergeSort(int[] arr, int l, int r, int[] tmp) {
    if (l >= r) return;
    int m = l + (r - l) / 2;
    mergeSort(arr, l, m, tmp);
    mergeSort(arr, m + 1, r, tmp);
    merge(arr, l, m, r, tmp);
}

void merge(int[] arr, int l, int m, int r, int[] tmp) {
    System.arraycopy(arr, l, tmp, l, r - l + 1);
    int i = l, j = m + 1, k = l;
    while (i <= m && j <= r) {
        arr[k++] = (tmp[i] <= tmp[j]) ? tmp[i++] : tmp[j++];
    }
    while (i <= m) arr[k++] = tmp[i++];
}
```

### Tree Traversals

```java
// Binary tree node
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

// Pre-order (iterative)
List<Integer> preorder(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    if (root != null) stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode n = stack.pop();
        res.add(n.val);
        if (n.right != null) stack.push(n.right);
        if (n.left != null) stack.push(n.left);
    }
    return res;
}

// In-order (iterative)
List<Integer> inorder(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root;
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) {
            stack.push(cur);
            cur = cur.left;
        }
        cur = stack.pop();
        res.add(cur.val);
        cur = cur.right;
    }
    return res;
}

// Post-order (iterative)
List<Integer> postorder(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root, last = null;
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) {
            stack.push(cur);
            cur = cur.left;
        }
        cur = stack.peek();
        if (cur.right == null || cur.right == last) {
            res.add(cur.val);
            stack.pop();
            last = cur;
            cur = null;
        } else {
            cur = cur.right;
        }
    }
    return res;
}

// Level-order (BFS)
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int sz = q.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < sz; i++) {
            TreeNode n = q.poll();
            level.add(n.val);
            if (n.left != null) q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
        res.add(level);
    }
    return res;
}

// Lowest Common Ancestor
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode l = lca(root.left, p, q);
    TreeNode r = lca(root.right, p, q);
    if (l != null && r != null) return root;
    return l != null ? l : r;
}

// Diameter of Binary Tree
int diameter(TreeNode root) {
    return dfs(root)[1];  // [height, diameter]
}

int[] dfs(TreeNode n) {
    if (n == null) return new int[]{0, 0};
    int[] l = dfs(n.left), r = dfs(n.right);
    int h = 1 + Math.max(l[0], r[0]);
    int d = Math.max(Math.max(l[1], r[1]), l[0] + r[0]);
    return new int[]{h, d};
}
```

### Graph Traversals

```java
// DFS (recursive with visited set)
void dfs(int node, Map<Integer, List<Integer>> graph, Set<Integer> visited) {
    visited.add(node);
    for (int nei : graph.getOrDefault(node, List.of())) {
        if (!visited.contains(nei)) dfs(nei, graph, visited);
    }
}

// BFS with level tracking
int bfs(int start, int target, Map<Integer, List<Integer>> graph) {
    Queue<Integer> q = new ArrayDeque<>();
    Set<Integer> visited = new HashSet<>();
    q.offer(start);
    visited.add(start);
    int dist = 0;
    while (!q.isEmpty()) {
        int sz = q.size();
        for (int i = 0; i < sz; i++) {
            int n = q.poll();
            if (n == target) return dist;
            for (int nei : graph.getOrDefault(n, List.of())) {
                if (visited.add(nei)) q.offer(nei);
            }
        }
        dist++;
    }
    return -1;
}

// Dijkstra's shortest path
int[] dijkstra(int start, Map<Integer, List<int[]>> graph, int n) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;
    Queue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{start, 0});
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], d = cur[1];
        if (d > dist[u]) continue;
        for (int[] edge : graph.getOrDefault(u, List.of())) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.offer(new int[]{v, dist[v]});
            }
        }
    }
    return dist;
}

// Union-Find (Disjoint Set Union)
class DSU {
    int[] parent, rank;
    DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) { int t = px; px = py; py = t; }
        parent[py] = px;
        if (rank[px] == rank[py]) rank[px]++;
        return true;
    }
}

// Topological Sort (Kahn's algorithm)
int[] topoSort(Map<Integer, List<Integer>> graph, int n) {
    int[] indeg = new int[n];
    for (int u : graph.keySet()) {
        for (int v : graph.get(u)) indeg[v]++;
    }
    Queue<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (indeg[i] == 0) q.offer(i);
    int[] res = new int[n];
    int idx = 0;
    while (!q.isEmpty()) {
        int u = q.poll();
        res[idx++] = u;
        for (int v : graph.getOrDefault(u, List.of())) {
            if (--indeg[v] == 0) q.offer(v);
        }
    }
    return idx == n ? res : new int[0];  // empty if cycle
}
```

---

## Data Structure Templates

### Trie

```java
class Trie {
    class Node {
        Node[] children = new Node[26];
        boolean isEnd;
    }
    Node root = new Node();
    
    void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (cur.children[idx] == null) cur.children[idx] = new Node();
            cur = cur.children[idx];
        }
        cur.isEnd = true;
    }
    
    boolean search(String word) {
        Node n = traverse(word);
        return n != null && n.isEnd;
    }
    
    boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }
    
    private Node traverse(String s) {
        Node cur = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (cur.children[idx] == null) return null;
            cur = cur.children[idx];
        }
        return cur;
    }
}
```

### Segment Tree

```java
class SegmentTree {
    int n;
    int[] tree;
    
    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }
    
    private void build(int[] arr, int node, int l, int r) {
        if (l == r) { tree[node] = arr[l]; return; }
        int m = l + (r - l) / 2;
        build(arr, node * 2, l, m);
        build(arr, node * 2 + 1, m + 1, r);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }
    
    void update(int idx, int val) { update(1, 0, n - 1, idx, val); }
    
    private void update(int node, int l, int r, int idx, int val) {
        if (l == r) { tree[node] = val; return; }
        int m = l + (r - l) / 2;
        if (idx <= m) update(node * 2, l, m, idx, val);
        else update(node * 2 + 1, m + 1, r, idx, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }
    
    int query(int ql, int qr) { return query(1, 0, n - 1, ql, qr); }
    
    private int query(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return tree[node];
        int m = l + (r - l) / 2;
        return query(node * 2, l, m, ql, qr) +
               query(node * 2 + 1, m + 1, r, ql, qr);
    }
}
```

### LRU Cache

```java
class LRUCache {
    class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }
    
    final int capacity;
    Map<Integer, Node> map = new HashMap<>();
    Node head = new Node(0, 0), tail = new Node(0, 0);
    
    LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    
    int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        moveToHead(n);
        return n.val;
    }
    
    void put(int key, int value) {
        Node n = map.get(key);
        if (n != null) {
            n.val = value;
            moveToHead(n);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }
        n = new Node(key, value);
        map.put(key, n);
        addToHead(n);
    }
    
    private void moveToHead(Node n) { remove(n); addToHead(n); }
    private void addToHead(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }
    private void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
}
```

### Min Stack (O(1) getMin)

```java
class MinStack {
    Deque<Integer> stack = new ArrayDeque<>();
    Deque<Integer> minStack = new ArrayDeque<>();
    
    void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) minStack.push(val);
    }
    void pop() {
        if (stack.pop().equals(minStack.peek())) minStack.pop();
    }
    int top() { return stack.peek(); }
    int getMin() { return minStack.peek(); }
}
```

---

## LeetCode Java Boilerplate

### Fast I/O Template

```java
import java.io.*;
import java.util.*;

public class Main {
    static class FastReader {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        
        String next() throws IOException {
            while (st == null || !st.hasMoreTokens()) 
                st = new StringTokenizer(br.readLine());
            return st.nextToken();
        }
        int nextInt() throws IOException { return Integer.parseInt(next()); }
        long nextLong() throws IOException { return Long.parseLong(next()); }
        double nextDouble() throws IOException { return Double.parseDouble(next()); }
        int[] nextIntArray(int n) throws IOException {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = nextInt();
            return arr;
        }
        String nextLine() throws IOException { return br.readLine(); }
    }
    
    public static void main(String[] args) throws IOException {
        FastReader fr = new FastReader();
        PrintWriter pw = new PrintWriter(System.out);
        int t = fr.nextInt();
        while (t-- > 0) {
            solve(fr, pw);
        }
        pw.flush();
    }
    
    static void solve(FastReader fr, PrintWriter pw) throws IOException {
        int n = fr.nextInt();
        // Problem-specific logic
        pw.println(result);
    }
}
```

### Modular Code Structure for LeetCode

```java
class Solution {
    // Public method expected by LeetCode
    public int optimalSolution(int[] nums) {
        // 1. Input validation
        if (nums == null || nums.length == 0) return 0;
        
        // 2. Initialize data structures
        Map<Integer, Integer> map = new HashMap<>();
        int result = 0;
        
        // 3. Algorithm
        for (int num : nums) {
            // 4. Processing
            result = Math.max(result, process(num));
        }
        
        // 5. Return result
        return result;
    }
    
    // Helper method (keeps main method clean)
    private int process(int x) {
        return x * x;
    }
    
    // Alternative: if multiple approaches, show commented versions
    // public int bruteForce(int[] nums) { ... }
}
```

---

## Design Pattern Templates

### Singleton (Thread-Safe)

```java
// Bill Pugh Singleton — best for most cases
class Singleton {
    private Singleton() {}
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() { return Holder.INSTANCE; }
}

// Enum Singleton — best for serialization safety
enum SingletonEnum { INSTANCE }

// Double-checked locking (Java 5+ with volatile)
class SingletonDCL {
    private static volatile SingletonDCL instance;
    private SingletonDCL() {}
    public static SingletonDCL getInstance() {
        if (instance == null) {
            synchronized (SingletonDCL.class) {
                if (instance == null) instance = new SingletonDCL();
            }
        }
        return instance;
    }
}
```

### Factory Pattern

```java
// Functional approach (no separate factory class needed)
enum PaymentType { CREDIT_CARD, PAYPAL, CRYPTO }

interface PaymentProcessor { void process(double amount); }

class PaymentFactory {
    private static final Map<PaymentType, PaymentProcessor> PROCESSORS = Map.of(
        PaymentType.CREDIT_CARD, amount -> System.out.println("CC: $" + amount),
        PaymentType.PAYPAL,      amount -> System.out.println("PayPal: $" + amount),
        PaymentType.CRYPTO,      amount -> System.out.println("Crypto: $" + amount)
    );
    
    static PaymentProcessor getProcessor(PaymentType type) {
        PaymentProcessor p = PROCESSORS.get(type);
        if (p == null) throw new IllegalArgumentException("Unknown: " + type);
        return p;
    }
}
```

### Builder Pattern

```java
// Lombok-style builder (manual, for interviews)
class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    
    private HttpRequest(Builder b) {
        this.url = b.url;
        this.method = b.method;
        this.headers = Collections.unmodifiableMap(new HashMap<>(b.headers));
        this.body = b.body;
    }
    
    static Builder builder(String url) { return new Builder(url); }
    
    static class Builder {
        private final String url;
        private String method = "GET";
        private Map<String, String> headers = new HashMap<>();
        private String body;
        
        Builder(String url) { this.url = url; }
        Builder method(String method) { this.method = method; return this; }
        Builder header(String k, String v) { headers.put(k, v); return this; }
        Builder body(String body) { this.body = body; return this; }
        HttpRequest build() { return new HttpRequest(this); }
    }
}
// Usage: HttpRequest req = HttpRequest.builder("https://api.example.com")
//         .method("POST").header("Content-Type", "application/json")
//         .body("{\"key\":\"value\"}").build();
```

### Strategy Pattern

```java
// Functional interface approach (no interfaces/classes needed)
interface SortStrategy { void sort(int[] arr); }

class Sorter {
    private SortStrategy strategy;
    Sorter(SortStrategy strategy) { this.strategy = strategy; }
    void setStrategy(SortStrategy strategy) { this.strategy = strategy; }
    void sort(int[] arr) { strategy.sort(arr); }
}

// Usage
SortStrategy quickSort = arr -> Arrays.sort(arr);  // Arrays.sort uses Dual-Pivot QuickSort
SortStrategy mergeSort = arr -> mergeSort(arr, 0, arr.length - 1, new int[arr.length]);

Sorter s = new Sorter(quickSort);
s.sort(data);
s.setStrategy(mergeSort);
s.sort(data);
```

---

## Concurrency Templates

### Thread Pool Template

```java
class ThreadPool {
    private final ThreadPoolExecutor executor;
    
    ThreadPool(int coreThreads, int maxThreads, int queueCapacity) {
        this.executor = new ThreadPoolExecutor(
            coreThreads, maxThreads,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.allowCoreThreadTimeOut(true);
    }
    
    Future<?> submit(Runnable task) {
        return executor.submit(task);
    }
    
    <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
    
    void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### CompletableFuture Pipeline

```java
// Sequential pipeline
CompletableFuture.supplyAsync(() -> fetchUser(id))
    .thenApplyAsync(user -> enrichUser(user))
    .thenAcceptAsync(user -> cacheUser(user))
    .exceptionally(err -> { log.error("Failed", err); return null; });

// Parallel composition
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> fetchUser(id));
CompletableFuture<Order> orderFuture = CompletableFuture.supplyAsync(() -> fetchOrder(id));

CompletableFuture<Response> response = userFuture
    .thenCombine(orderFuture, (user, order) -> new Response(user, order));

// All of
List<CompletableFuture<Data>> futures = ids.stream()
    .map(id -> CompletableFuture.supplyAsync(() -> fetchData(id)))
    .toList();

CompletableFuture<List<Data>> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
    .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());

// Timeout handling
CompletableFuture.supplyAsync(() -> fetchData(id))
    .orTimeout(5, TimeUnit.SECONDS)
    .exceptionally(err -> fallbackData());
```

### Virtual Thread Patterns (Java 21+)

```java
// Simple usage
Thread vThread = Thread.startVirtualThread(() -> handleRequest(req));

// Executor with virtual threads
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<Result>> futures = tasks.stream()
        .map(task -> executor.submit(() -> process(task)))
        .toList();
    for (var f : futures) f.get();  // More concurrent than platform threads
}

// StructuredTaskScope
Response handleRequest(Request req) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<User> user = scope.fork(() -> fetchUser(req.userId()));
        Future<Order> order = scope.fork(() -> fetchOrder(req.orderId()));
        scope.join();
        scope.throwIfFailed();
        return new Response(user.resultNow(), order.resultNow());
    }
}

// Scoped values
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

void handle(HttpExchange exchange) {
    User user = authenticate(exchange);
    ScopedValue.where(CURRENT_USER, user).run(() -> process(exchange));
}

void process(HttpExchange exchange) {
    User user = CURRENT_USER.get();  // No ThreadLocal.get() overhead
}
```

### Producer-Consumer Template

```java
class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    
    BlockingQueue(int capacity) { this.capacity = capacity; }
    
    synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) wait();
        queue.offer(item);
        notifyAll();
    }
    
    synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) wait();
        T item = queue.poll();
        notifyAll();
        return item;
    }
}
```

---

## Stream Pipeline Templates

### Filter-Map-Reduce

```java
// Classic pipeline
int total = orders.stream()
    .filter(o -> o.status() == Order.Status.COMPLETED)
    .filter(o -> o.amount() > 100)
    .mapToInt(Order::amount)
    .sum();

// With custom collector
Map<Category, IntSummaryStatistics> stats = products.stream()
    .collect(Collectors.groupingBy(
        Product::category,
        Collectors.summarizingInt(Product::price)
    ));
```

### Grouping and Partitioning

```java
// Group by single field
Map<Department, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::department));

// Group by with downstream
Map<Department, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));

// Multi-level grouping
Map<Department, Map<String, List<Employee>>> byDeptAndLevel = employees.stream()
    .collect(Collectors.groupingBy(Employee::department,
             Collectors.groupingBy(Employee::level)));

// Partitioning (two groups)
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() > 100_000));
```

### Custom Collector

```java
// Collect to ImmutableList
List<String> result = stream.collect(
    Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    )
);

// Collect to EnumMap
Map<DayOfWeek, List<Event>> byDay = events.stream()
    .collect(Collectors.groupingBy(
        Event::dayOfWeek,
        () -> new EnumMap<>(DayOfWeek.class),
        Collectors.toList()
    ));
```

### Parallel Stream Caution

```java
// Good use case: independent, CPU-intensive operations
long count = largeList.parallelStream()
    .filter(ExpensiveFilter::test)  // CPU-bound
    .count();

// Bad use case: I/O-bound, shared mutable state
List<Integer> results = Collections.synchronizedList(new ArrayList<>());
stream.parallel()
    .map(i -> fetchFromNetwork(i))  // I/O — use CompletableFuture instead
    .forEach(results::add);          // Shared mutable state — bad
```

---

## Testing Templates

### JUnit 5 Basics

```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class CalculatorTest {
    
    private Calculator calc;
    
    @BeforeEach
    void setUp() { calc = new Calculator(); }
    
    @Test
    void shouldAddTwoNumbers() {
        assertEquals(5, calc.add(2, 3));
    }
    
    @Test
    void shouldThrowOnDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> calc.divide(1, 0));
    }
    
    @ParameterizedTest
    @CsvSource({"0,1,1", "1,2,3", "10,20,30"})
    void shouldAdd(int a, int b, int expected) {
        assertEquals(expected, calc.add(a, b));
    }
    
    @Test
    void shouldMatchMultipleAssertions() {
        assertAll("properties",
            () -> assertEquals(4, calc.add(2, 2)),
            () -> assertEquals(0, calc.subtract(2, 2)),
            () -> assertTrue(calc.isPositive(5))
        );
    }
}
```

### Mockito Templates

```java
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)  // Auto-init mocks
class OrderServiceTest {
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @Mock
    private InventoryService inventoryService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void shouldProcessOrder() {
        when(inventoryService.isAvailable(anyString())).thenReturn(true);
        when(paymentGateway.charge(anyDouble())).thenReturn(new PaymentResult("txn_123"));
        
        OrderResult result = orderService.placeOrder(new Order("SKU-001", 50.00));
        
        assertEquals("SUCCESS", result.status());
        verify(paymentGateway).charge(50.00);
        verify(inventoryService).reserve("SKU-001");
    }
    
    @Test
    void shouldHandlePaymentFailure() {
        when(paymentGateway.charge(anyDouble())).thenThrow(new PaymentException("Insufficient funds"));
        
        assertThrows(PaymentException.class, () -> orderService.placeOrder(new Order("SKU-001", 1000.00)));
        
        verify(paymentGateway, times(3)).charge(anyDouble());  // Retry logic
    }
}
```

### TestContainers Template

```java
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class UserRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    private DataSource dataSource;
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        dataSource = createDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        userRepository = new UserRepository(dataSource);
    }
    
    @Test
    void shouldSaveAndRetrieveUser() {
        userRepository.save(new User("alice", "alice@example.com"));
        User found = userRepository.findByEmail("alice@example.com");
        assertEquals("alice", found.name());
    }
}
```

### AssertJ Fluent Assertions

```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldUseFluentAssertions() {
    // Collection assertions
    assertThat(result)
        .hasSize(3)
        .contains("expected", "also-expected")
        .doesNotContain("unexpected")
        .allMatch(s -> s.length() > 2);
    
    // Exception assertions
    assertThatThrownBy(() -> riskyOperation())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("invalid");
    
    // Custom assertions
    assertThat(person)
        .extracting(Person::name, Person::age)
        .containsExactly("Alice", 30);
    
    // Soft assertions (collect all failures)
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(result).isNotEmpty();
    softly.assertThat(result.get(0)).isEqualTo("expected");
    softly.assertAll();  // Throws aggregate failure
}
```

---

## Quick Reference: Complexity Cheat Sheet

| Data Structure | Access | Search | Insert | Delete | Space |
|---------------|--------|--------|--------|--------|-------|
| ArrayList | O(1) | O(n) | O(n) | O(n) | O(n) |
| LinkedList | O(n) | O(n) | O(1) | O(1) | O(n) |
| HashMap | O(1) | O(1) | O(1) | O(1) | O(n) |
| TreeMap | O(log n) | O(log n) | O(log n) | O(log n) | O(n) |
| HashSet | — | O(1) | O(1) | O(1) | O(n) |
| PriorityQueue | O(1)(min) | O(n) | O(log n) | O(log n) | O(n) |
| ArrayDeque | O(1) | O(n) | O(1) | O(1) | O(n) |

| Sorting Algorithm | Best | Average | Worst | Space |
|------------------|------|---------|-------|-------|
| Arrays.sort() (Dual-Pivot QuickSort) | O(n log n) | O(n log n) | O(n²) | O(log n) |
| Arrays.parallelSort() | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Collections.sort() (TimSort) | O(n) | O(n log n) | O(n log n) | O(n) |

---

> *"The best code is the code you don't have to write. These templates exist so you can focus on the interview problem's core logic, not boilerplate."*
