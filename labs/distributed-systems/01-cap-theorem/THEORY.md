# CAP Theorem: Theory

## Core Concepts

### Consistency (C)
Every read receives the most recent write or an error. All nodes see the same data at the same time.

### Availability (A)
Every request receives a non-error response, without guarantee that it contains the most recent write.

### Partition Tolerance (P)
The system continues to operate despite arbitrary message loss or failure of part of the system.

## The Theorem
In a distributed system, you can only guarantee two of three properties:

- **CP**: Consistency + Partition Tolerance (sacrifice Availability)
- **AP**: Availability + Partition Tolerance (sacrifice Consistency)
- **CA**: Consistency + Availability (sacrifice Partition Tolerance - impossible in practice)

## Formal Definition
Given a distributed system with nodes N1...Nn, during a network partition that splits nodes into groups G1...Gk:

- Consistency requires that all nodes in all groups return identical responses for the same query
- Availability requires that every node that receives a request must respond
- Partition Tolerance requires the system to function despite network failures
