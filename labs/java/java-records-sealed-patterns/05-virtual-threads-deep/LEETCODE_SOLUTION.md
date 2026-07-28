# LeetCode Solution: Web Crawler Multithreaded (Virtual Threads)

**Problem:** [1242. Web Crawler Multithreaded](https://leetcode.com/problems/web-crawler-multithreaded/)

Demonstrates virtual threads + structured concurrency for concurrent web crawling.

## Approach

Use `StructuredTaskScope.ShutdownOnFailure` with virtual threads to crawl URLs in parallel. Scoped values carry the depth limit.

## Java 21 Solution

```java
import java.util.*;
import java.util.concurrent.*;

interface HtmlParser {
    List<String> getUrls(String url);
}

class Solution {

    private static final ScopedValue<Integer> DEPTH = ScopedValue.newInstance();
    private static final ScopedValue<HtmlParser> PARSER = ScopedValue.newInstance();

    public List<String> crawl(String startUrl, HtmlParser htmlParser, int maxDepth)
            throws Exception {

        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        return ScopedValue.where(DEPTH, 0)
                .where(PARSER, htmlParser)
                .call(() -> {
                    crawlRecursive(startUrl, maxDepth, visited);
                    return List.copyOf(visited);
                });
    }

    private void crawlRecursive(String url, int maxDepth, Set<String> visited)
            throws Exception {

        int currentDepth = DEPTH.get();
        if (currentDepth >= maxDepth) return;

        List<String> urls = PARSER.get().getUrls(url);

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<Future<Void>> forks = new ArrayList<>();

            for (String next : urls) {
                String host = extractHost(next);
                if (!host.equals(extractHost(url))) continue;
                if (!visited.add(next)) continue;

                int nextDepth = currentDepth + 1;

                forks.add(scope.fork(() ->
                    ScopedValue.where(DEPTH, nextDepth)
                        .call(() -> {
                            crawlRecursive(next, maxDepth, visited);
                            return null;
                        })
                ));
            }

            scope.join();
            scope.throwIfFailed();
        }
    }

    private String extractHost(String url) {
        return url.split("/")[2];
    }
}
```

## Key Takeaway

Structured concurrency with virtual threads makes concurrent crawling **safe and composable** — no thread leaks, no forgotten `join()`, and scoped values eliminate manual parameter threading.
