package com.javalab.02;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    
    @Test
    @DisplayName("Should build and serialize a Person message")
    void testBuildAndSerialize() {
        MainImplementation.Person person = MainImplementation.Person.newBuilder()
            .setName("Bob")
            .setAge(25)
            .setEmail("bob@example.com")
            .build();
        
        assertEquals("Bob", person.getName());
        assertEquals(25, person.getAge());
        assertEquals("bob@example.com", person.getEmail());
    }
    
    @Test
    @DisplayName("Should round-trip serialize and deserialize")
    void testByteRoundTrip() {
        MainImplementation.Person original = MainImplementation.Person.newBuilder()
            .setName("Alice")
            .setAge(30)
            .setEmail("alice@test.com")
            .build();
        
        byte[] data = original.toByteArray();
        MainImplementation.Person restored = MainImplementation.Person.parseFrom(data);
        
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getEmail(), restored.getEmail());
    }
    
    @Test
    @DisplayName("Should convert to JSON format")
    void testToJson() {
        MainImplementation.Person person = MainImplementation.Person.newBuilder()
            .setName("Charlie")
            .setAge(35)
            .setEmail("charlie@test.com")
            .build();
        
        String json = person.toJson();
        assertTrue(json.contains("Charlie"));
        assertTrue(json.contains("35"));
        assertTrue(json.contains("charlie@test.com"));
    }
}
