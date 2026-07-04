# 04 - gRPC

## Overview

gRPC is a high-performance, open-source RPC framework developed by Google. This lab covers gRPC concepts, Protocol Buffers, streaming types, interceptors, and error handling with Java implementations.

## Learning Objectives
- Understand gRPC architecture and Protocol Buffers
- Implement unary, server-streaming, client-streaming, and bidirectional streaming
- Use gRPC interceptors for cross-cutting concerns
- Handle errors and deadlines

## Quick Start
```java
// Proto definition
syntax = "proto3";
service Greeter {
    rpc SayHello (HelloRequest) returns (HelloReply) {}
}
message HelloRequest { string name = 1; }
message HelloReply { string message = 1; }

// Java server
public class GreeterServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
            .addService(new GreeterImpl())
            .build();
        server.start();
        server.awaitTermination();
    }
}
```
