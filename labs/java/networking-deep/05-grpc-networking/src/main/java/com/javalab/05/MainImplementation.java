package com.javalab.lab05;

import io.grpc.*;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class MainImplementation {

    public static final String SERVICE_NAME = "com.javalab.Greeter";

    public static final MethodDescriptor.Marshaller<String> STRING_MARSHALLER =
        new MethodDescriptor.Marshaller<>() {
            @Override
            public java.io.InputStream stream(String value) {
                return new java.io.ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String parse(java.io.InputStream stream) {
                try {
                    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

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

    public static class GreeterService implements BindableService {
        public HelloReply sayHello(HelloRequest request) {
            return new HelloReply("Hello, " + request.getName());
        }

        @Override
        public ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder(SERVICE_NAME)
.addMethod(MethodDescriptor.newBuilder(
                    STRING_MARSHALLER,
                    STRING_MARSHALLER)
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
