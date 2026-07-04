# Theory: R2DBC Reactive Database Access

## Reactive Streams Specification
- **Publisher<T>**: Produces items
- **Subscriber<T>**: Consumes items with `onNext()`, `onError()`, `onComplete()`
- **Subscription**: Links publisher + subscriber with `request(n)` for backpressure
- **Processor<T,R>**: Both publisher and subscriber

## R2DBC SPI
- `ConnectionFactory`: Creates `Publisher<Connection>`
- `Connection`: Non-blocking equivalent of JDBC Connection
- `Batch`: Execute multiple statements
- `Result`: Reactive result handling with row metadata
- `Row`: Access column values by index or name

## Backpressure in R2DBC
R2DBC respects Reactive Streams backpressure. The database driver pulls rows as the subscriber requests them, preventing unbounded buffering:

```
Subscriber.request(10) → Driver fetches 10 rows → onNext × 10
Subscriber.request(5)  → Driver fetches 5 rows  → onNext × 5
```

## Transaction
R2DBC provides `R2dbcTransactionManager` for reactive transaction management. Transactions require an active connection bound to the reactive context (Project Reactor's `Context`).
