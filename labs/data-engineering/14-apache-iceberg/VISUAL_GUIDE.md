# Visual Guide to Apache Iceberg

```
Iceberg Metadata Hierarchy:
[Catalog] -> [Table Metadata JSON]
                  |
           [Snapshot List]
                  |
           [Manifest List]
                  |
           [Manifest Files]
           /    |      \\
[File.parquet] [File.parquet] [File.parquet]

Partition Evolution:
Spec v1: PARTITION BY hour(event_ts)
Spec v2: PARTITION BY day(event_ts)  -- changed without rewrite

Old files stay partitioned by hour
New files partitioned by day
Both readable in same query

Time Travel:
Snapshot 1 @ t1 ---> Snapshot 2 @ t2 ---> Snapshot 3 @ t3
                  \\                     /
                   v                    v
             Read at t1.5 = Snapshot 1
             Read at t2.5 = Snapshot 2
```
