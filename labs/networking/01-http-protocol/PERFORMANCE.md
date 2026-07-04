# HTTP Protocol - Performance

## Optimization Techniques

### Connection Pooling
```java
public HttpClient optimizedClient() {
    return HttpClient.newBuilder()
        .version(Version.HTTP_2)
        .connectTimeout(Duration.ofMillis(500))
        .executor(Executors.newFixedThreadPool(50))
        .build();
}
```

### Performance Benchmark
```java
public class HttpBenchmark {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/bench"))
            .GET().build();
        long start = System.nanoTime();
        int iterations = 100;
        CompletableFuture<?>[] futures = new CompletableFuture[iterations];
        for (int i = 0; i < iterations; i++)
            futures[i] = client.sendAsync(request, BodyHandlers.discarding());
        CompletableFuture.allOf(futures).join();
        long elapsed = System.nanoTime() - start;
        System.out.printf("Total: %d ms, Avg: %.2f ms%n",
            elapsed / 1_000_000, (elapsed / 1_000_000.0) / iterations);
    }
}
```

### Compression
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .header("Accept-Encoding", "gzip, deflate")
    .GET().build();
// Client automatically decompresses
```

## Performance Checklist
1. Use HTTP/2 for multiplexing
2. Enable gzip compression
3. Use connection pooling
4. Set appropriate timeouts
5. Use async where possible
6. Implement retry with backoff
7. Cache responses with ETag
8. Minimize redirects
