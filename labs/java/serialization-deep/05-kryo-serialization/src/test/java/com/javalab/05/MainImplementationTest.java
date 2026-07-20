package com.javalab.05;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    
    private MainImplementation serializer;
    
    @BeforeEach
    void setUp() {
        serializer = new MainImplementation();
    }
    
    @Test
    @DisplayName("Should serialize and deserialize a Person")
    void testRoundTrip() throws IOException {
        MainImplementation.Person original = new MainImplementation.Person("Alice", 30, "alice@test.com");
        byte[] data = serializer.serialize(original);
        MainImplementation.Person restored = serializer.deserialize(data);
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getEmail(), restored.getEmail());
    }
    
    @Test
    @DisplayName("Should handle null fields")
    void testNullFields() throws IOException {
        MainImplementation.Person original = new MainImplementation.Person(null, 0, null);
        byte[] data = serializer.serialize(original);
        MainImplementation.Person restored = serializer.deserialize(data);
        assertNull(restored.getName());
        assertEquals(0, restored.getAge());
        assertNull(restored.getEmail());
    }
    
    @Test
    @DisplayName("Should serialize with size prefix format")
    void testSizePrefix() throws IOException {
        MainImplementation.Person original = new MainImplementation.Person("Bob", 25, "bob@test.com");
        byte[] data = serializer.serializeWithSize(original);
        MainImplementation.Person restored = serializer.deserializeWithSize(data);
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getEmail(), restored.getEmail());
    }
}
