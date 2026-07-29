# Guide: Merkle Tree (Hash Tree)

## Overview

A **Merkle Tree** (also called a **Hash Tree**) is a tree in which every leaf node is labelled with the cryptographic hash of a data block, and every non-leaf node is labelled with the cryptographic hash of its children's labels. This structure enables efficient and secure verification of data integrity.

Invented by Ralph Merkle in 1979, Merkle trees are fundamental to blockchain (Bitcoin, Ethereum), distributed systems (Cassandra, DynamoDB), version control (Git), and certificate transparency.

### Why Not Just Use SHA-256 on the Whole Data?

| Aspect | Single Hash | Merkle Tree |
|--------|------------|-------------|
| Verification unit | Whole file | Any block |
| Data changed | Rehash everything | Rehash affected tree path |
| Proof size | Entire file | O(log n) hashes |
| Parallel verification | No | Yes |
| Partial data access | No | Yes |

**Key Insight**: Merkle trees enable **proof of inclusion** — proving that a specific data block belongs to a set without revealing the rest of the set.

---

## ASCII Diagram

```
           Root = H(H00 + H01)
          /                  \
    H00 = H(H0 + H1)    H01 = H(H2 + H3)
      /        \          /        \
    H0        H1        H2        H3
    |         |         |         |
  Block0    Block1    Block2    Block3
```

### Merkle Proof

To prove that Block1 is included:

```
Proof: [H0, H01]
Verification:
  1. Hash(Block1) → H1
  2. Hash(H0 + H1) → H00
  3. Hash(H00 + H01) → Root
  4. Compare with known Root
```

Proof size = O(log n). Verification time = O(log n).

---

## Source Code Walkthrough

The implementation is in `src/MerkleTree.java`.

### Node Structure (lines ~10-14)

```java
private static class Node {
    String hash;
    Node left, right;
    Node(String hash) { this.hash = hash; }
}
```

### Build (lines ~16-35)

```java
public MerkleTree(List<String> data) {
    if (data == null || data.isEmpty()) return;
    List<Node> leaves = new ArrayList<>();
    for (String d : data) leaves.add(new Node(hash(d)));
    root = build(leaves);
}

private Node build(List<Node> nodes) {
    if (nodes.size() == 1) return nodes.get(0);
    List<Node> parents = new ArrayList<>();
    for (int i = 0; i < nodes.size(); i += 2) {
        Node left = nodes.get(i);
        Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : nodes.get(i);
        // If odd count, duplicate last node
        Node parent = new Node(hash(left.hash + right.hash));
        parent.left = left;
        parent.right = right;
        parents.add(parent);
    }
    return build(parents);
}
```

**Walkthrough `build(["data1", "data2", "data3"])`:**

```
Leaves: [hash("data1"), hash("data2"), hash("data3")]

Round 1 (build parents):
  Pair 0: left=hash1, right=hash2 → parent = hash(hash1+hash2)
  Pair 1: left=hash3, right=hash3 (duplicated) → parent = hash(hash3+hash3)
  Parents: [p0, p1]

Round 2 (build root):
  Pair 0: left=p0, right=p1 → root = hash(p0+p1)

Tree:        root
           /      \
         p0        p1
        /  \      /  \
     hash1 hash2 hash3 hash3 (duplicate)
```

### rootHash (line ~37)

```java
public String rootHash() {
    return root == null ? "" : root.hash;
}
```

### verify (lines ~39-42)

```java
public boolean verify(List<String> data) {
    MerkleTree other = new MerkleTree(data);
    return rootHash().equals(other.rootHash());
}
```

### getProof (lines ~44-63)

```java
public List<String> getProof(String leafData) {
    String targetHash = hash(leafData);
    List<String> proof = new ArrayList<>();
    findProof(root, targetHash, proof);
    return proof;
}

private boolean findProof(Node node, String target, List<String> proof) {
    if (node == null) return false;
    if (node.left == null && node.right == null) return node.hash.equals(target);

    if (node.left != null && findProof(node.left, target, proof)) {
        proof.add("R:" + (node.right != null ? node.right.hash : node.left.hash));
        return true;
    }
    if (node.right != null && findProof(node.right, target, proof)) {
        proof.add("L:" + (node.left != null ? node.left.hash : node.right.hash));
        return true;
    }
    return false;
}
```

**Proof format**: `"L:hash"` or `"R:hash"` indicating sibling position.

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Build | O(n) | O(n) | n = number of leaf blocks |
| Root hash | O(1) | O(1) | Cached at root |
| Verify (full) | O(n) | O(n) | Builds another tree |
| Proof generation | O(log n) | O(log n) | DFS to find leaf |
| Proof verification | O(log n) | O(1) | Only hashes |

### Tree Properties
- **Height**: ceil(log₂ n) for n leaves
- **Nodes**: 2·ceil(n) - 1
- **Proof size**: ceil(log₂ n) hashes
- **Security**: Preimage resistance + collision resistance of hash function

---

## Comparison with Alternatives

| Feature | Merkle Tree | Hash List | Merkle DAG | Bloom Filter |
|---------|------------|-----------|------------|-------------|
| Proof size | O(log n) | O(n) | O(log n) | O(1) FP |
| Verification time | O(log n) | O(n) | O(log n) | O(k) FP |
| Parallel verification | Yes | No | Yes | N/A |
| Tamper detection | Any block | Whole data | Any block | Data loss |
| Partial verification | Yes | No | Yes | No |
| Blockchain usage | Core | No | Ethereum | SPV nodes |

**When NOT to use Merkle tree:**
- Data never changes and fits in one hash (use single hash)
- Need membership testing with space constraints (use Bloom filter)
- Data is small (< 1KB): overhead of tree structure not worth it
- Tamper detection isn't needed (use simple checksum)

---

## Use Cases

### 1. Blockchain (Bitcoin)
**System**: Block header contains Merkle root of all transactions
**Why**: SPV (Simplified Payment Verification) — light clients verify transactions by getting Merkle proof from full nodes, without downloading the entire block.
**Scale**: 2000+ transactions per block, proof size = 11 hashes (~352 bytes)

### 2. Git
**System**: Content-addressable filesystem
**Why**: Git stores directory tree as a Merkle tree. Each commit points to a tree object, which recursively hashes subtrees and files. Changing any file changes the tree hash.
**Operations**: `git diff` compares Merkle roots.

### 3. Certificate Transparency
**System**: Public log of SSL/TLS certificates
**Why**: Append-only Merkle tree. Certificate authorities submit certificates. Anyone can verify that a certificate is in the log (inclusion proof). Anyone can verify log consistency (append-only proof).

### 4. Distributed Database Anti-Entropy (Cassandra, DynamoDB)
**System**: Replica synchronisation
**Why**: Each replica builds a Merkle tree of its data. Compare roots → if different, recursively compare subtrees → find differing leaf blocks. Only repair what's different.

### 5. File Synchronisation (BitTorrent, IPFS)
**System**: P2P file distribution
**Why**: Split file into blocks, build Merkle tree. Peers verify blocks they download using Merkle proof. Allows downloading from multiple peers while verifying integrity.

### 6. Software Update Integrity
**System**: OS/firmware updates
**Why**: Manufacturer signs the Merkle root. Client downloads blocks from any source. Each block verified against root via Merkle proof.

---

## Common Pitfalls

### 1. Odd Leaf Count
If leaf count is odd, the last leaf must be duplicated (or hashed with itself). Different implementations handle this differently — must be consistent.

### 2. Hash Function Selection
- **SHA-256**: Standard for most applications (Bitcoin, Git)
- **SHA-512**: Higher security, slower
- **BLAKE2**: Faster than SHA-256, used in some modern systems
- **Don't use**: MD5, SHA-1 (collision vulnerabilities)

### 3. Concatenation Order
`hash(left + right)` vs `hash(right + left)` — must be consistent. Standard is left then right.

### 4. Empty Tree
Building a Merkle tree from empty data: should return a predefined "empty root" hash, often hash("") or hash(null).

### 5. Security Assumptions
Merkle trees rely on the hash function being collision-resistant. If you can find two different data blocks with the same hash, you can forge Merkle proofs.

---

## Advanced Variants

### Merkle Patricia Trie (Ethereum)
Combines Merkle tree with Patricia trie. Each node is keyed by a hex-prefix path. Used for Ethereum state, transaction, and receipt trees. Supports efficient proof of key-value pairs.

### Sparse Merkle Tree
For key-value stores where most keys are empty (unused). Uses default hash for empty subtrees. Proof size still O(log n) but space is O(k log n) where k = number of non-empty entries.

### Merkle Mountain Range
Append-only Merkle tree variant. Maintains a set of perfect Merkle trees (peaks). Efficient for continuous, append-only logs. Used in Mimblewimble, FlyClient.

### Verifiable Data Structures
Generalisation: Merkle trees + digital signatures + cryptographic commitments. Used in certificate transparency, CONIKS (key transparency), and blockchain light clients.

---

## Testing the Implementation

```java
MerkleTree mt = new MerkleTree(List.of("a", "b", "c", "d"));
String root1 = mt.rootHash();

// Verify full data
assert mt.verify(List.of("a", "b", "c", "d")) == true;

// Detect tampered data
assert mt.verify(List.of("a", "b", "x", "d")) == false;
```

### Merkle Proof Verification
```java
String target = "b";
List<String> proof = mt.getProof(target);

// Verify proof manually
String hash = sha256(target);
for (String p : proof) {
    String[] parts = p.split(":", 2);
    String siblingHash = parts[1];
    if (parts[0].equals("L")) {
        hash = sha256(hash + siblingHash);
    } else {
        hash = sha256(siblingHash + hash);
    }
}
assert hash.equals(mt.rootHash()) : "Proof verification failed";
```

### Edge Cases
```java
// Empty tree
MerkleTree empty = new MerkleTree(List.of());
assert empty.rootHash().equals("");

// Single element
MerkleTree single = new MerkleTree(List.of("only"));
assert single.verify(List.of("only"));
assert single.getProof("only").size() == 0; // root is leaf

// Odd number of leaves
MerkleTree odd = new MerkleTree(List.of("a", "b", "c"));
assert odd.verify(List.of("a", "b", "c"));
```

---

## Key Interview Takeaways

1. **Merkle tree = efficient integrity verification for block data**. O(log n) proof size, O(log n) verification.

2. **Proof of inclusion**: Given a Merkle root, you can prove any leaf belongs to the tree without revealing other leaves.

3. **Consistency proof**: For append-only logs, you can prove the current log is an extension of a previous log (important for CT and blockchain).

4. **Levels**: Build recursively from leaves to root. Odd leaf duplicated. Proof collected by DFS.

5. **Real impact**: Bitcoin, Git, Certificate Transparency, Cassandra anti-entropy, BitTorrent.

6. **Security**: Relies entirely on the hash function's collision resistance. SHA-256 minimum.

7. **Variants**: Merkle Patricia Trie (Ethereum), Sparse Merkle Tree (key-value stores), Merkle Mountain Range (append-only).