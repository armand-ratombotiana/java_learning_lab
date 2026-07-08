# Flashcards: Microservices Migration

## Front: What is Microservices Migration?
**Back:** Microservices Migration addresses critical challenges in modern application development, focusing on scalability, reliability, and performance at scale.

## Front: What is the CAP theorem?
**Back:** A distributed system can provide only two of three guarantees: Consistency, Availability, Partition Tolerance.

## Front: What is horizontal scaling?
**Back:** Adding more machines to handle increased load (scale out), as opposed to vertical scaling (scale up).

## Front: What is a load balancer?
**Back:** Distributes incoming traffic across multiple servers to prevent any single server from being overwhelmed.

## Front: What is the difference between REST and gRPC?
**Back:** REST uses HTTP/1.1 with JSON; gRPC uses HTTP/2 with Protocol Buffers, supporting bi-directional streaming.

## Front: What is a circuit breaker?
**Back:** Prevents cascading failures by stopping requests to a failing downstream service, allowing it time to recover.

## Front: What is eventual consistency?
**Back:** If no new updates are made, eventually all reads return the last updated value. Provides higher availability.

## Front: What is a message queue?
**Back:** Enables asynchronous communication between services, providing decoupling and buffering.

## Front: What is the purpose of caching?
**Back:** Stores frequently accessed data in fast memory to reduce latency and decrease load on databases.

## Front: What is a CDN?
**Back:** Content Delivery Network that delivers content from edge locations based on user geography.

## Front: What is database sharding?
**Back:** Splits a large database into smaller shards distributed across multiple database servers.

## Front: What is the two-phase commit protocol?
**Back:** 2PC has prepare and commit phases to provide atomic distributed transactions (blocking protocol).

## Front: What is a bloom filter?
**Back:** Space-efficient probabilistic data structure with no false negatives but possible false positives.

## Front: What is sync vs async replication?
**Back:** Sync waits for all replicas (strong consistency, high latency). Async acknowledges immediately (higher perf, possible data loss).

## Front: What is a consensus algorithm?
**Back:** Allows multiple nodes to agree on a value despite failures. Examples: Paxos, Raft.

## Front: What is an API gateway?
**Back:** Single entry point handling routing, authentication, rate limiting, and protocol translation for microservices.

## Front: What is observability?
**Back:** Understanding system internal state from external outputs through logs, metrics, and traces.

## Front: What is idempotency?
**Back:** Performing an operation multiple times produces the same result as once. Critical for reliability.

## Front: Monolithic vs microservices?
**Back:** Monolith = single deployable unit. Microservices = independently deployable services communicating over network.

## Front: What is the fallacies of distributed computing?
**Back:** Eight assumptions: network reliable, latency zero, bandwidth infinite, network secure, topology static, one admin, zero transport cost, homogeneous network.
