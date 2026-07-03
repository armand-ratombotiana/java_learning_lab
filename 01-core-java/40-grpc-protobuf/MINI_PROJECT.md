# Module 40: gRPC & Protocol Buffers - Mini Project

**Project Name**: High-Performance Microservice Link  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Create a strict API contract using Protocol Buffers, generate the Java source code automatically via Maven, and implement a high-performance gRPC Client and Server to demonstrate Unary and Server-Streaming RPCs.

## 📝 Requirements

### Core Features

1. **The Protobuf Contract (`.proto`)**:
   - Create a file `src/main/proto/calculator.proto`.
   - Define a `CalculatorService`.
   - Define a Unary RPC: `rpc Multiply(MultiplyRequest) returns (MultiplyResponse)`.
   - Define a Server-Streaming RPC: `rpc PrimeFactors(PrimeRequest) returns (stream PrimeResponse)`.
   - Define the corresponding message structures.

2. **Code Generation Configuration**:
   - In your `pom.xml`, include the `os-maven-plugin` and `protobuf-maven-plugin` to hook into the Maven compile lifecycle. This will parse your `.proto` file and generate the required Java Builder classes and gRPC base stubs.

3. **The gRPC Server**:
   - Create a class `CalculatorServiceImpl` that extends the generated `CalculatorServiceImplBase`.
   - Override `multiply()` to return the product of two numbers.
   - Override `primeFactors()` to calculate the prime factors of a given number, calling `responseObserver.onNext()` for each factor found to stream them back to the client instantly, followed by `responseObserver.onCompleted()`.
   - In a `main` method, instantiate an `io.grpc.Server`, bind your service, and start it on a specific port (e.g., 9090). Add a shutdown hook to handle `server.shutdown()`.

4. **The gRPC Client**:
   - Create a `CalculatorClient` class.
   - Establish a `ManagedChannel` pointing to `localhost:9090`. (Use `.usePlaintext()` for local development to bypass SSL).
   - Create a blocking stub: `CalculatorServiceGrpc.newBlockingStub(channel)`.
   - Call the `multiply` method and print the result.
   - Call the `primeFactors` method, which returns an `Iterator<PrimeResponse>`. Loop through the iterator and print the streamed numbers as they arrive.

---

## 💡 Solution Blueprint

1. **The `.proto` Contract**:
   ```protobuf
   syntax = "proto3";
   option java_package = "com.example.grpc";
   option java_multiple_files = true;

   message MultiplyRequest {
     int32 a = 1;
     int32 b = 2;
   }
   message MultiplyResponse {
     int32 result = 1;
   }
   message PrimeRequest { int32 number = 1; }
   message PrimeResponse { int32 factor = 1; }

   service CalculatorService {
     rpc Multiply(MultiplyRequest) returns (MultiplyResponse);
     rpc PrimeFactors(PrimeRequest) returns (stream PrimeResponse);
   }
   ```

2. **The Server Implementation**:
   ```java
   public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
       @Override
       public void multiply(MultiplyRequest req, StreamObserver<MultiplyResponse> responseObserver) {
           int result = req.getA() * req.getB();
           responseObserver.onNext(MultiplyResponse.newBuilder().setResult(result).build());
           responseObserver.onCompleted();
       }

       @Override
       public void primeFactors(PrimeRequest req, StreamObserver<PrimeResponse> responseObserver) {
           int k = 2;
           int N = req.getNumber();
           while (N > 1) {
               if (N % k == 0) {
                   responseObserver.onNext(PrimeResponse.newBuilder().setFactor(k).build());
                   N = N / k;
                   try { Thread.sleep(500); } catch (Exception e) {} // Simulate slow calculation
               } else {
                   k = k + 1;
               }
           }
           responseObserver.onCompleted();
       }
   }
   ```