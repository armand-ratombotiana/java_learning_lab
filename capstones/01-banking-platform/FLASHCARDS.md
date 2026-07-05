# Flashcards: Banking Platform

Front: What is the Saga pattern? | Back: A sequence of local transactions where each step has a compensating action for rollback, ensuring distributed transaction consistency without 2PC.

Front: What are pending holds? | Back: Temporary reservations on account balance for in-flight transactions, preventing double-spending.

Front: What is an idempotency key? | Back: A unique identifier for each request that ensures the same operation is applied only once, even if the request is retried.

Front: How does CQRS apply here? | Back: Write operations (payments) use one model; read operations (balance inquiries) use a separate, optimized projection.

Front: What is event sourcing? | Back: Storing state changes as an immutable append-only log; current state is derived by replaying events.

Front: What is a compensating transaction? | Back: A reverse operation that undoes a previously committed transaction in a saga when a subsequent step fails.
