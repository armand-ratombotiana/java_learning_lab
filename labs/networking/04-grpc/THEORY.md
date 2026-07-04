# gRPC - Theory

## Core Concepts
- **Protocol Buffers**: Interface definition language and serialization format
- **HTTP/2 Transport**: Multiplexed streams for efficient communication
- **Service Definition**: Define RPC methods and message types in .proto files
- **Code Generation**: Generate client and server stubs from proto files

## Streaming Types
1. **Unary RPC**: Single request, single response
2. **Server Streaming**: Single request, stream of responses
3. **Client Streaming**: Stream of requests, single response
4. **Bidirectional Streaming**: Stream of requests and responses

## gRPC vs REST
| Aspect | gRPC | REST |
|--------|------|------|
| Protocol | HTTP/2 | HTTP/1.1 or HTTP/2 |
| Payload | Protobuf (binary) | JSON/XML (text) |
| Code gen | Built-in | Manual or OpenAPI |
| Streaming | Native support | SSE or WebSocket |
| Performance | 5-10x faster | Baseline |
| Browser support | Limited (needs gRPC-Web) | Native |

## Java gRPC Implementation
```java
// Service implementation
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request,
            StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
            .setMessage("Hello, " + request.getName())
            .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

// Client
public class GreeterClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
        HelloRequest request = HelloRequest.newBuilder()
            .setName("World")
            .build();
        HelloReply response = stub.sayHello(request);
        System.out.println(response.getMessage());
        channel.shutdown();
    }
}
```

## Interceptors
```java
public class LoggingInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        System.out.println("Method: " + call.getMethodDescriptor().getFullMethodName());
        return next.startCall(call, headers);
    }
}
```
