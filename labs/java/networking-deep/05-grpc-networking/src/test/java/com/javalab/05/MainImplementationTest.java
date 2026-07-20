package com.javalab.05;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    @Test
    @DisplayName("Greeter service should return greeting")
    void testSayHello() {
        MainImplementation.GreeterService service = new MainImplementation.GreeterService();
        MainImplementation.HelloReply reply = service.sayHello(new MainImplementation.HelloRequest("World"));
        assertEquals("Hello, World", reply.getMessage());
    }

    @Test
    @DisplayName("Greeter client should format greeting")
    void testClient() {
        MainImplementation.GreeterClient client = new MainImplementation.GreeterClient("localhost", 8080);
        String greeting = client.sayHello("Alice");
        assertEquals("Hello, Alice", greeting);
    }
}
