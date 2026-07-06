# Real-World Project: GC-Tuned Web Server

## Overview
Design and benchmark a web server under different GC configurations. The server handles REST API requests with various allocation patterns, and you must find the optimal GC algorithm and tuning parameters.

## Architecture
```
HttpServer (com.sun.net.httpserver)
  → RequestRouter
    → Handlers (each creates different allocation patterns)
      → Handler A: many short-lived byte arrays (JSON parsing)
      → Handler B: medium-lived cached responses
      → Handler C: long-lived session objects
```

## Allocation Patterns
1. **Handler A** (/api/search): allocate 100 KB per request, 80% die in young gen
2. **Handler B** (/api/cache): allocate 10 MB cache, retained for 60 seconds
3. **Handler C** (/api/session): allocate 1 KB session objects, retained for 5 minutes

## GC Configurations to Test
| Collector | Young | Old | Key Flags |
|-----------|-------|-----|-----------|
| Parallel | 256m | 1g | -XX:+UseParallelGC |
| G1 | auto | auto | -XX:+UseG1GC -XX:MaxGCPauseMillis=50 |
| ZGC | n/a | n/a | -XX:+UseZGC -XX:MaxGCPauseMillis=10 |
| Shenandoah | n/a | n/a | -XX:+UseShenandoahGC |

## Metrics Collected
- Throughput (requests/second)
- GC pause time (P99, P99.9)
- Allocation rate (MB/sec)
- GC CPU time (% of total)
- Heap footprint after steady state

## Deliverables
- `GcTunedServer.java` — HTTP server with all handlers
- `LoadGenerator.java` — concurrent-load generator
- `GcConfigRunner.java` — runs server with each GC config
- `results.csv` — comparative metrics
- Report: which GC configuration performed best and why
