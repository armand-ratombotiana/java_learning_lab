# Real-World Project: Parallel Web Crawler

## Overview
Build a concurrent web crawler that fetches and processes HTML pages in parallel. This project simulates real-world systems where thread pools, futures, and structured concurrency manage many I/O-bound tasks.

## Architecture
```
URL Frontier (ConcurrentLinkedQueue)
    ↓
ForkJoinPool / ThreadPoolExecutor (fetch workers)
    ↓
HTML Parser (extracts links)
    ↓
Link Filter (deduplicates via ConcurrentHashMap)
    ↓
In-Memory Store (ConcurrentHashMap of URL → content)
```

## Key Components
1. **URL Frontier** — thread-safe queue of URLs to crawl
2. **Fetch Workers** — use ThreadPoolExecutor with bounded work queues
3. **Parser Workers** — use CompletableFuture for non-blocking composition
4. **Link Store** — ConcurrentHashMap with `putIfAbsent` for deduplication
5. **Crawl Coordinator** — uses StructuredTaskScope to manage depth-first vs breadth-first policies

## Constraints
- Max 50 concurrent connections
- Respect robots.txt (simulate with a Set of disallowed paths)
- Graceful shutdown on SIGINT (Runtime.getRuntime().addShutdownHook)
- Log crawl progress with SLF4J

## Evaluation Criteria
- Correctness: no duplicate pages, all links discovered
- Performance: utilize all available CPU cores
- Robustness: handle malformed HTML, timeouts, DNS failures

## Deliverables
- `WebCrawler.java` — main coordinator
- `FetchWorker.java`, `ParseWorker.java` — worker implementations
- `CrawlConfig.java` — configuration (thread counts, timeouts, seeds)
- `WebCrawlerTest.java` — integration test
