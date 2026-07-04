# Visual Guide: MongoDB

## Document vs Relational Model
```
Relational:                      MongoDB:
┌───── users ─────┐              ┌───── users ───────────────┐
│ id │ name       │              │ {                          │
├────┼────────────┤              │   _id: ObjectId,           │
│ 1  │ Alice      │              │   name: "Alice",           │
├────┴────────────┤              │   addresses: [             │
│                 │              │     { city: "NYC" },       │
│ ┌─── orders ──┐ │              │     { city: "LA"  }       │
│ │ id│user│amt │ │              │   ]                        │
│ └───┴────┴────┘ │              │ }                          │
└─────────────────┘              └────────────────────────────┘
```

## Replica Set Architecture
```
┌─────────────────────────────────────┐
│         Replica Set "rs0"           │
├─────────────────────────────────────┤
│  ┌────────────────────────────┐     │
│  │        PRIMARY              │     │
│  │  (writes + reads optional)  │     │
│  └───────────┬────────────────┘     │
│              │ oplog sync            │
│     ┌────────┼────────┐              │
│     ▼        ▼        ▼              │
│ ┌──────┐ ┌──────┐ ┌──────┐          │
│ │SECOND│ │SECOND│ │ARBITER│          │
│ │  ARY │ │  ARY │ │(vote │          │
│ │ read │ │read  │ │ only)│          │
│ └──────┘ └──────┘ └──────┘          │
└─────────────────────────────────────┘
```

## Aggregation Pipeline
```
db.orders.aggregate([
  { $match:   { status: "completed" } },
  { $group:   { _id: "$customer", total: { $sum: "$amount" } } },
  { $sort:    { total: -1 } },
  { $limit:   10 },
  { $project: { customer: "$_id", total: 1, _id: 0 } }
])
```
Visual flow:
```
Orders → [Match] → [Group] → [Sort] → [Limit] → [Project] → Results
 100K     5000       500       500       10         10          10
docs     docs      groups    groups    docs       docs        docs
```

## Sharded Cluster
```
         App
          │
    ┌─────┴─────┐
    │  mongos    │  ← Router (stateless)
    └─────┬─────┘
          │
    ┌─────┴─────┐
    │  Config    │  ← Metadata (3 nodes replica set)
    │  Servers   │
    └─────┬─────┘
          │
    ┌─────┴─────┴─────┐
    │         │         │
 ┌──┴──┐   ┌──┴──┐   ┌──┴──┐
 │Shard│   │Shard│   │Shard│
 │  A  │   │  B  │   │  C  │
 │(RS) │   │(RS) │   │(RS) │
 └─────┘   └─────┘   └─────┘
```

## Query Execution Plan
```
db.collection.find({ status: "A", age: { $gt: 25 } })
  .sort({ name: 1 })
  .hint({ status: 1, age: -1 })
  .explain("executionStats")
```
