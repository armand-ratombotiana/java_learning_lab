# Mental Models: R2DBC

## JDBC is Synchronous Telephone
JDBC is like calling someone on the phone. You dial, wait for an answer, have the conversation, and hang up. While talking, you can't do anything else. Your thread is dedicated to that one call.

## R2DBC is Async Messaging
R2DBC is like sending a text message. You send a query request, get a `Publisher<Result>`, and go do other work. When the database responds, a callback fires. You can handle many conversations concurrently without waiting.

## DatabaseClient is Reactive RestTemplate
`DatabaseClient` is to R2DBC what `RestTemplate`/`WebClient` is to HTTP. It provides a fluent API to build and execute queries, bind parameters, and map results reactively.

## Connection Pool is Reactive Resource Pool
Think of `ConnectionPool` as a pool of async resources. `acquire()` returns `Publisher<Connection>` – you subscribe to get a connection when one becomes available, without blocking.
