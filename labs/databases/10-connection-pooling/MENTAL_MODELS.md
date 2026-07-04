# Mental Models: Connection Pooling

## Rental Car Fleet
A connection pool is like a rental car fleet. You don't build a new car every time you need to drive. Cars are pre-built, parked in the lot. When you need one, you pick it up, use it, and return it. If all cars are rented, you wait. The fleet size limits how many concurrent drivers there are.

## Valet Parking
You give your keys (connection) to a valet (pool). The valet parks your car (returns connection to pool). When you need it again, the valet retrieves it. The valet also checks if the car needs maintenance (validation query) and rotates cars to prevent any single car from being used too long (max lifetime).

## Thread Pool Analogy
Just as thread pools reuse threads to avoid thread creation overhead, connection pools reuse connections to avoid TCP/TLS handshake overhead. Both have: min/max size, queue for pending requests, timeout for waiting, and metrics for monitoring.

## Buttery Smooth Highway
Without pooling: stop at every toll booth, pay, wait for receipt, drive 100m, repeat. With pooling: express pass, drive through without stopping. The toll plaza capacity (pool size) determines how many cars can be in toll zone simultaneously.
