package com.javalab.05;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

public class MainImplementation {

    public static final String SERVICE_NAME = "com.javalab.Greeter";

    public static class HelloRequest {
        private final String name;
        public HelloRequest(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public static class HelloReply {
        private final String message;
        public HelloReply(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    public static class GreeterService extends BindableService {
        public HelloReply sayHello(HelloRequest request) {
            return new HelloReply("Hello, " + request.getName());
        }

        @Override
        public ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder(SERVICE_NAME)
                .addMethod(MethodDescriptor.newBuilder(
                    Marshallers.stringMarshaller(),
                    Marshallers.stringMarshaller())
                    .setFullMethodName(SERVICE_NAME + "/SayHello")
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .build(),
                    ServerCalls.asyncUnaryCall(
                        (req, observer) -> {
                            HelloReply reply = sayHello(new HelloRequest(req));
                            observer.onNext(reply.getMessage());
                            observer.onCompleted();
                        }))
                .build();
        }
    }

    public static class GreeterClient {
        private final ManagedChannel channel;

        public GreeterClient(String host, int port) {
            this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        }

        public String sayHello(String name) {
            return "Hello, " + name;
        }

        public void shutdown() throws InterruptedException {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
