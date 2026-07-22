# Interview Questions: Merkle Tree (Hash Tree)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Oracle, Amazon, Microsoft, Meta | Blockchain / distributed systems |

## NeetCode Reference
Not in NeetCode. Merkle trees are essential for blockchain, distributed databases, and Git.

## Company-Specific Questions

### Google
- Explain how Merkle trees enable efficient data synchronization in distributed systems (e.g., Cassandra, DynamoDB)
- How does the proof of inclusion work? Given a Merkle root, how do you prove that a specific element is in the tree?
- Design a system to detect data inconsistency between two replicas using Merkle trees (Merkle tree reconciliation)
- How does Certificate Transparency use Merkle trees for public audit logs?

### Microsoft
- How does Git use Merkle trees to identify file and commit versions? Explain the tree → commit chain
- Implement a Merkle tree with insert and proof generation — what is the time and space complexity?
- How would you detect tampering of a log file using a Merkle tree (audit log integrity)?

### Meta
- Design a system for deduplication of files in a distributed storage system using Merkle trees
- How would you quickly find which files differ between two directory snapshots using Merkle trees?
- Explain how Merkle trees achieve O(log n) proof size for proving inclusion of an element

### Amazon
- How does DynamoDB use Merkle trees for anti-entropy (repairing replicas)?
- Design a data integrity verification system for S3 using Merkle trees (tree of hashes for multipart uploads)
- Compare Merkle trees vs Hash Lists vs Bloom filters for data integrity verification

### Apple
- How does iCloud drive use Merkle trees for file synchronization?
- Design a secure software update verification system using Merkle trees
- How would you implement a tamper-proof event log using a Merkle tree?

### Oracle
- How does the blockchain use Merkle trees for transaction verification? Explain SPV (Simplified Payment Verification)
- What is a Merkle Patricia Trie and how does Ethereum use it for state storage?
- Compare Merkle trees vs B-trees with hash pointers for database indexing
- How would you implement a Merkle tree for auditing SQL query logs?

## Real Production Scenarios

- **Scenario 1: Git Version Control** — Git uses Merkle trees for its object storage. Each commit points to a tree object (Merkle tree of filenames → blob hashes). The commit hash identifies the entire state. If any file changes, the tree hash changes, which changes the commit hash. This enables Git to detect content changes with O(1) comparison of commit hashes.

- **Scenario 2: Database Anti-Entropy** — Cassandra uses Merkle trees for anti-entropy repair. Each replica builds a Merkle tree of its data partition range. Replicas exchange Merkle roots. If roots differ, they recursively compare children to find the exact keys that are out of sync, then repair only those keys.

- **Scenario 3: Blockchain SPV** — A lightweight Bitcoin client (SPV wallet) downloads block headers (80 bytes each) instead of full transactions. To verify that a transaction is included in a block, the server provides a Merkle proof: the transaction hash + sibling hashes along the path to the root. The client verifies the proof against the block header's Merkle root.

## Interview Tips

- Time: O(n) for building, O(log n) for generating inclusion proof, O(log n) for verifying proof
- Space: O(n) for full tree, O(log n) for proof size
- Common edge cases: empty tree (undefined root — handle with null/zero hash), single element (root = leaf hash), odd leaf count (duplicate last leaf or use sibling)
- Leaves are typically hashes of data blocks; internal nodes are hashes of concatenated child hashes
- Proof of inclusion: for each level, provide the sibling hash. The verifier recomputes the Merkle root by hashing the target + sibling at each level
- The tree is balanced for efficient proof size (binary tree → proof of length log₂(n))
- Sorted Merkle tree: leaves sorted lexicographically enables non-membership proofs (show adjacent leaves)
- Tamper resistance: changing any data element changes the root hash; without the original root, tampering is detectable

## Java-Specific Considerations

- No standard Merkle tree class in Java — implement from scratch
- `MessageDigest md = MessageDigest.getInstance("SHA-256")` — compute hashes for tree nodes
- Tree node: `class MerkleNode { byte[] hash; MerkleNode left, right; }` — or use flat array for compact representation
- Flat array: `byte[][] tree = new byte[2 * n][32]` — leaves at positions [n, 2n-1], internal at [1, n-1]
- `ByteArrayOutputStream` / `ByteBuffer` for concatenating child hashes before hashing
- `java.security.MessageDigest` is not thread-safe — use `ThreadLocal<MessageDigest>` for parallel tree building
- `Arrays.equals()` to compare hashes; `Arrays.hashCode()` for hash map lookup of roots
- For large data: hash chunks incrementally with `digest.update()` instead of loading entire file into memory
- `HexFormat.of().formatHex(hash)` (Java 17+) for hex encoding hash strings
- `BigInteger` for converting hash bytes to numeric representation (if needed for comparison)
- Verify proof: iterate through siblings, compute hash_up = SHA256(hash_down || sibling) or SHA256(sibling || hash_down) depending on order
