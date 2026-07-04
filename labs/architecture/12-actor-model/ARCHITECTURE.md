# Actor Model Architecture Reference

## System Architecture
```
+-----------------------------------------------------+
|                  Actor System                         |
|                                                      |
|  +------------------------------------------------+ |
|  |           User Guardian                         | |
|  |    +-------+  +-------+  +-------+              | |
|  |    | Actor |  | Actor |  | Actor |  ...         | |
|  |    | A     |  | B     |  | C     |              | |
|  |    +-------+  +-------+  +-------+              | |
|  |        |           |         |                   | |
|  |    +-------+  +-------+  +-------+              | |
|  |    | Child |  | Child |  | Child |              | |
|  |    | A1    |  | B1    |  | C1    |              | |
|  |    +-------+  +-------+  +-------+              | |
|  +------------------------------------------------+ |
|                                                      |
|  +------------------------------------------------+ |
|  |           Infrastructure Actors                  | |
|  |    +----------+  +----------+  +----------+      | |
|  |    | Cluster  |  | Pub-Sub  |  | Event    |      | |
|  |    +----------+  +----------+  | Stream   |      | |
|  |                               +----------+      | |
|  +------------------------------------------------+ |
+-----------------------------------------------------+
```

## Actor Lifecycle
```
STARTED -> RECEIVING_MESSAGES -> STOPPED
    |            |
    v            v
 (failure)   (PostStop cleanup)
    |
    v
TERMINATED
```

## Supervision Directives
| Directive | Behavior |
|-----------|----------|
| Resume | Continue processing next message |
| Restart | Create new actor instance |
| Stop | Terminate the actor permanently |
| Escalate | Pass decision to parent supervisor |

## Message Delivery Guarantees
| Guarantee | Description |
|-----------|-------------|
| At-most-once | Fastest, may lose messages |
| At-least-once | No loss, may duplicate |
| Exactly-once | Hardest, reliable and idempotent |

## Deployment Configuration
```yaml
akka:
  actor:
    provider: cluster
  remote:
    artery:
      canonical:
        hostname: ${HOSTNAME}
        port: 2552
  cluster:
    seed-nodes:
      - "akka://system@host1:2552"
      - "akka://system@host2:2552"
```
