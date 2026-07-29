# LeetCode 1242: Web Crawler Multithreaded

> **Difficulty**: Medium | **Company**: Amazon, Google, Meta | **Category**: Concurrency Deep (Thread Creation)

## Problem

Given a URL `startUrl` and an interface `HtmlParser`, implement a multithreaded web crawler that:

- Crawls all reachable URLs under the same hostname.
- Returns all URLs in any order.
- Uses multiple threads to crawl concurrently.

## Solution

Uses a thread pool for concurrent crawling with thread-safe visited URL tracking via a `ConcurrentHashMap`.

```java
import java.util.*;
import java.util.concurrent.*;
import java.net.URI;

/**
 * LeetCode 1242: Web Crawler Multithreaded
 *
 * Thread-safe crawler using a thread pool and ConcurrentHashMap for visited URLs.
 *
 * Time: O(N) where N = number of URLs crawled
 * Space: O(N) for visited set and queue
 */
public class WebCrawler {

    interface HtmlParser {
        List<String> getUrls(String url);
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String hostname = extractHostname(startUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            crawlRecursive(startUrl, hostname, htmlParser, visited, executor);
        }

        return new ArrayList<>(visited);
    }

    private void crawlRecursive(
            String url, String hostname, HtmlParser parser,
            Set<String> visited, ExecutorService executor) {

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String nextUrl : parser.getUrls(url)) {
            if (!extractHostname(nextUrl).equals(hostname)) continue;
            if (!visited.add(nextUrl)) continue;

            String finalUrl = nextUrl;
            futures.add(CompletableFuture.runAsync(() ->
                crawlRecursive(finalUrl, hostname, parser, visited, executor), executor
            ));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private String extractHostname(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ─────────────────────
    // Verification (simulated)
    // ─────────────────────
    public static void main(String[] args) {
        var crawler = new WebCrawler();

        // Simulate a simple site graph
        // start: "http://example.com"
        //   ├── "http://example.com/page1"
        //   ├── "http://example.com/page2"
        //   └── "http://other.com/page3" (different host — excluded)
        HtmlParser parser = url -> {
            Map<String, List<String>> graph = Map.of(
                "http://example.com", List.of("http://example.com/page1", "http://example.com/page2", "http://other.com/page3"),
                "http://example.com/page1", List.of("http://example.com/subpage"),
                "http://example.com/page2", List.of(),
                "http://example.com/subpage", List.of()
            );
            return graph.getOrDefault(url, List.of());
        };

        List<String> result = crawler.crawl("http://example.com", parser);
        System.out.println("Crawled URLs: " + result);

        assert result.contains("http://example.com");
        assert result.contains("http://example.com/page1");
        assert result.contains("http://example.com/page2");
        assert result.contains("http://example.com/subpage");
        assert !result.contains("http://other.com/page3");
        assert result.size() == 4 : "Expected 4 URLs, got " + result.size();

        System.out.println("All tests passed.");
    }
}
```

## Complexity

| Metric          | Value       |
|-----------------|-------------|
| Time            | O(N)        |
| Space           | O(N)        |

## Key Insights

1. **Thread-safe visited set**: `ConcurrentHashMap.newKeySet()` handles concurrent `add` checks atomically.
2. **Thread-per-task executor**: `Executors.newVirtualThreadPerTaskExecutor()` creates a new virtual thread per crawl task — ideal for I/O-bound crawling.
3. **Hostname filtering**: Only crawl URLs under the same hostname to stay within the problem constraints.
4. **Join all subtasks**: `CompletableFuture.allOf(...).join()` ensures the root call waits for all nested crawls to complete.
