# Interview Questions: Merkle Tree

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a Merkle tree with build, rootHash, and verify methods.

**Answer:**
See GUIDE.md for full implementation. Key points:
- Build: bottom-up, hash pairs of child nodes
- Odd leaves: duplicate last leaf
- Verify: build another tree, compare root hashes
- Hash function: SHA-256 minimum

---

### Question 2
> How does Bitcoin use Merkle trees for Simplified Payment Verification (SPV)?

**Answer:**
Bitcoin block header contains the Merkle root of all transactions in the block (up to 2000+ transactions). A light client (SPV) only stores block headers (80 bytes each). To verify a transaction is included:
1. SPV node requests a Merkle proof from a full node
2. Full node returns O(log n) sibling hashes (~11 hashes for 2000 txs = ~352 bytes)
3. SPV node computes the root by hashing along the proof path
4. Compares computed root with the block header's Merkle root
5. If they match, the transaction is confirmed

This allows light clients to verify payments without storing the full blockchain.

---

### Question 3
> Explain how Git uses Merkle trees.

**Answer:**
Git is a content-addressable filesystem using a Merkle tree of objects:
- **blob**: hash of file content
- **tree**: hash of (blob hashes + subtrees) for a directory
- **commit**: hash of (tree hash + parent commit hash + author + message)
- **tag**: hash of commit + annotation

When you change any file, every tree hash from root to leaf changes. `git diff` compares tree hashes. Each commit is a snapshot that can be verified as a Merkle proof.

---

### Question 4
> Compare Merkle trees and Bloom filters. When would you use each?

**Answer:**
| Feature | Merkle Tree | Bloom Filter |
|---------|------------|-------------|
| Purpose | Prove data inclusion | Membership test |
| Space | O(n) storage | O(m) bits |
| Proof size | O(log n) hashes | O(k) hash bits |
| False positives | Impossible | Configurable |
| Verification | Deterministic | Probabilistic |
| Privacy | Contains no data | Bits only |

**Use Merkle** for integrity verification (blockchain, file sync).
**Use Bloom** for membership with memory constraints (crawlers, safe browsing).

---

### Question 5
> Design an anti-entropy (repair) system for a distributed database using Merkle trees.

**Answer:**
Each node builds a Merkle tree over its key range:
1. Split key range into blocks (e.g., 100 keys per leaf)
2. Build Merkle tree of block hashes
3. On sync, compare Merkle roots
4. If roots differ, compare child hashes recursively
5. When a differing leaf is found, request only that block's data
6. Repair the differing data

**Efficiency**: If only 1 of 1000 blocks differs, we transfer 1 block + O(log n) Merkle proof instead of all blocks.

---

### Question 6
> How does Certificate Transparency use Merkle trees?

**Answer:**
Certificate Transparency (CT) maintains an append-only Merkle tree of all issued SSL certificates:
- **Submission**: CA submits certificate → tree appends it
- **Inclusion proof**: Anyone can prove a certificate is in the log
- **Consistency proof**: Prove that version V+1 of the log is V + new certificates (append-only)
- **Monitoring**: Domain owners check if unauthorised certificates were issued for their domain
- **Auditing**: Anyone verifies the log is behaving correctly (append-only property)

CT uses Merkle Mountain Range (MMR) for efficient append operations.

---

### Question 7
> Implement a function to verify a Merkle proof given a data block, proof, and root hash.

**Answer:**

```java
boolean verifyProof(String data, List<String> proof, String rootHash) {
    String hash = sha256(data);
    for (String p : proof) {
        String[] parts = p.split(":", 2);
        String sibling = parts[1];
        if (parts[0].equals("L")) {
            hash = sha256(hash + sibling);  // current is left
        } else {
            hash = sha256(sibling + hash);  // current is right
        }
    }
    return hash.equals(rootHash);
}

String sha256(String s) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(s.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
}
```

---

### Question 8
> How does Ethereum differ from Bitcoin in its Merkle tree usage?

**Answer:**
Ethereum uses **Merkle Patricia Trie** (MPT), not a simple Merkle tree:
- **3 trees per block**: State tree, transaction tree, receipt tree
- **State tree**: Key-value pairs (address → account state). Patricia trie enables efficient key lookup and update
- **Account proofs**: Prove account balance, nonce, code hash, storage root
- **Storage tree**: Per-contract key-value storage (also MPT)
- **Merkle proof**: Proof that a specific address has a specific balance at a specific block

---

### Question 9
> Design a file integrity monitoring system using Merkle trees.

**Answer:**
1. **Baseline**: Build Merkle tree over all monitored files (file → hash leaf → directory tree → root)
2. **Store**: Persist the Merkle root (signed) in secure storage
3. **Monitor**: Periodically rebuild Merkle tree, compare with stored root
4. **Report**: If root differs, use proof to identify which file(s) changed
5. **Alert**: If any leaf hash differs, the file was modified
6. **Rootkit detection**: Filesystem driver can intercept reads and verify against Merkle root

---

### Question 10
> What is a Merkle proof of consistency? How is it different from a proof of inclusion?

**Answer:**
- **Proof of inclusion**: Prove leaf L is in the tree with root R (standard Merkle proof)
- **Proof of consistency**: Prove that a tree with root R₂ is a superset of a tree with root R₁ (append-only property)

**Consistency proof**: Given R₁ (old root) and R₂ (new root), reveal the subset of R₂'s tree that corresponds to R₁. If the old subtree hash matches R₁, the tree must be a proper extension.

Used in Certificate Transparency to prove the log is append-only.

---

### Question 11
> Compare Merkle tree with hash list for data verification.

**Answer:**
| Feature | Hash List | Merkle Tree |
|---------|-----------|------------|
| Proof size | O(n) | O(log n) |
| Verify single block | Need all hashes | O(log n) hashes |
| Parallel verification | No | Yes |
| Efficient for large data | No | Yes |

**Hash list** is simpler and sufficient for small n (< 100 blocks). **Merkle tree** scales to millions of blocks.

---

### Question 12
> How does IPFS (InterPlanetary File System) use Merkle trees?

**Answer:**
IPFS uses a Merkle DAG (Directed Acyclic Graph):
- Each file is split into blocks
- Blocks are cryptographically hashed (CID — Content Identifier)
- Files are Merkle DAG nodes (links to blocks or sub-files)
- Directories are DAG nodes linking to files
- Deduplication: same content = same hash = same CID
- Verification: given root CID, verify any block

---

### Question 13
> What hash function should you use for a Merkle tree? Why not MD5 or SHA-1?

**Answer:**
- **SHA-256**: Minimum recommended (used in Bitcoin, Git)
- **SHA-512**: Higher security margin
- **BLAKE2**: Faster, used in some modern systems
- **MD5, SHA-1**: Broken (collision attacks exist). Can forge Merkle proofs.

**Collision attack on Merkle tree**: If you can find two data blocks with the same hash, you can swap them without changing the root. This breaks integrity.

---

### Question 14
> Implement an append-only Merkle tree (Merkle Mountain Range).

**Answer:**
```java
class MMR {
    List<String> peaks = new ArrayList<>();
    List<String> allHashes = new ArrayList<>();

    void append(String data) {
        String hash = sha256(data);
        allHashes.add(hash);
        int count = allHashes.size();

        // Merge peaks: while last two peaks are same size, combine
        while (peaks.size() >= 2 && hasSameSize(peaks.get(peaks.size()-2), peaks.get(peaks.size()-1))) {
            String right = peaks.remove(peaks.size() - 1);
            String left = peaks.remove(peaks.size() - 1);
            peaks.add(sha256(left + right));
        }

        peaks.add(hash);
    }

    String root() {
        // Combine all peaks into one root (hash left to right)
        if (peaks.isEmpty()) return "";
        String r = peaks.get(0);
        for (int i = 1; i < peaks.size(); i++)
            r = sha256(r + peaks.get(i));
        return r;
    }
}
```

---

### Question 15
> Design a proof-of-reserves system using Merkle trees for a cryptocurrency exchange.

**Answer:**
1. Exchange builds a Merkle tree where each leaf = (userID, balance)
2. Exchange signs the Merkle root and publishes it
3. Each user receives a Merkle proof that their balance is included
4. User verifies: their balance + proof → hash = published root
5. Total reserve = sum of all leaf balances (verifiable by aggregating Merkle paths)
6. Auditor sums all leaf balances → total reserve
7. If total reserve ≥ total user deposits, exchange is solvent

**Privacy**: User only sees their own leaf, not others'. The Merkle root commits to all balances without revealing them.

---

### Question 16
> How does ZFS / Btrfs use Merkle trees for data integrity?

**Answer:**
These filesystems use Merkle trees (called **checksum trees**) for all data and metadata:
- Each data block has a checksum (SHA-256)
- Each tree/directory node has a checksum of its children
- Root of Merkle tree = filesystem state
- On read, verify checksum from root down to data
- **Self-healing**: On RAID, if checksum mismatch, reconstruct from parity or mirror

---

### Question 17
> What is a Sparse Merkle Tree? When would you use it?

**Answer:**
A Sparse Merkle Tree represents a key-value store where most keys are empty (don't exist). The tree has a fixed depth (e.g., 256 for 256-bit keys). Non-existent keys follow a path of "default" hashes (precomputed hash of empty subtree).

**Advantages**:
- Proof of non-existence: prove a key doesn't exist
- Space: only store non-empty subtrees
- Same proof size for existing and non-existing keys

**Use case**: Ethereum state tree (billions of account slots, but most are empty).