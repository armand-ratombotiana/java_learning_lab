# HTTP Clients (Java 11+) -- Code Deep Dive
## Main Implementation
Package: com.javalab.03

### Java 11+ HttpClient
HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

### Async HttpClient
CompletableFuture future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
future.thenAccept(response -> System.out.println(response.body()));

### Round-Trip Test Pattern
Each implementation includes a local loopback test that verifies the server
can accept connections, process messages, and return responses correctly.
