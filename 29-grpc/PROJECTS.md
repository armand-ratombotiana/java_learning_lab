# gRPC Module - PROJECTS.md

---

# Mini-Project: gRPC Service Implementation

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Protocol Buffers, gRPC Services, Unary/Server Streaming, Client Streaming, Bidirectional Streaming

This mini-project demonstrates gRPC with Protocol Buffers for high-performance inter-service communication. You'll implement a complete gRPC service for a todo application.

---

## Project Structure

```
29-grpc/
├── pom.xml
├── src/main/proto/
│   └── todo.proto
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── service/
│   │   └── TodoServiceImpl.java
│   ├── client/
│   │   └── TodoClient.java
│   └── server/
│       └── GrpcServer.java
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>grpc-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <grpc.version>1.60.0</grpc.version>
        <protobuf.version>3.25.1</protobuf.version>
        <maven.compiler.source>17</maven.compiler.source>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-api</artifactId>
            <version>${grpc.version}</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>${grpc.version}</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>${grpc.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>${protobuf.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java-util</artifactId>
            <version>${protobuf.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocExecutable>protoc</protocExecutable>
                    <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>java</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Step 2: Protocol Buffers Definition

```protobuf
// src/main/proto/todo.proto
syntax = "proto3";

option java_package = "com.learning.todo";
option java_multiple_files = true;

package todo;

service TodoService {
    // Unary RPC - Create a todo
    rpc CreateTodo(CreateTodoRequest) returns (Todo);
    
    // Unary RPC - Get a todo
    rpc GetTodo(GetTodoRequest) returns (Todo);
    
    // Unary RPC - List all todos
    rpc ListTodos(ListTodosRequest) returns (TodoList);
    
    // Unary RPC - Update a todo
    rpc UpdateTodo(UpdateTodoRequest) returns (Todo);
    
    // Unary RPC - Delete a todo
    rpc DeleteTodo(DeleteTodoRequest) returns (DeleteResponse);
    
    // Server streaming - Watch todos
    rpc WatchTodos(WatchRequest) returns (stream Todo);
    
    // Client streaming - Create multiple todos
    rpc CreateTodosBatch(stream CreateTodoRequest) returns (TodoList);
    
    // Bidirectional streaming - Chat-like functionality
    rpc Chat(stream ChatMessage) returns (stream ChatMessage);
}

message Todo {
    string id = 1;
    string title = 2;
    string description = 3;
    bool completed = 4;
    int32 priority = 5;
    string created_at = 6;
    string updated_at = 7;
}

message CreateTodoRequest {
    string title = 1;
    string description = 2;
    int32 priority = 3;
}

message GetTodoRequest {
    string id = 1;
}

message ListTodosRequest {
    bool include_completed = 1;
}

message UpdateTodoRequest {
    string id = 1;
    string title = 2;
    string description = 3;
    bool completed = 4;
    int32 priority = 5;
}

message DeleteTodoRequest {
    string id = 1;
}

message DeleteResponse {
    bool success = 1;
}

message TodoList {
    repeated Todo todos = 1;
}

message WatchRequest {
    bool include_completed = 1;
}

message ChatMessage {
    string id = 1;
    string message = 2;
    string sender = 3;
    string timestamp = 4;
}
```

---

## Step 3: gRPC Service Implementation

```java
// service/TodoServiceImpl.java
package com.learning.service;

import com.learning.todo.*;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TodoServiceImpl extends TodoServiceGrpc.TodoServiceImplBase {
    
    private final Map<String, Todo> todos = new ConcurrentHashMap<>();
    private final List<StreamObserver<Todo>> watchers = new CopyOnWriteArrayList<>();
    
    @Override
    public void createTodo(CreateTodoRequest request, StreamObserver<Todo> responseObserver) {
        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();
        
        Todo todo = Todo.newBuilder()
            .setId(id)
            .setTitle(request.getTitle())
            .setDescription(request.getDescription())
            .setPriority(request.getPriority())
            .setCompleted(false)
            .setCreatedAt(now)
            .setUpdatedAt(now)
            .build();
        
        todos.put(id, todo);
        
        responseObserver.onNext(todo);
        responseObserver.onCompleted();
    }
    
    @Override
    public void getTodo(GetTodoRequest request, StreamObserver<Todo> responseObserver) {
        Todo todo = todos.get(request.getId());
        
        if (todo != null) {
            responseObserver.onNext(todo);
        } else {
            responseObserver.onError(
                io.grpc.Status.NOT_FOUND.withDescription("Todo not found").asRuntimeException()
            );
            return;
        }
        
        responseObserver.onCompleted();
    }
    
    @Override
    public void listTodos(ListTodosRequest request, StreamObserver<TodoList> responseObserver) {
        List<Todo> todoList = todos.values().stream()
            .filter(todo -> request.getIncludeCompleted() || !todo.getCompleted())
            .toList();
        
        responseObserver.onNext(TodoList.newBuilder()
            .addAllTodos(todoList)
            .build());
        
        responseObserver.onCompleted();
    }
    
    @Override
    public void updateTodo(UpdateTodoRequest request, StreamObserver<Todo> responseObserver) {
        Todo existing = todos.get(request.getId());
        
        if (existing == null) {
            responseObserver.onError(
                io.grpc.Status.NOT_FOUND.withDescription("Todo not found").asRuntimeException()
            );
            return;
        }
        
        Todo updated = Todo.newBuilder()
            .setId(request.getId())
            .setTitle(request.getTitle())
            .setDescription(request.getDescription())
            .setCompleted(request.getCompleted())
            .setPriority(request.getPriority())
            .setCreatedAt(existing.getCreatedAt())
            .setUpdatedAt(Instant.now().toString())
            .build();
        
        todos.put(request.getId(), updated);
        
        // Notify watchers
        for (StreamObserver<Todo> watcher : watchers) {
            watcher.onNext(updated);
        }
        
        responseObserver.onNext(updated);
        responseObserver.onCompleted();
    }
    
    @Override
    public void deleteTodo(DeleteTodoRequest request, StreamObserver<DeleteResponse> responseObserver) {
        boolean removed = todos.remove(request.getId()) != null;
        
        responseObserver.onNext(DeleteResponse.newBuilder()
            .setSuccess(removed)
            .build());
        
        responseObserver.onCompleted();
    }
    
    @Override
    public StreamObserver<Todo> watchTodos(WatchRequest request, StreamObserver<Todo> responseObserver) {
        watchers.add(responseObserver);
        
        // Send initial todos
        todos.values().stream()
            .filter(todo -> request.getIncludeCompleted() || !todo.getCompleted())
            .forEach(responseObserver::onNext);
        
        responseObserver.onCompleted();
        
        return new StreamObserver<Todo>() {
            @Override
            public void onNext(Todo value) {}
            
            @Override
            public void onError(Throwable t) {
                watchers.remove(responseObserver);
            }
            
            @Override
            public void onCompleted() {
                watchers.remove(responseObserver);
            }
        };
    }
    
    @Override
    public StreamObserver<CreateTodoRequest> createTodosBatch(
            StreamObserver<TodoList> responseObserver) {
        
        List<Todo> created = new ArrayList<>();
        
        return new StreamObserver<CreateTodoRequest>() {
            @Override
            public void onNext(CreateTodoRequest request) {
                String id = UUID.randomUUID().toString();
                String now = Instant.now().toString();
                
                Todo todo = Todo.newBuilder()
                    .setId(id)
                    .setTitle(request.getTitle())
                    .setDescription(request.getDescription())
                    .setPriority(request.getPriority())
                    .setCompleted(false)
                    .setCreatedAt(now)
                    .setUpdatedAt(now)
                    .build();
                
                todos.put(id, todo);
                created.add(todo);
            }
            
            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }
            
            @Override
            public void onCompleted() {
                responseObserver.onNext(TodoList.newBuilder()
                    .addAllTodos(created)
                    .build());
                responseObserver.onCompleted();
            }
        };
    }
    
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                String timestamp = Instant.now().toString();
                ChatMessage response = ChatMessage.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setMessage("Echo: " + message.getMessage())
                    .setSender("Server")
                    .setTimestamp(timestamp)
                    .build();
                
                responseObserver.onNext(response);
            }
            
            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }
            
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
```

---

## Step 4: gRPC Server

```java
// server/GrpcServer.java
package com.learning.server;

import com.learning.service.TodoServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class GrpcServer {
    
    private final Server server;
    
    public GrpcServer(int port) {
        this.server = ServerBuilder.forPort(port)
            .addService(new TodoServiceImpl())
            .build();
    }
    
    public void start() throws IOException {
        server.start();
        System.out.println("gRPC Server started on port " + server.getPort());
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            server.shutdown();
        }));
    }
    
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServer server = new GrpcServer(8080);
        server.start();
        server.blockUntilShutdown();
    }
}
```

---

## Step 5: gRPC Client

```java
// client/TodoClient.java
package com.learning.client;

import com.learning.todo.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class TodoClient {
    
    private final ManagedChannel channel;
    private final TodoServiceGrpc.TodoServiceBlockingStub blockingStub;
    private final TodoServiceGrpc.TodoServiceStub asyncStub;
    
    public TodoClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
        
        this.blockingStub = TodoServiceGrpc.newBlockingStub(channel);
        this.asyncStub = TodoServiceGrpc.newStub(channel);
    }
    
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    // Create a todo
    public Todo createTodo(String title, String description, int priority) {
        CreateTodoRequest request = CreateTodoRequest.newBuilder()
            .setTitle(title)
            .setDescription(description)
            .setPriority(priority)
            .build();
        
        return blockingStub.createTodo(request);
    }
    
    // Get a todo
    public Todo getTodo(String id) {
        GetTodoRequest request = GetTodoRequest.newBuilder()
            .setId(id)
            .build();
        
        return blockingStub.getTodo(request);
    }
    
    // List all todos
    public TodoList listTodos() {
        ListTodosRequest request = ListTodosRequest.newBuilder()
            .setIncludeCompleted(true)
            .build();
        
        return blockingStub.listTodos(request);
    }
    
    // Update a todo
    public Todo updateTodo(String id, String title, boolean completed) {
        UpdateTodoRequest request = UpdateTodoRequest.newBuilder()
            .setId(id)
            .setTitle(title)
            .setCompleted(completed)
            .build();
        
        return blockingStub.updateTodo(request);
    }
    
    // Delete a todo
    public boolean deleteTodo(String id) {
        DeleteTodoRequest request = DeleteTodoRequest.newBuilder()
            .setId(id)
            .build();
        
        return blockingStub.deleteTodo(request).getSuccess();
    }
    
    // Server streaming - Watch todos
    public void watchTodos() {
        WatchRequest request = WatchRequest.newBuilder()
            .setIncludeCompleted(true)
            .build();
        
        asyncStub.watchTodos(request, new StreamObserver<Todo>() {
            @Override
            public void onNext(Todo todo) {
                System.out.println("Watch update: " + todo.getTitle() + " - " + todo.getCompleted());
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("Watch error: " + t.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("Watch completed");
            }
        });
    }
    
    // Client streaming - Batch create
    public void batchCreateTodos() {
        final String[] titles = {"Task 1", "Task 2", "Task 3"};
        final int[] count = {0};
        
        StreamObserver<TodoList> responseObserver = new StreamObserver<TodoList>() {
            @Override
            public void onNext(TodoList list) {
                System.out.println("Created " + list.getTodosList().size() + " todos");
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("Batch error: " + t.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("Batch completed");
            }
        };
        
        StreamObserver<CreateTodoRequest> requestObserver = asyncStub.createTodosBatch(responseObserver);
        
        for (String title : titles) {
            CreateTodoRequest request = CreateTodoRequest.newBuilder()
                .setTitle(title)
                .setPriority(count[0]++)
                .build();
            
            requestObserver.onNext(request);
        }
        
        requestObserver.onCompleted();
    }
    
    // Bidirectional streaming - Chat
    public void chat() {
        StreamObserver<ChatMessage> responseObserver = new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage msg) {
                System.out.println("[" + msg.getSender() + "] " + msg.getMessage());
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("Chat error: " + t.getMessage());
            }
            
            @Override
            public void onCompleted() {
                System.out.println("Chat completed");
            }
        };
        
        StreamObserver<ChatMessage> requestObserver = asyncStub.chat(responseObserver);
        
        String[] messages = {"Hello", "How are you?", "Goodbye!"};
        
        for (String msg : messages) {
            ChatMessage request = ChatMessage.newBuilder()
                .setMessage(msg)
                .setSender("Client")
                .build();
            
            requestObserver.onNext(request);
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        requestObserver.onCompleted();
    }
    
    public static void main(String[] args) throws Exception {
        TodoClient client = new TodoClient("localhost", 8080);
        
        try {
            // Create a todo
            Todo todo = client.createTodo("Learn gRPC", "Study Protocol Buffers", 1);
            System.out.println("Created: " + todo.getId());
            
            // List todos
            TodoList list = client.listTodos();
            System.out.println("Total todos: " + list.getTodosList().size());
            
            // Update todo
            Todo updated = client.updateTodo(todo.getId(), "Learn gRPC updated", true);
            System.out.println("Updated: " + updated.getTitle());
            
            // Delete todo
            boolean deleted = client.deleteTodo(todo.getId());
            System.out.println("Deleted: " + deleted);
            
        } finally {
            client.shutdown();
        }
    }
}
```

---

## Build Instructions

```bash
cd 29-grpc

# Generate proto files
mvn protobuf:compile

# Compile
mvn clean compile

# Run server
mvn exec:java -Dexec.mainClass=com.learning.server.GrpcServer

# Run client (in another terminal)
mvn exec:java -Dexec.mainClass=com.learning.client.TodoClient

# Or use the client CLI
java -cp target/classes:target/dependency/* com.learning.client.TodoClient
```

---

# Real-World Project: gRPC Microservices Architecture

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: gRPC Interceptors, Security (TLS), Health Checks, Error Handling, Load Balancing

This comprehensive project implements a production-grade gRPC microservices architecture with health checks, security, interceptors, and proper error handling.

---

## Complete Implementation

```java
// Interceptors - Logging and Authentication
package com.learning.interceptor;

import io.grpc.*;
import java.util.logging.Logger;

public class LoggingInterceptor implements ServerInterceptor {
    
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, 
            Metadata headers, 
            ServerCallHandler<ReqT, RespT> next) {
        
        String methodName = call.getMethodDescriptor().getFullMethodName();
        logger.info("gRPC call: " + methodName);
        
        long startTime = System.nanoTime();
        
        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                long duration = System.nanoTime() - startTime;
                logger.info("gRPC " + methodName + " completed in " + duration + "ns with status: " + status);
                super.close(status, trailers);
            }
        };
        
        return next.startCall(wrappedCall, headers);
    }
}

public class AuthInterceptor implements ServerInterceptor {
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, 
            Metadata headers, 
            ServerCallHandler<ReqT, RespT> next) {
        
        Metadata.Key<String> authHeader = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
        String authToken = headers.get(authHeader);
        
        if (authToken == null || !validateToken(authToken)) {
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), headers);
            return new ServerCall.Listener<ReqT>() {};
        }
        
        return next.startCall(call, headers);
    }
    
    private boolean validateToken(String token) {
        return token.startsWith("Bearer ");
    }
}
```

---

## Health Check Service

```java
// Health Check Implementation
package com.learning.health

import io.grpc.health.v1.*;
import io.grpc.stub.StreamObserver;

public class HealthServiceImpl extends HealthGrpc.HealthImplBase {
    
    private volatile HealthCheckResponse.ServingStatus status = 
        HealthCheckResponse.ServingStatus.SERVING;
    
    @Override
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse response = HealthCheckResponse.newBuilder()
            .setStatus(status)
            .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void watch(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        // Send initial status
        responseObserver.onNext(HealthCheckResponse.newBuilder()
            .setStatus(status)
            .build());
        
        // In a real implementation, watch for status changes
    }
    
    public void setServingStatus(HealthCheckResponse.ServingStatus status) {
        this.status = status;
    }
}
```

---

## Advanced Error Handling

```java
// gRPC Error Utilities
package com.learning.error

import io.grpc.*;
import java.util.Optional;

public class GrpcExceptionHandler {
    
    public static StatusRuntimeException notFound(String resource) {
        return Status.NOT_FOUND.withDescription(resource + " not found").asRuntimeException();
    }
    
    public static StatusRuntimeException invalidArgument(String field, String message) {
        return Status.INVALID_ARGUMENT.withDescription(field + ": " + message).asRuntimeException();
    }
    
    public static StatusRuntimeException internalError(String message) {
        return Status.INTERNAL.withDescription(message).asRuntimeException();
    }
    
    public static StatusRuntimeException unauthorized() {
        return Status.UNAUTHENTICATED.withDescription("Authentication required").asRuntimeException();
    }
    
    public static StatusRuntimeException forbidden() {
        return Status.PERMISSION_DENIED.withDescription("Access denied").asRuntimeException();
    }
    
    public static StatusRuntimeException fromException(Exception e) {
        return Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException();
    }
}

public class GrpcErrorResponse {
    private final int code;
    private final String message;
    private final String details;
    
    public GrpcErrorResponse(Status status, Metadata trailers) {
        this.code = status.getCode().value();
        this.message = status.getDescription();
        this.details = trails != null ? trailers.toString() : null;
    }
}
```

---

## Load Balancing

```java
// Client-side Load Balancing
package com.learning.lb

import io.grpc.*;
import java.util.List;
import java.util.concurrent.Executors;

public class GrpcLoadBalancer {
    
    public static ManagedChannel createBalancedChannel(List<String> addresses) {
        return GrpcChannelBuilder.forTarget("dns:" + addresses.get(0))
            .nameResolverFactory(new GrpcLbNameResolverFactory())
            .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
            .executor(Executors.newFixedThreadPool(4))
            .build();
    }
    
    // Pick first available (simple load balancing)
    public static ManagedChannel createSimpleChannel(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
    }
}

class GrpcLbNameResolverFactory extends NameResolver.Factory {
    // Implementation for DNS-based load balancing
}
```

---

## Reflection Service for grpcurl

```java
// Enable Reflection
package com.learning.reflection

import io.grpc.protobuf/services.ProtoReflectionServiceV1;
import io.grpc.services.CoreReflectionServiceV1;

extension ServerBuilder {
    fun addReflectionService(): ServerBuilder {
        return this.addService(ProtoReflectionServiceV1.newReflectionService())
            .addService(CoreReflectionServiceV1.newReflectionService());
    }
}

// Server startup with reflection
Server server = ServerBuilder.forPort(8080)
    .addService(new TodoServiceImpl())
    .addService(ProtoReflectionServiceV1.newReflectionService())
    .build();

server.start();
```

---

## Build Instructions (Real-World)

```bash
cd 29-grpc

# Generate proto files and compile
mvn clean compile

# Run server with reflection
mvn exec:java -Dexec.mainClass=com.learning.server.GrpcServer

# Use grpcurl to test
grpcurl -plaintext localhost:8080 list
grpcurl -plaintext localhost:8080 todo.TodoService/CreateTodo \
  -d '{"title": "Test", "description": "Testing", "priority": 1}'

grpcurl -plaintext localhost:8080 grpc.health.v1.Health/Check

# Check server reflection
grpcurl -plaintext -reflect钜 localhost:8080 describe
```

---

## Summary

This module demonstrates:

1. **Protocol Buffers**: Defining gRPC services in .proto files
2. **Unary RPC**: Simple request-response
3. **Server Streaming**: Watching for updates
4. **Client Streaming**: Batch operations
5. **Bidirectional Streaming**: Real-time communication
6. **Interceptors**: Logging and authentication
7. **Health Checks**: gRPC Health Protocol
8. **Error Handling**: Proper Status/Metadata usage

These skills enable building high-performance microservices with gRPC as the communication protocol.