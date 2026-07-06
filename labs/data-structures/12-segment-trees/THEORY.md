# Theory: Segment Trees

## Fundamentals

A segment tree is a binary tree data structure used for storing intervals or segments. It allows querying which segments contain a given point or overlapping intervals. Segment trees support range queries (sum, min, max, etc.) and range updates in O(log n) time.

## Structure

A segment tree for an array of n elements:
- Is a full binary tree with n leaves (each leaf represents one array element)
- Has about 2n-1 nodes total (approximately 4n in array representation)
- Each internal node represents the union of its children's intervals
- The root represents the entire array [0, n-1]

### Array Representation

For a segment tree stored in an array:
- Root at index 1 (or 0)
- Left child of node i: 2*i (or 2*i+1)
- Right child of node i: 2*i+1 (or 2*i+2)
- Array size needed: 4*n (safe bound)

## Operations

### Construction

Build the tree recursively:
`
function build(node, l, r):
    if l == r:
        tree[node] = arr[l]
    else:
        mid = (l + r) / 2
        build(2*node, l, mid)
        build(2*node+1, mid+1, r)
        tree[node] = tree[2*node] + tree[2*node+1]
`

### Range Query

Query sum over [ql, qr]:
`
function query(node, l, r, ql, qr):
    if ql <= l && r <= qr: return tree[node]
    if qr < l || r < ql: return 0
    mid = (l + r) / 2
    left = query(2*node, l, mid, ql, qr)
    right = query(2*node+1, mid+1, r, ql, qr)
    return left + right
`

### Point Update

Update element at position pos to value val:
`
function update(node, l, r, pos, val):
    if l == r:
        tree[node] = val
    else:
        mid = (l + r) / 2
        if pos <= mid: update(2*node, l, mid, pos, val)
        else: update(2*node+1, mid+1, r, pos, val)
        tree[node] = tree[2*node] + tree[2*node+1]
`

## Lazy Propagation

For range updates (e.g., adding x to all elements in [l, r]), lazy propagation delays updates by storing pending operations in internal nodes:

- A lazy[] array stores pending updates
- When a node is fully covered by an update, update its value and mark it lazy
- When traversing, push lazy values to children before recursing

## Complexity

| Operation | Segment Tree |
|-----------|-------------|
| Construction | O(n) |
| Range Query | O(log n) |
| Point Update | O(log n) |
| Range Update (lazy) | O(log n) |
| Space | O(n) |
