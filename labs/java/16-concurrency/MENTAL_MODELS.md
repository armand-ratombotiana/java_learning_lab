# Mental Models — Concurrency

## Thread as Worker
Each thread is a worker in a workshop. Tasks are assignments. The number of workers (threads) may be limited by physical space (CPU cores).

## Lock as Key
A `synchronized` block or Lock is like a key to a room — only one thread can enter at a time.

## Race Condition as Crossed Signals
Two threads act on shared data without coordination — like two drivers at a four-way stop without rules.

## Deadlock as Gridlock
Thread A holds lock 1 and waits for lock 2; Thread B holds lock 2 and waits for lock 1. Both are stuck.

## ExecutorService as Team Leader
A team leader manages a pool of workers. Tasks arrive, are assigned, completed, and workers return to the pool.

## CompletableFuture as IOU
You get an IOU (CompletableFuture) immediately. It will be filled later. You define what happens when it's filled, using thenApply, thenAccept, etc.
