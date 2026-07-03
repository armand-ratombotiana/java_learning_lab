# 29 - gRPC

gRPC remote procedure call implementation with Spring Boot. Covers gRPC service definition in .proto files, server-side service implementation, client stub generation, bidirectional streaming, and Spring Boot gRPC integration.

## Prerequisites

- Java 11+
- Maven 3.x
- Spring Boot
- Protocol Buffers compiler (protoc)

## Key Concepts

- Protocol Buffers: `.proto` file definition, message types, service definitions
- gRPC service stubs: blocking, future, and reactive stubs
- Unary RPC: single request, single response
- Server streaming: single request, stream of responses
- Client streaming: stream of requests, single response
- Bidirectional streaming: simultaneous read/write streams
- Spring Boot gRPC integration and auto-configuration
- HTTP/2 transport and binary framing

## Module Structure

- `grpc-learning/` - Spring Boot gRPC application

## Learning Objectives

- Define gRPC services and messages with Protocol Buffers
- Implement gRPC servers and clients
- Use different streaming patterns (unary, server-stream, client-stream, bidirectional)

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 29-grpc
mvn clean package
```

Run the Spring Boot application:

```bash
cd grpc-learning
mvn spring-boot:run
```
