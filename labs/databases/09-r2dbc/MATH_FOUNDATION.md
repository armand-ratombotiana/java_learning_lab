# Math Foundation: R2DBC Backpressure

## Demand Management
```
request(n) from Subscriber
    │
    ▼
Publisher emits up to n items
    │
    ▼
Subscriber processes and requests next batch
    │
    ▼
Driver fetches next n rows from cursor
```

## Thread Efficiency
Compare JDBC thread model vs R2DBC:

```
JDBC:  100 connections = 100 threads (blocked)
R2DBC: 100 connections = 2-4 threads (event loop)
```

Thread count formula for event loop:
```
optimalThreads = availableCores * (1 + blockingCoefficient)
               where blockingCoefficient ≈ 0 for R2DBC
```

## Response Time Percentiles
With JDBC under load, P99 latency degrades sharply due to thread pool saturation:
```
Throughput: 100 req/s → thread pool exhausted → queuing delay
```
With R2DBC, event loop handles all requests without queuing:
```
Throughput: 1000 req/s → backpressure applied → graceful degradation
```
