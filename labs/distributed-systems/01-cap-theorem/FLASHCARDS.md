# CAP Theorem: Flashcards

## Front: What is the CAP Theorem?
**Back**: No distributed system can simultaneously provide Consistency, Availability, and Partition Tolerance.

## Front: What is Consistency?
**Back**: Every read receives the most recent write or an error.

## Front: What is Availability?
**Back**: Every request receives a non-error response.

## Front: What is Partition Tolerance?
**Back**: System continues operating despite network failures.

## Front: Who proposed CAP?
**Back**: Eric Brewer in 2000.

## Front: Who proved CAP?
**Back**: Seth Gilbert and Nancy Lynch in 2002.

## Front: What does PACELC add?
**Back**: Latency vs Consistency tradeoff during normal (non-partition) operation.

## Front: How is quorum calculated?
**Back**: Q = N/2 + 1 (majority of nodes).

## Front: Example of a CP database?
**Back**: MongoDB, HBase, ZooKeeper.

## Front: Example of an AP database?
**Back**: Cassandra, DynamoDB, CouchDB.
