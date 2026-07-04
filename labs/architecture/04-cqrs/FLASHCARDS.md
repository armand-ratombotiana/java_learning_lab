# CQRS Flashcards

## Q: What is CQRS?
**A:** Separating read and write operations into different models, each optimized for its purpose.

## Q: What is a command?
**A:** An imperative message expressing intent to change system state (e.g., PlaceOrder).

## Q: What is a query?
**A:** A request for data that has no side effects on system state.

## Q: What is a projection?
**A:** A component that subscribes to events and updates read models accordingly.

## Q: What is eventual consistency in CQRS?
**A:** The read model may lag behind the write model temporarily.

## Q: What is a command handler?
**A:** A component that receives commands and executes corresponding domain logic.

## Q: What is a query handler?
**A:** A component that processes queries by reading from the read model.

## Q: What is a materialized view?
**A:** A pre-computed, denormalized read model optimized for specific queries.

## Q: What is event replay?
**A:** Re-processing historical events to rebuild read models from scratch.

## Q: What is a command gateway?
**A:** A facade that simplifies sending commands and receiving results asynchronously.
