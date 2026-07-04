# HTTP Protocol - Common Mistakes

## 1. Not Handling Redirects
```java
// WRONG - doesn't follow redirects
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://example.com"))
    .build();

// RIGHT
HttpClient client = HttpClient.newBuilder()
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();
```

## 2. Ignoring Connection Timeouts
```java
// WRONG - no timeout, can hang forever
HttpClient client = HttpClient.newHttpClient();

// RIGHT
HttpClient client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .build();
```

## 3. Blocking on Async Calls
```java
// WRONG - blocking defeats async purpose
client.sendAsync(request, handler).get();

// RIGHT
client.sendAsync(request, handler)
    .thenApply(HttpResponse::body)
    .thenAccept(System.out::println);
```

## 4. Wrong Content-Type for POST
```java
// WRONG - missing Content-Type
HttpRequest.newBuilder()
    .POST(BodyPublishers.ofString(json))
    .build();

// RIGHT
HttpRequest.newBuilder()
    .POST(BodyPublishers.ofString(json))
    .header("Content-Type", "application/json")
    .build();
```

## 5. Not Closing Response InputStream
```java
// WRONG
HttpResponse<InputStream> response = client.send(request,
    HttpResponse.BodyHandlers.ofInputStream());

// RIGHT - use try-with-resources
try (InputStream stream = response.body()) { /* process */ }
```
