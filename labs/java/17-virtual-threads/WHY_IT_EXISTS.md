# Why Virtual Threads Exist

## The Thread-per-Request Problem
Web servers traditionally use one OS thread per request. With ~1000 threads per GB, scaling beyond ~10K concurrent requests is expensive.

## Blocking Is Harmful (on Platform Threads)
Blocking a platform thread wastes 1 MB of stack memory. Developers resorted to asynchronous, non-blocking code (CompletableFuture, reactive) which is harder to reason about.

## Virtual Threads Solution
- One virtual thread per request — no pooling
- Blocking yields the carrier thread, not the OS thread
- Developers write synchronous, blocking code — the JVM manages multiplexing

## Goal
Make concurrency accessible to "plain old Java" developers — no callbacks, no reactive, no async keywords.
