# Messaging - MENTAL_MODELS

## Mental Model 1: The Postal Service
- **Message = Letter**: Has sender, recipient, content
- **Queue = Mailbox**: Letters wait until picked up
- **Topic = Mailing list**: One message goes to all subscribers
- **Broker = Post Office**: Routes, stores, delivers messages
- **Consumer = Recipient**: Picks up and processes mail

## Mental Model 2: The Factory Assembly Line
- **Producer = Worker A**: Creates parts and puts them on belt
- **Topic = Conveyor belt**: Carries parts between stations
- **Partition = Lane**: Different belts for different product types
- **Consumer Group = Team of workers**: Each takes parts from their lane
- **Offset = Position**: Tracks how far along the belt each worker has processed

## Mental Model 3: The Radio Station
- **Producer = Radio DJ**: Broadcasts content on frequency
- **Topic = Radio frequency**: Anyone tuned in receives it
- **Consumer = Listener**: Tuned to the frequency
- **Consumer Group = Family**: Different members can listen to different shows
- **Replay = Podcast**: Listen again from the beginning

## Messaging Patterns

| Pattern | Description | Analogy |
|---------|-------------|---------|
| Point-to-Point | One message → one consumer | Private letter |
| Pub/Sub | One message → many consumers | Radio broadcast |
| Competing Consumers | Multiple consumers take from same queue | Ticket counter |
| Dead Letter Queue | Failed messages stored separately | Post-office return |
| Request-Reply | Send message, expect response | Question-answer |
| Routing | Different message types → different queues | Mail sorter |

## Delivery Semantics

```
AT_MOST_ONCE                            AT_LEAST_ONCE                          EXACTLY_ONCE
   │                                        │                                      │
   │ Fire & forget                         │ Retry until ack                      │ Idempotency +
   │ May lose messages                     │ May duplicate messages               │ Transactional
   │ Fastest                               │ Reliable                             │ Guaranteed
   │ Metrics, logs                         │ Payments, orders                     │ Financial txns
```

## Kafka Consumer Group Model

```
Topic: orders (4 partitions)

Consumer Group: order-processors
├── Consumer 1 ─── Partition 0, 1
├── Consumer 2 ─── Partition 2, 3
└── Consumer 3 ─── Idle (not enough partitions)

Consumer Group: order-analytics
├── Consumer A ─── Partition 0, 1, 2
└── Consumer B ─── Partition 3
```
