# gRPC — Deep Interview Guide

## Table of Contents
1. [gRPC Fundamentals](#grpc-fundamentals)
2. [Protocol Buffers](#protocol-buffers)
3. [Service Definitions & HTTP/2](#service-definitions--http2)
4. [Streaming Types](#streaming-types)
5. [Interceptors, Deadlines, Cancellation](#interceptors-deadlines-cancellation)
6. [Load Balancing & Connection Management](#load-balancing--connection-management)
7. [Security, TLS, Authentication](#security-tls-authentication)
8. [Java Code Examples](#java-code-examples)
9. [15+ Interview Questions](#15-interview-questions)

---

## gRPC Fundamentals

gRPC is a high-performance, open-source RPC framework initially developed by Google. It uses HTTP/2 for transport, Protocol Buffers as the interface description language, and provides features like streaming, flow control, and pluggable authentication.

### Key Features

| Feature | Description |
|---------|-------------|
| **HTTP/2** | Multiplexed streams, header compression, server push |
| **Protocol Buffers** | Language-neutral, efficient serialization |
| **Streaming** | Unary, server-streaming, client-streaming, bidirectional |
| **Deadlines** | Client-specified timeout for RPCs |
| **Cancellation** | Client or server can cancel an RPC |
| **Interceptors** | Middleware for cross-cutting concerns |
| **Load Balancing** | Client-side, proxy (envoy), or service mesh |
| **Pluggable Auth** | TLS, OAuth2, JWT, custom credentials |

### Comparison with REST

| Aspect | gRPC | REST |
|--------|------|------|
| **Protocol** | HTTP/2 | HTTP/1.1 or HTTP/2 |
| **Payload** | Protobuf (binary) | JSON/XML (text) |
| **Contract** | Strict (.proto file) | Loose (OpenAPI) |
| **Streaming** | Native support | SSE, WebSocket |
| **Codegen** | Server/client stubs | Manual or OpenAPI gen |
| **Browser support** | Via gRPC-Web | Native |
| **Performance** | ~10x faster | Baseline |

---

## Protocol Buffers

Protocol Buffers (protobuf) is a language-neutral, platform-neutral mechanism for serializing structured data.

### Message Definition

```protobuf
syntax = "proto3";

package ecommerce;

option java_package = "com.example.ecommerce.proto";
option java_multiple_files = true;

import "google/protobuf/timestamp.proto";
import "google/protobuf/wrappers.proto";

// Product represents an item in the catalog
message Product {
    string id = 1;
    string name = 2;
    string description = 3;
    double price = 4;
    repeated string tags = 5;
    google.protobuf.Timestamp created_at = 6;
    Category category = 7;
    google.protobuf.DoubleValue rating = 8;  // optional wrapper
}

enum Category {
    CATEGORY_UNSPECIFIED = 0;
    ELECTRONICS = 1;
    CLOTHING = 2;
    BOOKS = 3;
    HOME = 4;
}

message GetProductRequest {
    string product_id = 1;
}

message ListProductsRequest {
    int32 page_size = 1;
    string page_token = 2;
}

message ListProductsResponse {
    repeated Product products = 1;
    string next_page_token = 2;
}

message CreateProductRequest {
    Product product = 1;
}

message DeleteProductRequest {
    string product_id = 1;
}
```

### Scalar Types

| Proto Type | Java Type | Notes |
|------------|-----------|-------|
| `double` | `double` | 64-bit |
| `float` | `float` | 32-bit |
| `int32` | `int` | Variable-length |
| `int64` | `long` | Variable-length |
| `uint32` | `int` | Variable-length unsigned |
| `uint64` | `long` | Variable-length unsigned |
| `sint32` | `int` | Efficient for negative numbers |
| `fixed32` | `int` | Always 4 bytes |
| `fixed64` | `long` | Always 8 bytes |
| `bool` | `boolean` | |
| `string` | `String` | UTF-8 encoded |
| `bytes` | `ByteString` | Raw bytes |

### Field Rules

- **singular**: Zero or one instance (default in proto3)
- **optional**: Explicit presence tracking (wrapper types)
- **repeated**: Zero or more (ordered list)
- **map<K,V>**: Key-value pairs

### Reserved Fields

```protobuf
message Product {
    reserved 2, 15, 20 to 30;
    reserved "old_field_name";
}
```

---

## Service Definitions & HTTP/2

### Service Definition

```protobuf
service ProductService {
    // Unary
    rpc GetProduct(GetProductRequest) returns (Product);

    // Server-streaming
    rpc ListProducts(ListProductsRequest) returns (stream Product);

    // Client-streaming
    rpc BulkCreateProducts(stream CreateProductRequest) returns (BulkCreateResponse);

    // Bidirectional streaming
    rpc SearchProducts(stream SearchRequest) returns (stream SearchResult);
}

message BulkCreateResponse {
    int32 created_count = 1;
    repeated string product_ids = 2;
}

message SearchRequest {
    oneof query {
        string keyword = 1;
        double max_price = 2;
        Category category = 3;
    }
    int32 limit = 4;
}

message SearchResult {
    Product product = 1;
    double relevance_score = 2;
}
```

### HTTP/2 Mapping

gRPC maps RPCs to HTTP/2:

```
POST /ecommerce.ProductService/GetProduct
POST /ecommerce.ProductService/ListProducts
POST /ecommerce.ProductService/BulkCreateProducts
POST /ecommerce.ProductService/SearchProducts
```

**Headers**: `Content-Type: application/grpc`, `TE: trailers`, custom metadata.

**gRPC status codes** are sent as HTTP/2 trailers.

---

## Streaming Types

### 1. Unary RPC

Simple request-response. Most common pattern.

```
Client ──Request──→ Server
Client ←─Response── Server
```

### 2. Server-Streaming RPC

Client sends a single request, server sends back a stream of responses.

```
Client ──Request────────→ Server
Client ←─Response (1)──── Server
Client ←─Response (2)──── Server
Client ←─Response (N)──── Server
Client ←─Status (trailers)─ Server
```

**Use cases**: Paginated queries, event feeds, large result sets.

### 3. Client-Streaming RPC

Client sends a stream of requests, server sends back a single response.

```
Client ──Request (1)───→ Server
Client ──Request (2)───→ Server
Client ──Request (N)───→ Server
Client ←─Response─────── Server
```

**Use cases**: File uploads, batch processing, aggregation.

### 4. Bidirectional Streaming RPC

Both client and server send independent streams.

```
Client ──Request (1)───→ Server
Client ←─Response (1)─── Server
Client ──Request (2)───→ Server
Client ←─Response (2)─── Server
Client ──Request (N)───→ Server
Client ←─Response (N)─── Server
```

**Use cases**: Chat, real-time collaboration, streaming ETL.

---

## Interceptors, Deadlines, Cancellation

### Interceptors

Interceptors are similar to middleware in web frameworks. They intercept incoming/outgoing calls for cross-cutting concerns.

**Server Interceptor**:
```java
public class LoggingServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String method = call.getMethodDescriptor().getFullMethodName();
        log.info("Received call: {}", method);

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            @Override
            public void sendMessage(RespT message) {
                log.info("Sending response for: {}", method);
                super.sendMessage(message);
            }
        };

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
            next.startCall(wrappedCall, headers)) {
            @Override
            public void onHalfClose() {
                log.info("Client half-close for: {}", method);
                super.onHalfClose();
            }

            @Override
            public void onCancel() {
                log.info("Call cancelled: {}", method);
                super.onCancel();
            }
        };
    }
}
```

**Client Interceptor**:
```java
public class LoggingClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        log.info("Sending request: {}", method.getFullMethodName());
        return new ForwardingClientCall.SimpleForwardingClientCall<>(
            next.newCall(method, callOptions)) {
            @Override
            public void sendMessage(ReqT message) {
                log.info("Message: {}", message);
                super.sendMessage(message);
            }
        };
    }
}
```

### Deadlines

Deadlines allow clients to specify a maximum time to wait for an RPC to complete.

```java
// With a timeout
stub.withDeadlineAfter(5, TimeUnit.SECONDS).getProduct(request);

// With a fixed deadline
stub.withDeadline(Deadline.after(5, TimeUnit.SECONDS)).getProduct(request);

// Propagation
stub.withDeadline(Deadline.after(5, TimeUnit.SECONDS)
    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))); // tighter
```

**Server-side checks**:
```java
public void getProduct(GetProductRequest request, StreamObserver<Product> observer) {
    if (Context.current().getDeadline() != null
            && Context.current().getDeadline().isExpired()) {
        observer.onError(Status.DEADLINE_EXCEEDED.asRuntimeException());
        return;
    }
    // Process...
}
```

### Cancellation

**Client-side cancellation**:
```java
CancellableContext ctx = Context.current().withCancellation();
ctx.run(() -> {
    stub.getProduct(request);
});
ctx.cancel(new RuntimeException("Cancelled by client"));
```

**Server-side detection**:
```java
Context.current().addListener(context -> {
    log.info("Client cancelled the call");
}, Runnable::run);
```

---

## Load Balancing & Connection Management

### Client-Side Load Balancing

gRPC provides built-in client-side load balancing:

```java
ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8080, ...)
    .defaultLoadBalancingPolicy("round_robin")
    .build();
```

**Policies**:
- `pick_first` (default) — connects to first address, falls to next on failure
- `round_robin` — distributes across all addresses
- `grpclb` — uses a gRPC load balancer (external)
- Custom policies

### Name Resolver

```java
public class CustomNameResolverProvider extends NameResolverProvider {
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        return new NameResolver() {
            @Override
            public String getServiceAuthority() {
                return targetUri.getAuthority();
            }

            @Override
            public void start(Listener listener) {
                // Resolve addresses from service discovery
                List<EquivalentAddressGroup> addresses = resolve();
                listener.onAddresses(addresses, Attributes.EMPTY);
            }

            @Override
            public void shutdown() {
                // Cleanup
            }
        };
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    public int priority() {
        return 5;
    }
}
```

### Retry Policy

```java
// Via ServiceConfig JSON
String serviceConfig = "{\n" +
    "  \"methodConfig\": [{\n" +
    "    \"name\": [{\"service\": \"ecommerce.ProductService\"}],\n" +
    "    \"retryPolicy\": {\n" +
    "      \"maxAttempts\": 3,\n" +
    "      \"initialBackoff\": \"0.1s\",\n" +
    "      \"maxBackoff\": \"5s\",\n" +
    "      \"backoffMultiplier\": 2.0,\n" +
    "      \"retryableStatusCodes\": [\"UNAVAILABLE\"]\n" +
    "    }\n" +
    "  }]\n" +
    "}";

ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8080, ...)
    .defaultServiceConfig(GsonJsonParser.parse(serviceConfig))
    .enableRetry()
    .build();
```

### Keepalive

```java
ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8080, ...)
    .keepAliveTime(60, TimeUnit.SECONDS)
    .keepAliveTimeout(20, TimeUnit.SECONDS)
    .keepAliveWithoutCalls(true)
    .build();
```

**Server-side keepalive**:
```java
Server server = ServerBuilder.forPort(8080)
    .keepAliveTime(60, TimeUnit.SECONDS)
    .keepAliveTimeout(20, TimeUnit.SECONDS)
    .permitKeepAliveTime(30, TimeUnit.SECONDS)
    .permitKeepAliveWithoutCalls(true)
    .addService(new ProductServiceImpl())
    .build();
```

---

## Security, TLS, Authentication

### TLS Configuration

**Server-side**:
```java
Server server = ServerBuilder.forPort(8443)
    .useTransportSecurity(
        new File("server.crt"),  // Certificate chain
        new File("server.pem")   // Private key
    )
    .addService(new ProductServiceImpl())
    .build();
```

**Client-side**:
```java
ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8443, ...)
    .useTransportSecurity()
    .build();
```

**Mutual TLS (mTLS)**:
```java
// Server
Server server = Grpc.newServerBuilderForPort(8443, ServerCredentials)
    .tlsBuilder(
        tlsBuilder -> tlsBuilder
            .keyManager(new File("server.crt"), new File("server.pem"))
            .trustManager(new File("ca.crt"))
            .clientAuth(ClientAuth.REQUIRE)
    )
    .addService(new ProductServiceImpl())
    .build();

// Client
ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8443, ...)
    .tlsBuilder(
        tlsBuilder -> tlsBuilder
            .keyManager(new File("client.crt"), new File("client.pem"))
            .trustManager(new File("ca.crt"))
    )
    .build();
```

### Authentication Interceptors

**JWT Token on Client**:
```java
public class JwtClientInterceptor implements ClientInterceptor {
    private final String token;

    public JwtClientInterceptor(String token) {
        this.token = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(Metadata.Key.of("authorization",
                    Metadata.ASCII_STRING_MARSHALLER), "Bearer " + token);
                super.start(responseListener, headers);
            }
        };
    }
}
```

**JWT Validation on Server**:
```java
public class JwtServerInterceptor implements ServerInterceptor {
    private final JwtValidator jwtValidator;

    public JwtServerInterceptor(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.close(Status.UNAUTHENTICATED
                .withDescription("Missing or invalid authorization header"),
                new Metadata());
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(7);
        try {
            JwtPrincipal principal = jwtValidator.validateToken(token);
            Context context = Context.current()
                .withValue(Constants.AUTH_CONTEXT_KEY, principal);
            return Contexts.interceptCall(context, call, headers, next);
        } catch (Exception e) {
            call.close(Status.UNAUTHENTICATED
                .withDescription("Token validation failed"), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }
}
```

### OAuth2

```java
// Client with OAuth2
ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 443, ...)
    .callCredentials(new OAuth2CallCredentials(
        new ClientCredentialsGrantHandler(
            ClientCredentialsGrant.builder()
                .tokenEndpoint("https://auth.example.com/token")
                .clientId("client-id")
                .clientSecret("client-secret")
                .build()
        )
    ))
    .build();
```

---

## Java Code Examples

### 1. Complete gRPC Service Implementation

```proto
// product_service.proto
syntax = "proto3";

package ecommerce;

option java_package = "com.example.grpc";
option java_multiple_files = true;

service ProductService {
    rpc GetProduct(GetProductRequest) returns (Product);
    rpc ListProducts(ListProductsRequest) returns (stream Product);
    rpc CreateProduct(CreateProductRequest) returns (Product);
    rpc SearchProducts(stream SearchRequest) returns (stream SearchResult);
}

message GetProductRequest {
    string product_id = 1;
}

message ListProductsRequest {
    int32 page_size = 1;
    string page_token = 2;
}

message Product {
    string id = 1;
    string name = 2;
    double price = 3;
    string category = 4;
    bool available = 5;
}

message CreateProductRequest {
    string name = 1;
    double price = 2;
    string category = 3;
}

message SearchRequest {
    string query = 1;
    int32 limit = 2;
}

message SearchResult {
    Product product = 1;
    double score = 2;
}
```

```java
// ProductServiceImpl.java
package com.example.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final Map<String, Product> productStore = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<Product> observer) {
        log.info("getProduct called for ID: {}", request.getProductId());

        if (request.getProductId().isBlank()) {
            observer.onError(Status.INVALID_ARGUMENT
                .withDescription("Product ID cannot be empty")
                .asRuntimeException());
            return;
        }

        Product product = productStore.get(request.getProductId());
        if (product == null) {
            observer.onError(Status.NOT_FOUND
                .withDescription("Product not found: " + request.getProductId())
                .asRuntimeException());
            return;
        }

        observer.onNext(product);
        observer.onCompleted();
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<Product> observer) {
        log.info("listProducts called with pageSize={}, pageToken={}",
            request.getPageSize(), request.getPageToken());

        List<Product> allProducts = List.copyOf(productStore.values());

        int pageSize = request.getPageSize() > 0
            ? Math.min(request.getPageSize(), 100) : 10;
        int offset = 0;
        if (!request.getPageToken().isBlank()) {
            try {
                offset = Integer.parseInt(request.getPageToken());
            } catch (NumberFormatException e) {
                observer.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid page token")
                    .asRuntimeException());
                return;
            }
        }

        allProducts.stream()
            .skip(offset)
            .limit(pageSize)
            .forEach(observer::onNext);

        observer.onCompleted();
    }

    @Override
    public StreamObserver<SearchRequest> searchProducts(
            StreamObserver<SearchResult> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(SearchRequest request) {
                log.info("Received search query: {}", request.getQuery());

                String query = request.getQuery().toLowerCase();
                int limit = request.getLimit() > 0 ? request.getLimit() : 10;

                productStore.values().stream()
                    .filter(p -> p.getName().toLowerCase().contains(query))
                    .limit(limit)
                    .forEach(product -> {
                        double score = computeRelevance(product, query);
                        responseObserver.onNext(SearchResult.newBuilder()
                            .setProduct(product)
                            .setScore(score)
                            .build());
                    });
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error in search stream", t);
            }

            @Override
            public void onCompleted() {
                log.info("Search stream completed");
                responseObserver.onCompleted();
            }
        };
    }

    private double computeRelevance(Product product, String query) {
        double score = 0.0;
        String name = product.getName().toLowerCase();
        if (name.equals(query)) score += 10.0;
        else if (name.startsWith(query)) score += 5.0;
        else if (name.contains(query)) score += 2.0;
        return score;
    }
}
```

```java
// GrpcServer.java
package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class GrpcServer {

    private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

    private final int port;
    private final Server server;

    public GrpcServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
            .addService(ServerInterceptors.intercept(
                new ProductServiceImpl(),
                new LoggingServerInterceptor(),
                new JwtServerInterceptor(new JwtValidator("secret-key"))
            ))
            .keepAliveTime(60, TimeUnit.SECONDS)
            .keepAliveTimeout(20, TimeUnit.SECONDS)
            .permitKeepAliveTime(30, TimeUnit.SECONDS)
            .maxInboundMessageSize(4 * 1024 * 1024) // 4MB
            .build();
    }

    public void start() throws Exception {
        server.start();
        log.info("gRPC server started on port {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gRPC server");
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {
        new GrpcServer(8080).start();
    }
}
```

### 2. gRPC Client with Deadline and Retry

```java
package com.example.grpc.client;

import com.example.grpc.ProductServiceGrpc;
import com.example.grpc.ProductServiceGrpc.ProductServiceBlockingStub;
import com.example.grpc.GetProductRequest;
import com.example.grpc.Product;
import io.grpc.*;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class GrpcClient {

    private static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

    private final ManagedChannel channel;
    private final ProductServiceBlockingStub blockingStub;

    public GrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new JwtClientInterceptor("test-jwt-token"))
            .defaultLoadBalancingPolicy("round_robin")
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(10, TimeUnit.SECONDS)
            .enableRetry()
            .maxRetryAttempts(3)
            .build();

        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);
    }

    public Product getProduct(String productId) {
        var request = GetProductRequest.newBuilder()
            .setProductId(productId)
            .build();

        try {
            Product product = blockingStub.getProduct(request);
            log.info("Got product: {}", product);
            return product;
        } catch (StatusRuntimeException e) {
            Status status = e.getStatus();
            log.error("gRPC call failed with code={}, description={}",
                status.getCode(), status.getDescription());
            throw new GrpcClientException("Failed to get product", e);
        }
    }

    public void shutdown() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            channel.shutdownNow();
        }
    }

    // Async stub example
    public void getProductAsync(String productId) {
        var futureStub = ProductServiceGrpc.newFutureStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);

        var request = GetProductRequest.newBuilder()
            .setProductId(productId)
            .build();

        futureStub.getProduct(request)
            .thenAccept(product ->
                log.info("Async result: {}", product))
            .exceptionally(throwable -> {
                log.error("Async call failed", throwable);
                return null;
            });
    }

    public static void main(String[] args) {
        GrpcClient client = new GrpcClient("localhost", 8080);

        try {
            // Success case
            client.getProduct("product-123");
        } catch (GrpcClientException e) {
            log.error("Client error", e);
        } finally {
            client.shutdown();
        }
    }
}

class GrpcClientException extends RuntimeException {
    public GrpcClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 3. Interceptor for Correlation ID Propagation

```java
// ContextKeys.java
package com.example.grpc.context;

import io.grpc.Context;

public class ContextKeys {
    public static final Context.Key<String> CORRELATION_ID =
        Context.key("correlation-id");
    public static final Context.Key<String> USER_ID =
        Context.key("user-id");
}
```

```java
// CorrelationIdServerInterceptor.java
package com.example.grpc.interceptor;

import com.example.grpc.context.ContextKeys;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationIdServerInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdServerInterceptor.class);

    private static final Metadata.Key<String> CORRELATION_ID_KEY =
        Metadata.Key.of("x-correlation-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String correlationId = headers.get(CORRELATION_ID_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        Context context = Context.current()
            .withValue(ContextKeys.CORRELATION_ID, correlationId);

        MDC.put("correlationId", correlationId);

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                responseHeaders.put(CORRELATION_ID_KEY, correlationId);
                super.sendHeaders(responseHeaders);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                MDC.clear();
                super.close(status, trailers);
            }
        };

        return Contexts.interceptCall(context, wrappedCall, headers, next);
    }
}
```

```java
// CorrelationIdClientInterceptor.java
package com.example.grpc.interceptor;

import com.example.grpc.context.ContextKeys;
import io.grpc.*;
import org.slf4j.MDC;

public class CorrelationIdClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> CORRELATION_ID_KEY =
        Metadata.Key.of("x-correlation-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String correlationId = ContextKeys.CORRELATION_ID.get();
                if (correlationId == null) {
                    correlationId = MDC.get("correlationId");
                }
                if (correlationId != null) {
                    headers.put(CORRELATION_ID_KEY, correlationId);
                }
                super.start(responseListener, headers);
            }

            @Override
            public void onMessage(RespT message) {
                super.onMessage(message);
            }
        };
    }
}
```

### 4. Bidirectional Streaming — Chat Service

```protobuf
// chat.proto
syntax = "proto3";

package chat;

option java_package = "com.example.chat";

service ChatService {
    rpc Chat(stream ChatMessage) returns (stream ChatMessage);
}

message ChatMessage {
    string user_id = 1;
    string room_id = 2;
    string text = 3;
    int64 timestamp = 4;
}
```

```java
// ChatServiceImpl.java
package com.example.chat;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final Map<String, CopyOnWriteArrayList<StreamObserver<ChatMessage>>>
        rooms = new ConcurrentHashMap<>();

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<>() {
            private String currentRoom = "";
            private String currentUser = "";

            @Override
            public void onNext(ChatMessage message) {
                String roomId = message.getRoomId();
                String userId = message.getUserId();

                if (!currentRoom.equals(roomId)) {
                    // Leave previous room, join new room
                    if (!currentRoom.isEmpty()) {
                        rooms.getOrDefault(currentRoom, new CopyOnWriteArrayList<>())
                            .remove(responseObserver);
                    }
                    rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>())
                        .add(responseObserver);
                    currentRoom = roomId;
                    currentUser = userId;
                }

                log.info("[{}][{}]: {}", roomId, userId, message.getText());

                // Broadcast to all users in the room
                rooms.getOrDefault(roomId, new CopyOnWriteArrayList<>())
                    .forEach(observer -> {
                        if (observer != responseObserver) {
                            try {
                                observer.onNext(message);
                            } catch (Exception e) {
                                log.error("Failed to send message to user", e);
                            }
                        }
                    });
            }

            @Override
            public void onError(Throwable t) {
                log.error("Chat error for user {} in room {}", currentUser, currentRoom, t);
                leaveRoom();
            }

            @Override
            public void onCompleted() {
                log.info("User {} left room {}", currentUser, currentRoom);
                leaveRoom();
                responseObserver.onCompleted();
            }

            private void leaveRoom() {
                if (!currentRoom.isEmpty()) {
                    rooms.getOrDefault(currentRoom, new CopyOnWriteArrayList<>())
                        .remove(responseObserver);
                }
            }
        };
    }
}
```

### 5. Server Reflection and Health Checking

```java
// Server with reflection and health
package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservabilityServer {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityServer.class);

    private final Server server;
    private final HealthStatusManager healthManager;

    public ObservabilityServer(int port) {
        this.healthManager = new HealthStatusManager();

        this.server = ServerBuilder.forPort(port)
            .addService(new ProductServiceImpl())
            .addService(healthManager.getHealthService())
            .addService(ProtoReflectionService.newInstance())
            .build();
    }

    public void start() throws Exception {
        server.start();
        healthManager.setStatus(
            HealthStatusManager.SERVICE_NAME_ALL_SERVICES,
            HealthCheckResponse.ServingStatus.SERVING);
        log.info("Server started on port {}", server.getPort());
    }

    public void markNotServing() {
        healthManager.setStatus(
            HealthStatusManager.SERVICE_NAME_ALL_SERVICES,
            HealthCheckResponse.ServingStatus.NOT_SERVING);
    }

    public void shutdown() {
        server.shutdown();
    }
}
```

---

## 15+ Interview Questions

### Q1: How does gRPC achieve better performance than REST?
**Answer**: gRPC uses several performance optimizations: (1) **HTTP/2** — multiplexed streams over a single TCP connection, avoiding head-of-line blocking; (2) **Protocol Buffers** — binary serialization is ~8x faster and produces ~5x smaller payloads than JSON; (3) **HPACK header compression** — reduces overhead from repeated headers; (4) **Streaming** — eliminates request/response overhead per message; (5) **Strong typing** — no runtime parsing ambiguity.

### Q2: Explain the gRPC lifecycle for a unary RPC.
**Answer**: (1) Client creates a channel to the server; (2) The stub marshals the request message to protobuf bytes; (3) Client sends an HTTP/2 POST request with `Content-Type: application/grpc`; (4) Server unmarshals, processes, and marshals the response; (5) Server sends the response followed by HTTP/2 trailers containing the gRPC status code; (6) Client checks the status and returns the result or error.

### Q3: What are gRPC deadlines and how do they differ from HTTP timeouts?
**Answer**: Deadlines are client-specified time limits for the entire RPC lifecycle (including retries), not just a single request. They propagate across service calls in a distributed system, allowing the original caller's deadline to be respected by downstream services. If a deadline expires, the client receives a `DEADLINE_EXCEEDED` error. Unlike HTTP timeouts that are connection-level, gRPC deadlines are per-RPC and can be propagated via `Context`.

### Q4: How does gRPC handle streaming flow control?
**Answer**: gRPC inherits HTTP/2's built-in flow control. HTTP/2 uses **stream-level and connection-level flow control** via WINDOW_UPDATE frames. The receiver controls how much data the sender can send by advertising a window size. This prevents a fast sender from overwhelming a slow receiver. gRPC's server can also use `onReady()` callback when the stream is ready for more messages.

### Q5: What is the difference between gRPC and gRPC-Web?
**Answer**: gRPC-Web is a variant for browser clients. Browsers can't control HTTP/2 trailers, so gRPC-Web moves status codes and metadata into the response body or headers. gRPC-Web requires a proxy (Envoy, gRPC-Web proxy) to convert between gRPC-Web and standard gRPC. It supports unary and server-streaming only (no client-streaming or bidirectional streaming).

### Q6: Explain gRPC's channel and stub architecture.
**Answer**: A **Channel** represents a connection to a gRPC server (host + port, connection pooling, load balancing). A **Stub** is a client-side wrapper generated from the service definition. Types: **BlockingStub** (blocking calls), **FutureStub** (ListenableFuture-based async), **Stub** (StreamObserver-based async). Channels can be shared across stubs and safely reused. Channels handle connection management, retry, and load balancing.

### Q7: How do you implement authentication in gRPC?
**Answer**: Two main approaches: (1) **Credential-based** — `CallCredentials` attached to the channel or call, which add authorization metadata (JWT, OAuth2 tokens) to each RPC; (2) **Interceptor-based** — client interceptors add auth headers, server interceptors validate them. For TLS: use `useTransportSecurity()` on server and `useTransportSecurity()` on client. For mTLS: both sides present certificates.

### Q8: What error codes does gRPC define and when are they used?
**Answer**: gRPC defines a set of canonical status codes transmitted as HTTP/2 trailers: `OK (0)`, `CANCELLED (1)`, `UNKNOWN (2)`, `INVALID_ARGUMENT (3)`, `DEADLINE_EXCEEDED (4)`, `NOT_FOUND (5)`, `ALREADY_EXISTS (6)`, `PERMISSION_DENIED (7)`, `RESOURCE_EXHAUSTED (8)`, `FAILED_PRECONDITION (9)`, `ABORTED (10)`, `OUT_OF_RANGE (11)`, `UNIMPLEMENTED (12)`, `INTERNAL (13)`, `UNAVAILABLE (14)`, `DATA_LOSS (15)`, `UNAUTHENTICATED (16)`.

### Q9: Describe gRPC context propagation.
**Answer**: gRPC uses `io.grpc.Context` — a thread-local scoped context that can carry values across asynchronous boundaries. `Context` is linked to each incoming RPC and can propagate: deadlines, cancellation signals, authentication principals, correlation IDs. `Contexts.interceptCall()` weaves context into the call chain. Context propagates naturally across gRPC calls within the same JVM, and can be sent to downstream services via metadata headers.

### Q10: How do you handle large messages in gRPC?
**Answer**: Configure `maxInboundMessageSize()` on both server and client (default 4MB). For files: use streaming (client-streaming for upload, server-streaming for download). For very large payloads (>100MB): consider chunking the data into smaller messages or sending a reference (URL, S3 key) and having the client fetch it separately. Use `setOnReady()` handler for backpressure-aware streaming.

### Q11: Explain the name resolver and load balancing architecture.
**Answer**: **Name Resolver**: converts a target URI to a list of server addresses. The default resolver uses DNS. Custom resolvers can integrate with service discovery (Consul, Eureka, Kubernetes). **Load Balancer**: picks a server from the resolved list for each RPC. `pick_first` uses the first available address; `round_robin` distributes across all addresses. **Subchannel**: represents a connection to a single server. The load balancer decides which subchannel to use.

### Q12: What is the retry policy in gRPC and how does it work?
**Answer**: gRPC supports configurable retry via ServiceConfig JSON. The retry policy specifies: `maxAttempts`, `initialBackoff`, `maxBackoff`, `backoffMultiplier`, and `retryableStatusCodes`. Retries happen transparently at the client level. The client re-creates the RPC with the same deadline. Only idempotent RPCs should be retried (safe methods). `UNAVAILABLE` is the most common retryable status.

### Q13: How does gRPC handle connection failures and reconnection?
**Answer**: gRPC maintains subchannels — connections to individual servers. If a connection fails, it enters `TRANSIENT_FAILURE` state and the load balancer routes requests to other subchannels. The channel periodically attempts reconnection with exponential backoff (1s initial, 120s max, jitter). The channel becomes `IDLE` after a period of inactivity and reconnects on the next RPC.

### Q14: Explain the role of envoy in gRPC service meshes.
**Answer**: Envoy acts as a **sidecar proxy** in service meshes. For gRPC: (1) Terminates TLS/mTLS; (2) Provides advanced load balancing (ring hash, least request, Maglev); (3) Implements circuit breaking, retry budgets; (4) Provides observability (metrics, tracing, access logs); (5) Handles gRPC-Web translation; (6) Enables fault injection and traffic splitting for testing. Envoy understands HTTP/2 natively and can be configured via xDS protocol.

### Q15: How do you migrate from REST to gRPC?
**Answer**: (1) Define `.proto` files matching existing REST endpoints; (2) Run both REST and gRPC servers side-by-side behind a reverse proxy (Envoy, Nginx); (3) Use a gateway (grpc-gateway, Envoy gRPC-JSON transcoder) to expose gRPC services as REST/JSON for clients that can't use gRPC; (4) Gradually migrate clients from REST to gRPC stubs; (5) Once fully migrated, deprecate the REST endpoints and gateway.

### Q16: What are the limitations of gRPC?
**Answer**: (1) **Browser support** — requires gRPC-Web with a proxy; (2) **Protobuf debuggability** — binary format is less human-readable than JSON; (3) **Limited caching** — HTTP/2 makes response caching harder; (4) **Load balancing** — requires client-side LB or proxy (Envoy); (5) **Code generation** — adds build complexity; (6) **Streaming resource management** — long-lived streams require careful connection management; (7) **Larger initial footprint** compared to simple HTTP clients.

### Q17: How do you implement distributed tracing in gRPC?
**Answer**: (1) Use **OpenTelemetry** gRPC instrumentation (interceptors); (2) The OpenTelemetry gRPC plugin automatically creates spans for each RPC; (3) **Context propagation** carries trace context (trace ID, span ID) across service boundaries via gRPC metadata; (4) Configure exporters (Jaeger, Zipkin, OTLP) to collect traces; (5) Use **attributes** to enrich spans (method name, status code, latency); (6) For streaming RPCs, spans cover the entire stream lifecycle.

### Q18: Explain gRPC's flow control mechanism in detail.
**Answer**: gRPC relies on HTTP/2 flow control. Each stream starts with an initial window size (default 65,535 bytes). The receiver sends `WINDOW_UPDATE` frames to increase the window. The `SETTINGS` frame can set the initial window size for all streams (connection-level). Server can use `FlowControlHandler` for custom flow control. gRPC also provides StreamObserver's `onReady()` callback — called when the stream's outbound buffer is ready to accept more messages. This enables application-level backpressure.

### Q19: What is the difference between interceptors and call credentials?
**Answer**: **Interceptors** are general-purpose middleware — they can modify requests/responses, add headers, log, monitor, authenticate, and more. They run on both client and server side. **CallCredentials** are specifically for authentication — they attach credentials (tokens, OAuth assertions) to each RPC call. CallCredentials can be composed with interceptors. Typically, you use `CallCredentials` for auth and interceptors for logging, metrics, and tracing.

### Q20: How does gRPC compare with Apache Thrift?
**Answer**: Both are RPC frameworks with IDL and code generation. **gRPC advantages**: HTTP/2 (multiplexing, streaming, standard protocol), larger ecosystem (Envoy, gRPC-Web, OpenTelemetry), stronger community. **Thrift advantages**: more transport options (TCP, HTTP), more flexible serialization, better support for non-HTTP environments. For microservices, gRPC is more popular. For internal high-performance services, both are good choices.
