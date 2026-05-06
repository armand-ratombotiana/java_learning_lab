package com.learning.grpc;

public class GrpcTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 40: gRPC & Protocol Buffers ===");
        demonstrateGrpc();
    }

    private static void demonstrateGrpc() {
        System.out.println("\n--- gRPC ---");
        System.out.println("- High-performance RPC framework");
        System.out.println("- HTTP/2 based transport");
        System.out.println("- Protocol Buffers for serialization");
        System.out.println("- Unary, Server Streaming, Client Streaming, Bidirectional");
        System.out.println("\n--- Protocol Buffers ---");
        System.out.println("- IDL for defining data structures");
        System.out.println("- Binary format, smaller than JSON");
        System.out.println("- Schema evolution support");
    }
}