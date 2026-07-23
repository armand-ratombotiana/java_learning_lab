# Lab 11 — SSL/TLS Certificate: LeetCode Connections

**Q1: Encode and Decode TinyURL (LeetCode 535)** — URL encoding/decoding is used in CSR (Certificate Signing Request) encoding.

**Q2: Validate Binary Search Tree (LeetCode 98)** — Certificate chain validation is like BST validation — each certificate must be signed by its parent, forming a trust chain from leaf to root CA.

**Q3: Implement Trie (LeetCode 208)** — DNS resolution follows a trie-like structure — root → TLD → domain → subdomain. Certificate validation involves DNS name matching.

**Q4: Serialize and Deserialize Binary Tree (LeetCode 297)** — Certificate encoding/decoding (DER, PEM formats) is serialization/deserialization.

**Q5: Design Consistent Hashing (LeetCode — system design)** — Certificate distribution across CDN edge nodes uses consistent hashing.
