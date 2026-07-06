# ConcurrentHashMap Tests

This directory holds integration and stress tests.

Run all concurrent tests:

```bash
mvn test
```

For stress testing with higher thread counts:

```bash
mvn test -Dstress.threads=64 -Dstress.iterations=100000
```
