# Lab 06: Bloom Filters Applied

## Overview
Master probabilistic data structures for system design: Bloom filters, counting Bloom filters, scalable Bloom filters, Cuckoo filters, and real-world applications.

## Data Structures

| Structure | Space per Element | False Positives | Deletes | Use Case |
|-----------|------------------|-----------------|---------|----------|
| **Bloom Filter** | ~10 bits | Yes (configurable) | No | Membership test |
| **Counting Bloom** | ~4x Bloom | Yes | Yes | Deletable membership |
| **Scalable Bloom** | Dynamic | Yes (bounded) | No | Growing data sets |
| **Cuckoo Filter** | ~6 bits | Yes (lower than Bloom) | Yes | Low false positive rate |

## Learning Objectives
- Implement a standard Bloom filter with configurable false positive rate
- Build a counting Bloom filter with delete support
- Design a scalable Bloom filter that adapts to data volume
- Implement a Cuckoo filter with higher space efficiency
- Apply Bloom filters to system design problems (caching, spam detection, deduplication)

## Applications in System Design
- **Cassandra/DynamoDB**: Bloom filters for SSTable lookups
- **Medium**: Recommendation deduplication
- **Google Bigtable**: Row key membership tests
- **Bitcoin SPV**: Lightweight transaction verification
- **CDN**: Cache key lookup acceleration
