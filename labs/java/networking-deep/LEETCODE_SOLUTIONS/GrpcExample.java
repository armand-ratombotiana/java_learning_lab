package networking;

import java.net.*;
import java.io.*;

/**
 * gRPC (gRPC Remote Procedure Call) demonstration (conceptual).
 * 
 * gRPC is a high-performance RPC framework using HTTP/2 and Protocol Buffers.
 * 
 * Java libraries:
 * - io.grpc:grpc-netty-shaded:1.60.0
 * - io.grpc:grpc-protobuf:1.60.0
 * - io.grpc:grpc-stub:1.60.0
 * 
 * This class demonstrates the gRPC programming model.
 */
public class GrpcExample {

    /*
    // Step 1: Define service in .proto file
    //   service Greeter {
    //     rpc SayHello (HelloRequest) returns (HelloReply);
    //   }
    //   message HelloRequest { string name = 1; }
    //   message HelloReply { string message = 1; }
    //
    // Step 2: Compile with protoc
    //   protoc --java_out=src/main/java --grpc-java_out=src/main/java greeter.proto
    //
    // Step 3: Implement the service
    //   static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    //       @Override
    //       public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
    //           String message = "Hello " + req.getName();
    //           HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
    //           responseObserver.onNext(reply);
    //           responseObserver.onCompleted();
    //       }
    //   }
    //
    // Step 4: Start the server
    //   Server server = ServerBuilder.forPort(50051)
    //       .addService(new GreeterImpl())
    //       .build()
    //       .start();
    //
    // Step 5: Client call
    //   ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
    //       .usePlaintext()
    //       .build();
    //   GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
    //   HelloReply response = stub.sayHello(HelloRequest.newBuilder().setName("World").build());
    */

    // Simulated gRPC-style service
    interface GreeterService {
        String sayHello(String name);
    }

    static class GreeterImpl implements GreeterService {
        public String sayHello(String name) { return "Hello " + name; }
    }

    static class GrpcClient {
        private final GreeterService service;

        GrpcClient(GreeterService service) { this.service = service; }

        String greet(String name) { return service.sayHello(name); }
    }

    public static void main(String[] args) {
        // Simulated gRPC flow
        GreeterService server = new GreeterImpl();
        GrpcClient client = new GrpcClient(server);

        String response = client.greet("World");
        System.out.println("gRPC response: " + response);
        assert response.equals("Hello World");

        // Unary vs streaming
        System.out.println("\ngRPC communication patterns:");
        System.out.println("  Unary: client sends 1 request, server sends 1 response");
        System.out.println("  Server streaming: server sends multiple responses");
        System.out.println("  Client streaming: client sends multiple requests");
        System.out.println("  Bidirectional: both sides stream concurrently");

        System.out.println("All GrpcExample tests passed.");
    }
}