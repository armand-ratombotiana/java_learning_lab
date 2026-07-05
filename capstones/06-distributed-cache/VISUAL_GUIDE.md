# Visual Guide: Distributed Cache

## Consistent Hashing Ring
```
            Node A (hash: 0)
                |
    Node E      |     Node B
    (hash: 300) |     (hash: 100)
         \      |      /
          \     |     /
           \    |    /
            \   |   /
             \  |  /
              \ | /
    Node D --- Node C
    (hash: 250)  (hash: 150)

    Key route: hash(key) = 120 -> go clockwise -> Node B
```

## Cluster Architecture
```
[Client] <--> [Client Library] <--> [Node A (Primary)]
                                          |
                     +--------------------+-------------------+
                     |                    |                   |
              [Node B (Replica)]    [Node C (Replica)]  [Node D (Standby)]
                     |                    |
           [Gossip] ---------> [Gossip] -----> [Gossip]
```

## Gossip Protocol Flow
```
Node A -> Node B: "Here's my heartbeat counter (42), who's alive?"
Node B -> Node A: "I'm alive (heartbeat 55), A is alive (42), C is alive (38), D is SUSPECT (heartbeat 15, last seen 6s ago)"
Node A -> Node C: (gossip to another random peer)
```
