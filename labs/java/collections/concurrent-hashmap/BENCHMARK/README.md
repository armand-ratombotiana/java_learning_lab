# Benchmarking ConcurrentHashMap

This directory contains performance benchmarks comparing
`ConcurrentHashMapSimulator`, `StripedLockMap`, and `java.util.concurrent.ConcurrentHashMap`
under various thread counts and operation mixes.

Run benchmarks with:

```bash
mvn test -Pbenchmark
```
