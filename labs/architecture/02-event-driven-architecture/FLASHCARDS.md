# Event-Driven Architecture Flashcards

## Q: What is an event?
**A:** An immutable record of something that happened in the system at a point in time.

## Q: What is pub-sub pattern?
**A:** Publish-subscribe where events are published to topics and multiple consumers can subscribe independently.

## Q: What is event sourcing?
**A:** Storing events as the primary source of truth; current state is derived by replaying events.

## Q: What is a dead letter queue?
**A:** A queue for messages that failed processing after retries, allowing later inspection and reprocessing.

## Q: What is exactly-once semantics?
**A:** Guaranteeing that each event is processed exactly once even in failure scenarios.

## Q: What is idempotent processing?
**A:** Processing that produces the same result even if the same event is received multiple times.

## Q: What is event schema evolution?
**A:** The ability to change event structures over time while maintaining backward compatibility.

## Q: What is an upcaster?
**A:** A component that transforms old event versions to current schema during event replay.

## Q: What is consumer lag?
**A:** The difference between the latest event in a topic and the last event consumed by a consumer group.

## Q: What is a compaction topic?
**A:** A topic that retains only the latest message for each key, useful for state reconstruction.
