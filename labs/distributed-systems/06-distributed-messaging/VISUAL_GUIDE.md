# Distributed Messaging: Visual Guide

## Kafka Architecture

```
Producer 1 ──┐
              │
Producer 2 ──┼──▶ Topic A ──┬── Partition 0 ──▶ Consumer Group 1
              │              │                   (Consumer A, B)
Producer 3 ──┘              │
                             ├── Partition 1 ──▶ Consumer Group 2
                             │                   (Consumer C, D)
                             └── Partition 2 ──▶ Consumer Group 3
                                                  (Consumer E)
```

## RabbitMQ Exchange Routing

```
Producer ──▶ Topic Exchange ──┬── "user.*" ──▶ Queue 1 ──▶ Consumer A
                              │
                              ├── "order.*" ──▶ Queue 2 ──▶ Consumer B
                              │
                              └── "#.created" ─▶ Queue 3 ──▶ Consumer C
```
