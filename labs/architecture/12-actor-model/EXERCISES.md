# Actor Model Exercises

## Beginner Exercises

### Exercise 1: Counter Actor
Create a simple counter actor that can increment, decrement, and return the current count.

### Exercise 2: Message Protocol
Design message protocol for a chat room where users can join, send messages, and leave.

## Intermediate Exercises

### Exercise 3: Bank Account Actor
Implement a bank account actor:
- Deposit, withdraw, get balance
- Insufficient funds validation
- Transaction history in actor state

### Exercise 4: Supervisor Hierarchy
Create a supervision hierarchy:
- OrderSupervisor with child OrderActors
- Restart strategy for transient failures
- Stop strategy for permanent failures
- Escalate unhandled failures

### Exercise 5: Ask Pattern
Implement request-response pattern:
- Send query to actor
- Actor responds with data
- Handle timeout
- Handle actor not found

## Advanced Exercises

### Exercise 6: Cluster Actors
Set up Akka Cluster with 3 nodes:
- Actors distributed across nodes
- Cluster singleton for coordinator
- Cluster sharding for stateful actors
- Handle node failure

### Exercise 7: Event-Sourced Actor
Implement an event-sourced actor with Akka Persistence:
- Events stored to database
- State rebuilt from events on restart
- Snapshot optimization
- Command validation before event persistence

### Exercise 8: Actor-Based Web Service
Build an actor-based HTTP service using Akka HTTP:
- Request per actor
- Actor pooling for load distribution
- Supervision for fault tolerance
- Cluster deployment for scalability
