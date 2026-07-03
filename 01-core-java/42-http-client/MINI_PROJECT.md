# Mini Project: Async API Aggregator & WebSocket Client

## Objective
Build a modern HTTP client application that demonstrates asynchronous API aggregation (fetching data from three different endpoints concurrently) and establishes a real-time WebSocket connection to an echo server.

## Prerequisites
*   Java 11+
*   Internet connection (to hit public test APIs)

## Step 1: The Global HttpClient
Create a singleton-like wrapper for the `HttpClient`. We will provide a custom bounded thread pool to prevent thread exhaustion during async calls.

```java
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpConfig {
    
    // A bounded pool prevents sendAsync from spawning thousands of threads
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    // The client is thread-safe and MUST be reused
    public static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(executor)
            .build();
            
    public static void shutdown() {
        executor.shutdown();
    }
}
```

## Step 2: Asynchronous API Aggregator
We will fetch a random joke, a random fact, and a random activity simultaneously using `CompletableFuture`.

```java
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class AsyncAggregator {

    public void fetchDashboardData() {
        System.out.println("--- Starting Async Aggregation ---");
        long start = System.currentTimeMillis();

        // 1. Define the requests
        HttpRequest jokeReq = HttpRequest.newBuilder(URI.create("https://official-joke-api.appspot.com/random_joke")).build();
        HttpRequest factReq = HttpRequest.newBuilder(URI.create("https://uselessfacts.jsph.pl/api/v2/facts/random")).build();
        HttpRequest activityReq = HttpRequest.newBuilder(URI.create("https://www.boredapi.com/api/activity")).build();

        // 2. Send requests asynchronously
        CompletableFuture<String> jokeFuture = sendAsync(jokeReq, "Joke");
        CompletableFuture<String> factFuture = sendAsync(factReq, "Fact");
        CompletableFuture<String> activityFuture = sendAsync(activityReq, "Activity");

        // 3. Wait for ALL of them to complete
        CompletableFuture.allOf(jokeFuture, factFuture, activityFuture).join();

        long end = System.currentTimeMillis();
        System.out.println("Aggregation complete in " + (end - start) + "ms\n");
    }

    private CompletableFuture<String> sendAsync(HttpRequest request, String name) {
        return HttpConfig.CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("[" + name + "] received on thread: " + Thread.currentThread().getName());
                    return response.body();
                })
                .exceptionally(e -> {
                    System.err.println("[" + name + "] failed: " + e.getMessage());
                    return null;
                });
    }
}
```

## Step 3: The WebSocket Client
Connect to a public echo server and demonstrate reactive backpressure in the listener.

```java
import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class EchoWebSocketClient {

    public void connectAndChat() throws InterruptedException {
        System.out.println("--- Starting WebSocket Client ---");
        CountDownLatch latch = new CountDownLatch(1);

        WebSocket ws = HttpConfig.CLIENT.newWebSocketBuilder()
                .buildAsync(URI.create("wss://echo.websocket.events"), new WebSocket.Listener() {
                    
                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("[WS] Connected!");
                        webSocket.sendText("Hello, Echo Server!", true);
                        
                        // CRITICAL: Request the first message
                        WebSocket.Listener.super.onOpen(webSocket); 
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        System.out.println("[WS] Received: " + data);
                        
                        // Send another message just for fun
                        if (data.toString().contains("Hello")) {
                            webSocket.sendText("Closing soon...", true);
                        } else {
                            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Done");
                        }
                        
                        // CRITICAL: Request the next message
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        System.out.println("[WS] Closed: " + reason);
                        latch.countDown(); // Release the main thread
                        return null;
                    }
                    
                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("[WS] Error: " + error.getMessage());
                        latch.countDown();
                    }
                }).join(); // Block until connected

        // Wait for the conversation to finish
        latch.await();
    }
}
```

## Step 4: Execute
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 1. Run the Async Aggregator
        new AsyncAggregator().fetchDashboardData();
        
        // 2. Run the WebSocket Client
        new EchoWebSocketClient().connectAndChat();
        
        // 3. Clean up the thread pool so the JVM can exit
        HttpConfig.shutdown();
    }
}
```

## Expected Output
Notice how the async HTTP requests are handled by our custom thread pool (`pool-1-thread-X`).
```text
--- Starting Async Aggregation ---
[Fact] received on thread: pool-1-thread-2
[Activity] received on thread: pool-1-thread-3
[Joke] received on thread: pool-1-thread-1
Aggregation complete in 650ms

--- Starting WebSocket Client ---
[WS] Connected!
[WS] Received: echo.websocket.events sponsored by Lob.com
[WS] Received: Hello, Echo Server!
[WS] Received: Closing soon...
[WS] Closed: Done
```