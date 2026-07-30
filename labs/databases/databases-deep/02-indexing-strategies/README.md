# 02 - Indexing Strategies

## Topics Covered
- B-Tree indexes (balanced tree structure, leaf pages)
- Hash indexes (equality-only, collision chains)
- Bitmap indexes (low-cardinality columns)
- GiST, GIN, SP-GiST indexes
- Partial indexes (WHERE predicates)
- Composite indexes (column order matters)
- Covering indexes (INCLUDE columns)
- Index-only scans, Visibility Map

## Goal
Design optimal index strategies for mixed analytical and transactional workloads.

## Exercises

1. Create a B-tree, hash, and GiST index on the same column and compare plan choices.
2. Build a composite index on (a, b) vs (b, a) and show when each is used.
3. Use a partial index to accelerate queries filtering on a rare status value.
4. Create a covering index with INCLUDE to eliminate heap fetches.