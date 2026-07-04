# How R2DBC Works

## Connection Flow
1. `ConnectionFactory.create()` returns `Publisher<Connection>`
2. Subscriber requests a connection
3. Driver opens TCP socket asynchronously (non-blocking NIO)
4. On socket ready, performs SSL handshake (if configured) and authentication
5. Completes the `Publisher` with a `Connection` instance

## Query Execution
1. `Client.sql(query)` creates a `Spec` builder
2. `.bind(param, value)` binds parameters
3. `.fetch()` creates a `FetchSpec`
4. `.first()` / `.all()` / `.rowsUpdated()` subscribes and triggers execution
5. Driver sends query text + parameters asynchronously
6. Rows stream back as `Publisher<Row>` with backpressure
7. Each row is mapped via the provided row mapping function

## Transaction
1. `Connection.beginTransaction()` sends `BEGIN` asynchronously
2. `Connection.commitTransaction()` sends `COMMIT`
3. `Connection.rollbackTransaction()` sends `ROLLBACK`
4. `R2dbcTransactionManager` uses reactive context to track connection binding
