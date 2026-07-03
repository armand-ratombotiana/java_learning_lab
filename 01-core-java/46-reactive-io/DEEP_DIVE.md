# Deep Dive: Reactive I/O

## 1. The Intersection of NIO and Reactive Streams
In previous modules, we learned about **NIO (Non-blocking I/O)**, which allows a single thread to multiplex thousands of connections using a `Selector`. We also learned about **Reactive Programming (Flow API)**, which provides a standard for asynchronous, push-based data streams with backpressure.

**Reactive I/O** is the marriage of these two concepts. It applies the Reactive Streams specification (Publisher/Subscriber) directly to non-blocking network and file channels.

## 2. Why Reactive I/O?
While raw NIO (using Selectors and ByteBuffers) is extremely fast, it is notoriously difficult to write. You have to manually manage partial writes, buffer flipping, and state machines.
Reactive I/O frameworks abstract away the raw NIO mechanics while preserving the performance benefits, wrapping the data flow in a safe, declarative, backpressure-aware API.

*   **The Problem with Raw NIO**: If a client sends 10GB of data over a non-blocking socket, and you read it into memory faster than you can process it, you get an `OutOfMemoryError`.
*   **The Reactive I/O Solution**: The Subscriber (your application) tells the Publisher (the network socket): "I am ready for 1 chunk of data." The framework reads exactly 1 chunk from the socket, delivers it, and then *stops reading from the OS network buffer* until you request more. This naturally throttles the sender at the TCP level (TCP Windowing).

## 3. AsynchronousFileChannel (File I/O)
Java 7 introduced `AsynchronousFileChannel`. While not strictly part of the Flow API, it is the foundation of reactive file processing in Java. It allows you to read/write files without blocking the calling thread, using either `Future`s or `CompletionHandler`s.

```java
AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
ByteBuffer buffer = ByteBuffer.allocate(1024);

// Read asynchronously. The main thread continues immediately.
channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("Read " + result + " bytes asynchronously.");
        // Process data...
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
    }
});
```

## 4. Reactive HTTP Client (Java 11+)
The modern `java.net.http.HttpClient` is inherently reactive. The `HttpResponse.BodySubscriber` implements `java.util.concurrent.Flow.Subscriber<List<ByteBuffer>>`.

This means when you download a massive file, the HTTP client acts as a `Publisher`. It pushes chunks of data (`ByteBuffer`s) to your `Subscriber`. If your subscriber does not call `subscription.request(1)`, the HTTP client pauses reading from the network socket, applying true end-to-end backpressure.

## 5. Reactive Frameworks (Project Reactor / Netty)
The JDK provides the building blocks (NIO, Flow API), but building a production-ready Reactive I/O server from scratch is complex. In the real world, developers use frameworks built on top of these JDK primitives.

*   **Netty**: The undisputed king of asynchronous, event-driven network application frameworks in Java. It completely abstracts raw NIO into a pipeline of handlers.
*   **Project Reactor (Spring WebFlux)**: Builds on top of Netty to provide a rich, declarative API (`Mono` and `Flux`) that implements the Reactive Streams specification.

### Conceptual Example (Spring WebFlux / Reactor)
```java
// A reactive REST endpoint that streams data from a database to a client
@GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
public Flux<Data> streamData() {
    // The database driver (e.g., R2DBC) is a Publisher.
    // The HTTP Response is a Subscriber.
    // Data flows from the DB to the network non-blockingly, with backpressure preventing OOM.
    return databaseRepository.findAllData(); 
}
```