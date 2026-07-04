# CAP Theorem: Visual Guide

## The CAP Triangle

```
        Consistency
            /\
           /  \
          /    \
         / CA   \ CP
        /        \
       /__________\
  Availability    Partition Tolerance
           \      /
            \ AP /
             \  /
              \/
```

## System Classifications

| System        | Type | Example Use Case       |
|---------------|------|------------------------|
| MongoDB       | CP   | Document store         |
| Cassandra     | AP   | Time-series data       |
| ZooKeeper     | CP   | Coordination service   |
| Redis Cluster | AP   | Session cache          |
| MySQL Cluster | CA   | Traditional RDBMS      |

## Partition Scenario Flow

```
Client → [Write to N1] → N1 updated
                          |
                    [PARTITION OCCURS]
                          |
N2 ← [Read from N2] ← stale data (AP) or error (CP)
```
