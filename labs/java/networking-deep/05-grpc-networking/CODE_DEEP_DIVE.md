# gRPC Networking -- Code Deep Dive
## Main Implementation
Package: com.javalab.05

### gRPC Service Definition
service Greeter { rpc SayHello (HelloRequest) returns (HelloReply); }

### gRPC Client
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
HelloReply reply = stub.sayHello(request);

### Round-Trip Test Pattern
Each implementation includes a local loopback test that verifies the server
can accept connections, process messages, and return responses correctly.
