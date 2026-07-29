# Mock Interview: Merkle Tree

## Setting

- **Round**: System design + cryptographic data structures
- **Duration**: 60 minutes
- **Focus**: Merkle tree, proof generation, blockchain systems

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** What's a Merkle tree and what problems does it solve?

**Candidate:** A Merkle tree is a binary tree where each leaf contains the hash of a data block, and each internal node contains the hash of its two children's hashes. The root represents a commitment to the entire data set.

**Key problems solved:**
1. **Efficient verification**: Verify any data block with O(log n) proof size
2. **Tamper detection**: Any change anywhere in the data changes the root
3. **Parallel verification**: Multiple proofs can be verified independently
4. **Privacy**: Prove a block exists without revealing other blocks

**Interviewer:** Show me the code for building a Merkle tree from a list of data blocks.

**Candidate:**

```java
class MerkleTree {
    class Node {
        String hash;
        Node left, right;
        Node(String h) { hash = h; }
    }

    Node root;

    MerkleTree(List<String> data) {
        List<Node> leaves = new ArrayList<>();
        for (String d : data) leaves.add(new Node(sha256(d)));
        root = build(leaves);
    }

    Node build(List<Node> nodes) {
        if (nodes.size() == 1) return nodes.get(0);
        List<Node> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            Node left = nodes.get(i);
            Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;
            parents.add(new Node(sha256(left.hash + right.hash)));
        }
        return build(parents);
    }

    String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes());
            return HexFormat.of().formatHex(d);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
```

---

### Part 2: Core Problem — Design a Proof-of-Reserves System (30 min)

**Interviewer:** A cryptocurrency exchange wants to prove it has enough funds to cover all user deposits without revealing individual balances. Design a system.

**Candidate:** The exchange builds a Merkle tree where each leaf is (userID, balance). They publish the Merkle root and a signed statement. Each user receives a Merkle proof that their balance is included.

Let me implement this:

```java
// Simplified for interview
class ProofOfReserves {
    static class UserLeaf {
        String userId;
        long balance;

        String toHashString() {
            return userId + ":" + balance;
        }
    }

    class Node {
        String hash;
        long totalBalance; // sum of balances in subtree
        Node left, right;
        String userId; // only for leaf nodes

        Node(String hash, long totalBalance) {
            this.hash = hash;
            this.totalBalance = totalBalance;
        }
    }

    Node buildTree(List<UserLeaf> users) {
        List<Node> leaves = new ArrayList<>();
        for (UserLeaf u : users) {
            Node leaf = new Node(sha256(u.toHashString()), u.balance);
            leaf.userId = u.userId;
            leaves.add(leaf);
        }
        return build(leaves);
    }

    Node build(List<Node> nodes) {
        if (nodes.size() == 1) return nodes.get(0);
        List<Node> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            Node left = nodes.get(i);
            Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;
            Node parent = new Node(
                sha256(left.hash + right.hash),
                left.totalBalance + right.totalBalance
            );
            parent.left = left;
            parent.right = right;
            parents.add(parent);
        }
        return build(parents);
    }

    // Generate proof for a user
    List<String> generateProof(String userId, Node root) {
        List<String> proof = new ArrayList<>();
        findProofPath(root, userId, proof);
        return proof;
    }

    boolean findProofPath(Node node, String userId, List<String> proof) {
        if (node == null) return false;
        if (node.userId != null && node.userId.equals(userId)) return true;

        if (node.left != null && findProofPath(node.left, userId, proof)) {
            String siblingHash = node.right != null ? node.right.hash : node.left.hash;
            proof.add("R:" + siblingHash);
            return true;
        }
        if (node.right != null && findProofPath(node.right, userId, proof)) {
            String siblingHash = node.left != null ? node.left.hash : node.right.hash;
            proof.add("L:" + siblingHash);
            return true;
        }
        return false;
    }
}
```

**Interviewer:** How does the user verify their proof?

**Candidate:** The user:
1. Gets their proof (sibling hashes) from the exchange
2. Computes their leaf hash = sha256(userId + ":" + balance)
3. Walks up the proof: for each sibling hash, combines as instructed (L or R)
4. Compares the resulting root hash with the published root

```java
boolean verifyProof(String userId, long balance, List<String> proof, String rootHash) {
    String currentHash = sha256(userId + ":" + balance);
    for (String p : proof) {
        String[] parts = p.split(":", 2);
        String sibling = parts[1];
        if (parts[0].equals("L")) {
            currentHash = sha256(currentHash + sibling);
        } else {
            currentHash = sha256(sibling + currentHash);
        }
    }
    return currentHash.equals(rootHash);
}
```

**Interviewer:** What about privacy? Can users see each other's balances?

**Candidate:** With the basic design, the proof only contains sibling hashes, not sibling balances. Since SHA-256 is preimage-resistant, a user cannot reverse the sibling hash to find the sibling's balance. The privacy guarantee is: each user learns their own balance and that it's included in the total.

However, the **total balance** is revealed at the root. The exchange publishes `root.totalBalance` which is the sum of all user deposits.

**Interviewer:** What if an exchange tries to fake the total by including fake users?

**Candidate:** This is the critical limitation of Basic PoR. The exchange could add fake user leaves with non-zero balances to inflate the total. Solutions:
1. **Auditor**: A trusted third party (auditor) randomly samples users and asks for their proofs
2. **Distributed verification**: Users independently verify their inclusion. If a significant portion of users verify and don't report issues, the tree is likely valid
3. **Nullifier set**: Publish the set of all user IDs (hashed). Anyone can verify that the set of leaves matches the published user set
4. **ZKP (zk-SNARKs)**: Generate a zero-knowledge proof that the total balance in the tree equals a public number AND all leaves correspond to real users

---

### Part 3: Follow-up (10 min)

**Interviewer:** How does Bitcoin validate transactions using Merkle trees? Describe the SPV flow.

**Candidate:**
1. Block header contains Merkle root of all transactions (8000+ transactions)
2. Light client (SPV) stores block headers only (80 bytes each)
3. To verify a transaction:
   - SPV requests Merkle proof from a full node
   - Full node returns 13 sibling hashes (for 8000 transactions, log₂8000 ≈ 13)
   - SPV hashes up to compute the root
   - Compares with the block header's Merkle root
   - If matches, transaction is confirmed in that block
4. Also checks: proof that the block is part of the longest chain (via block headers)

**Interviewer:** Compare Bitcoin's Merkle tree with Ethereum's Merkle Patricia Trie.

**Candidate:**
- Bitcoin uses a standard binary Merkle tree
- Ethereum uses a Merkle Patricia Trie (MPT) — a hybrid of Patricia trie and Merkle tree
- MPT supports key-value proof: prove the value at a specific key (e.g., account balance for address)
- MPT is a trie: keys are hex-encoded, each node is a hash of its children
- MPT is more space-efficient for sparse state (most accounts are empty)
- MPT supports proof of non-existence: prove a key doesn't exist
- Standard Merkle tree can't prove non-existence without revealing all leaves

---

### Part 4: System Design (5 min)

**Interviewer:** Design a certificate transparency log using Merkle trees.

**Candidate:**
1. **Append-only log**: Certificate authorities submit certificates for inclusion
2. **Merkle tree**: Build over all certificates in order
3. **Signed tree head (STH)**: Periodically, the log signs and publishes the current Merkle root
4. **Inclusion proof**: Anyone can prove a specific certificate is in the log
5. **Consistency proof**: Prove that the current STH is an append-only extension of a previous STH
6. **Monitor**: Domain owners watch for unauthorised certificates
7. **Auditor**: Anyone verifies the log is append-only

**Consistency proof**: Given old root R₁ at time t₁, new root R₂ at time t₂, the log provides a proof that all certificates in R₁ are also in R₂. This prevents the log from rewriting history.

---

## Debrief

### What Went Well
- Correct Merkle tree build and proof generation
- Proof-of-reserves design with privacy analysis
- SPV flow explanation was accurate
- Certificate transparency discussion showed depth

### Areas for Growth
- Could discuss zk-Merkle trees for enhanced privacy
- Total balance computation could be more thorough

### Score
| Category | Score (1-5) |
|----------|-------------|
| Merkle Tree Knowledge | 5 |
| Cryptographic Understanding | 5 |
| System Design | 5 |
| Proof-of-Reserves Design | 4 |
| Blockchain Knowledge | 5 |
| **Overall** | **4.8 / 5** |