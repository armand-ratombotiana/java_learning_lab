# Saga Pattern Flashcards

## Q: What is a saga?
**A:** A sequence of local transactions with compensating actions to maintain data consistency across microservices.

## Q: What is choreography?
**A:** A decentralized saga where each service publishes events and reacts to other services' events.

## Q: What is orchestration?
**A:** A centralized saga where a coordinator tells services what to do.

## Q: What is a compensating transaction?
**A:** An action that semantically undoes a previous transaction (e.g., refund).

## Q: Why must saga steps be idempotent?
**A:** Because events can be delivered multiple times due to retries.

## Q: What is LIFO compensation?
**A:** Compensating in reverse order - undo the last step first.

## Q: What is saga timeout?
**A:** A maximum duration for a saga; if exceeded, compensation is triggered.

## Q: What is saga store?
**A:** A persistent store for saga state, enabling recovery after failures.

## Q: What is TCC (Try-Confirm/Cancel)?
**A:** A protocol similar to sagas but with explicit two-phase operations per service.

## Q: What is the difference between 2PC and Saga?
**A:** 2PC is blocking and requires all participants to be available; sagas are non-blocking with compensation.
