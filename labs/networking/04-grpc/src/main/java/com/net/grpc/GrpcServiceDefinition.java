package com.net.grpc;

import java.util.*;
import java.util.concurrent.*;

public class GrpcServiceDefinition {

    public enum MethodType { UNARY, SERVER_STREAMING, CLIENT_STREAMING, BIDIRECTIONAL }

    public static class MethodDefinition {
        public final String name;
        public final String inputType;
        public final String outputType;
        public final MethodType type;

        public MethodDefinition(String name, String input, String output, MethodType type) {
            this.name = name;
            this.inputType = input;
            this.outputType = output;
            this.type = type;
        }

        @Override
        public String toString() {
            return type + " " + name + "(" + inputType + ") -> (" + outputType + ")";
        }
    }

    public static class ServiceDefinition {
        public final String serviceName;
        public final String packageName;
        public final List<MethodDefinition> methods = new ArrayList<>();

        public ServiceDefinition(String serviceName, String packageName) {
            this.serviceName = serviceName;
            this.packageName = packageName;
        }

        public ServiceDefinition addMethod(String name, String input, String output, MethodType type) {
            methods.add(new MethodDefinition(name, input, output, type));
            return this;
        }

        public void printDefinition() {
            System.out.println("service " + serviceName + " {");
            System.out.println("  package: " + packageName);
            for (MethodDefinition m : methods) {
                System.out.println("  rpc " + m.name + "(" + m.inputType + ") returns (" + m.outputType + ")");
            }
            System.out.println("}");
        }
    }

    public static class GrpcServer {
        private final List<ServiceDefinition> services = new ArrayList<>();

        public void registerService(ServiceDefinition svc) {
            services.add(svc);
            System.out.println("Registered service: " + svc.serviceName);
        }

        public void start(int port) {
            System.out.println("gRPC server listening on port " + port);
            for (ServiceDefinition svc : services) {
                System.out.println("  Serving: " + svc.serviceName + " (" + svc.methods.size() + " methods)");
            }
        }
    }

    public static void main(String[] args) {
        ServiceDefinition userService = new ServiceDefinition("UserService", "com.example.user.v1")
            .addMethod("GetUser", "GetUserRequest", "GetUserResponse", MethodType.UNARY)
            .addMethod("ListUsers", "ListUsersRequest", "ListUsersResponse", MethodType.SERVER_STREAMING)
            .addMethod("UpdateUser", "UpdateUserRequest", "UpdateUserResponse", MethodType.UNARY)
            .addMethod("WatchUsers", "WatchUsersRequest", "UserEvent", MethodType.BIDIRECTIONAL);

        ServiceDefinition orderService = new ServiceDefinition("OrderService", "com.example.order.v1")
            .addMethod("CreateOrder", "CreateOrderRequest", "CreateOrderResponse", MethodType.UNARY)
            .addMethod("StreamOrders", "StreamOrdersRequest", "Order", MethodType.SERVER_STREAMING);

        System.out.println("=== gRPC Service Definitions ===\n");
        userService.printDefinition();
        System.out.println();
        orderService.printDefinition();

        GrpcServer server = new GrpcServer();
        server.registerService(userService);
        server.registerService(orderService);
        server.start(50051);
    }
}
