# Event Sourcing Flashcards

## Q: What is Event Sourcing?
**A:** Storing state changes as a sequence of events; current state is derived by replaying events.

## Q: What is an event?
**A:** An immutable record of something that happened in the system.

## Q: What is a snapshot?
**A:** A saved aggregate state at a specific version to optimize replay.

## Q: What is upcasting?
**A:** Transforming old event versions to current schema during event replay.

## Q: What is temporal query?
**A:** Querying the state of an aggregate at a specific point in time.

## Q: What is event replay?
**A:** Re-processing all events to rebuild current state.

## Q: What is an event store?
**A:** A database specialized for storing and retrieving event streams.

## Q: What is optimistic concurrency in event store?
**A:** Preventing conflicting writes by checking event version on append.

## Q: What is a projection?
**A:** A read model built from events, used for querying.

## Q: What is event serialization?
**A:** Converting events to/from a storable format (JSON, Avro, Protobuf).
