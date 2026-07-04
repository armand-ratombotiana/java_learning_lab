# Architecture: Advanced Trees in System Design

## Database Index Architecture (B+ Tree)

```
┌─────────────────────────────┐
│  Search     Insert   Delete │
│    ↓          ↓        ↓    │
│  ┌───────────────────────┐  │
│  │  B+ Tree Operations   │  │
│  │  (Split, Merge, Seek) │  │
│  └────────┬──────────────┘  │
│           ↓                 │
│  ┌───────────────────────┐  │
│  │  Buffer Pool (Pages)  │  │
│  └────────┬──────────────┘  │
│           ↓                 │
│  ┌───────────────────────┐  │
│  │  Disk I/O             │  │
│  │  (Block-aligned       │  │
│  │   pages, prefetching) │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### B+ Tree vs B-Tree

- **B+ Tree**: all data in leaves, internal nodes only keys + pointers
- **B-Tree**: data in every node
- B+ Tree better for range queries (linked leaves)
- Both used in databases; B+ Tree more common for indexes

## Java Collections Architecture

```
Map
├── TreeMap (Red-Black)
│   └── NavigableMap
│       └── SubMap views
├── HashMap (buckets + trees)
├── LinkedHashMap (list + hash)
└── ConcurrentSkipListMap (skip list, not tree)
```

## Range Query Architecture

```
Sorted Keys → TreeMap → subMap(low, high) → Range View
                                          ↓
                              Iterate over range O(k+log n)
```

## Segment Tree for Real-Time Analytics

```
Stream of events
  ↓
Segment Tree (range sum, min, max)
  ↓
  - Windowed aggregations (last 5 minutes)
  - Real-time dashboards
  - A/B test analysis
```

## B-Tree in File Systems

```
Directory Path: /home/user/docs/file.txt
                      ↓
Root Directory (B-tree of entries)
                      ↓
home directory (B-tree)
                      ↓
user directory (B-tree)
                      ↓
docs directory (B-tree)
                      ↓
file.txt (inode pointer)
```

- ext4: Htree (B-tree variant) for directory indexing
- NTFS: B+ tree for Master File Table (MFT)
- HFS+: B-tree for catalog file

## Distributed B-Trees

- **Google Spanner**: uses B-tree-like structures for distributed storage
- **Amazon DynamoDB**: B-tree-based indexes
- **MongoDB**: WiredTiger storage engine uses B-trees
- **SQLite**: B-tree per table and index

## TreeMap in the Java Ecosystem

- **Spring**: `DefaultSingletonBeanRegistry`, session attribute maps
- **Hibernate**: second-level cache regions
- **Jackson**: serialization config sorted by property order
- **Apache Kafka**: offset management, partition assignment
