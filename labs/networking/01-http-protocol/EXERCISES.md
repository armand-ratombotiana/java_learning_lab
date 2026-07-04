# HTTP Protocol - Exercises

## Exercise 1: Build a GET Client
Write a Java program that sends a GET request to https://jsonplaceholder.typicode.com/posts and prints all titles.

## Exercise 2: POST with Authentication
Implement a POST request to create a resource with Bearer token authentication.

## Exercise 3: HTTP/2 vs HTTP/1.1 Benchmark
Write a benchmark sending 50 requests using both versions, comparing total time.

## Exercise 4: Build a Simple Router
Implement an HTTP server with path parameter routing: /users/{id}

## Exercise 5: Implement Caching
Create a caching HTTP client that stores responses in memory.

## Exercise 6: Error Handling Middleware
Build an HTTP server with error handling middleware.

### Starter Code for Exercise 1
```java
public class Exercise1 {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
            .GET().build();
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        // Parse and print titles from JSON array
    }
}
```
