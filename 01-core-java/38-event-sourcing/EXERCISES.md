# Event Sourcing Exercises

## Exercise 1: Order Aggregate
Implement an event-sourced Order aggregate with events:
- OrderCreated
- ItemAdded
- ItemRemoved
- OrderShipped
- OrderDelivered

Replay events to get current order state.

## Exercise 2: Snapshot Implementation
Add snapshotting to the BankAccount example - serialize state after every N events to optimize replay.

## Exercise 3: Create a Projection
Build a projection that counts total transactions and average withdrawal amount from event history.

## Exercise 4: Add Optimistic Locking
Implement version checking on append to prevent concurrent writes from corrupting event stream.

## Exercise 5: CQRS Implementation
Separate the read model (projections) from write model (commands) for a simple domain.