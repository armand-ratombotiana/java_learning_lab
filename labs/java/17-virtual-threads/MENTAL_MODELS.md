# Mental Models — Virtual Threads

## Virtual Thread as Toy Thread
A virtual thread is like a toy thread (string) — you can have millions in a drawer. A platform thread is a heavy chain.

## Carrier Thread as Bus
Virtual threads are passengers. Carrier threads are buses. Passengers get on/off at stops (blocking points). One bus can carry many passengers.

## M:N Scheduling
M virtual threads scheduled on N carrier threads (platform threads), which are scheduled on CPU cores.

## Yield as Cooperation
When a virtual thread blocks (I/O, lock), it voluntarily yields its carrier thread — like stepping off the bus at a stop.

## StructuredTaskScope as Try-With-Resources
```java
try (var scope = new StructuredTaskScope<>()) {
    // Tasks are "resources" — they're cleaned up when scope closes
}
```
