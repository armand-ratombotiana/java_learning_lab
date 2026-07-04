# HTTP Protocol - Mental Models

## 1. The Envelope Model

HTTP messages as structured envelopes.

```java
public class HttpRequest {
    private HttpMethod method;
    private URI uri;
    private Map<String, List<String>> headers = new HashMap<>();
    private byte[] body;
}
```

## 2. The Restaurant Model

| HTTP Concept | Restaurant Analogy |
|-------------|-------------------|
| Client | Customer |
| Server | Kitchen |
| GET | "What's on the menu?" |
| POST | "I'd like to order something new" |
| PUT | "Please replace my order" |
| DELETE | "Cancel my order" |
| 200 OK | "Here's your food" |
| 404 | "We don't have that dish" |

## 3. The Highway Model (HTTP/2 Multiplexing)

HTTP/1.1: single-lane road - cars wait in line
HTTP/2: multi-lane highway - cars travel simultaneously

```java
List<CompletableFuture<String>> futures = uris.stream()
    .map(uri -> HttpRequest.newBuilder(uri).GET().build())
    .map(req -> client.sendAsync(req, BodyHandlers.ofString())
        .thenApply(HttpResponse::body))
    .toList();
CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
```

## 4. The Vending Machine Model (Stateless)

Each HTTP request is independent.

```java
@RestController
public class ApiController {
    @GetMapping("/resource")
    public ResponseEntity<Resource> getResource(@RequestHeader("Authorization") String token) {
        if (!authService.validate(token)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(resourceService.getResource());
    }
}
```
