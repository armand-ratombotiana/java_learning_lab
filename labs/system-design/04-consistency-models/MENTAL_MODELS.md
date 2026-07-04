# Consistency Models - MENTAL MODELS

## Mental Model 1: The Grade Book
- **Strong consistency**: Teacher updates grade → everyone sees the same grade immediately
- **Eventual consistency**: Teacher updates grade → students see old grade for a while, then it updates
- **Causal consistency**: If teacher corrects test and then enters grade, you see correction before grade
- **Read-after-write**: You submit homework → refresh → you see your submission (others may not)

## Mental Model 2: The Bulletin Board
- **Strong**: Any message posted is instantly visible to everyone checking any copy of the board
- **Eventual**: Message takes time to propagate to all bulletin boards. Someone may not see it yet
- **Quorum**: Must check 3 out of 5 bulletin boards; if 2/3 agree, that's the truth

## Mental Model 3: The Shared Whiteboard
- **Linearizable**: Every marker stroke appears in same order for all viewers
- **Causal**: If you draw a circle and someone draws a dot inside, the dot always appears after the circle
- **Eventual**: Both strokes appear eventually, possibly in different orders for different viewers

## Consistency Spectrum

```
Weakest ◄──────────────────────────────────────────────► Strongest

Eventual    Causal    Read-Your-   Monotonic    Sequential    Linearizable
Consistency          Writes        Reads        Consistency   (Strict)

    ▲                    ▲                              ▲
 Social feeds        Shopping cart                  Bank transfers
 DNS                 Comments                       Stock trading
 Analytics           Session reads                  Distributed locks
```

## CAP Triangle

```
               Consistency
                    ▲
                    │
                    │
          CP ───────┼─────── CA
          (HBase,   │       (RDBMS single-site)
          ZooKeeper)│
                    │
                    ├──────► Availability
                    │
          AP ───────┘
          (Cassandra,
           DynamoDB)
```

Pick 2 of 3, but P is mandatory in distributed systems. Real choice is CP vs AP.
