# Visual Guide: PostgreSQL

## Query Processing Pipeline
```
SQL Query
    │
    ▼
┌─────────────┐
│   Parser    │  → Tokenize, build parse tree
└──────┬──────┘
       ▼
┌─────────────┐
│  Analyzer   │  → Semantic analysis, type resolution
└──────┬──────┘
       ▼
┌─────────────┐
│  Rewriter   │  → Rule-based transformation (views, rules)
└──────┬──────┘
       ▼
┌─────────────┐
│  Planner    │  → Generate join orders, scan paths
└──────┬──────┘
       ▼
┌─────────────┐
│ Optimizer   │  → Cost-based optimization (CBO)
└──────┬──────┘
       ▼
┌─────────────┐
│  Executor   │  → Iterate plan nodes, fetch rows
└──────┬──────┘
       ▼
    Results
```

## B-Tree Index Structure
```
          [Root: 50, 100, 150]
           /     |      |     \
    [10,30,40] [60,80] [120] [200,250]
     /    \     /    \    |     /    \
   Leaf  Leaf  Leaf Leaf Leaf Leaf  Leaf
```

## MVCC Tuple Versions
```
INSERT:  Tuple1 (xmin=100, xmax=0)    → visible to tx 100+
UPDATE:  Tuple1 (xmin=100, xmax=200)   → dead (committed)
         Tuple2 (xmin=200, xmax=0)     → visible to tx 200+
```

## Connection Pooling (HikariCP + PostgreSQL)
```
App Thread ──┐
              ├── HikariCP Pool ──┼── PostgreSQL
App Thread ──┘    (min=5, max=20) │
                                  │
                            ┌─────┴─────┐
                            │ Backend    │
                            │ Process    │
                            └───────────┘
```

## JSONB Document Store
```
┌───────────────────────┐
│  Table: events        │
├───────────────────────┤
│ id  │ data (JSONB)    │
│─────┼─────────────────│
│ 1   │ {"user":"alice"} │
│ 2   │ {"user":"bob"}   │
└─────┴─────────────────┘
    │
    ▼
┌──────────────────────────────┐
│ GIN Index on data            │
│ → {"user":"alice"} tokenized │
│ → Fast key-existence lookups │
└──────────────────────────────┘
```
