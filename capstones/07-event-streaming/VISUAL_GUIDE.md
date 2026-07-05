# Visual Guide: Event Streaming

## Architecture Overview
```
[Producer A]     [Producer B]     [Producer C]
    |                 |                 |
    +--------+--------+--------+--------+
             |        |        |
        [Topic "orders" - Partition 0] [Topic "orders" - Partition 1]
             |        |        |
    +--------+--------+--------+
    |        |        |
[Broker 1] [Broker 2] [Broker 3]
(Leader P0) (Leader P1) (Replica P0)
(Replica P1) (Replica P0) (Replica P1)
    |        |        |
    +--------+--------+
             |
[Consumer Group: order-processors]
    |        |        |
[Consumer 1] [Consumer 2] [Consumer 3]
(reads P0)   (reads P1)   (idle)
```

## Log Segment Structure
```
Partition Directory (topic-0/)
    +-- 00000000000000000000.log      (segment data: record batch -> size, CRC, magic, attributes, records...)
    +-- 00000000000000000000.index    (offset -> position mapping: [4KB offset, 4KB position])
    +-- 00000000000000000000.timeindex (offset -> timestamp mapping)
    +-- 00000000000000001024.log      (next segment, starts at offset 1024)
    +-- 00000000000000001024.index
    +-- ...
```

## Replication Flow
```
Leader:        Write P0-Offset 42
                |
    +-----------+-----------+
    |           |           |
Follower 1: Fetch(from=41)  Follower 2: Fetch(from=41)
    | (writes P0-Offset 42)     | (writes P0-Offset 42)
    | (ACKs to leader)         | (ACKs to leader)
    +-----------+-----------+
                |
Leader: (ISR = [Leader, F1, F2]) -> Respond to producer
```
