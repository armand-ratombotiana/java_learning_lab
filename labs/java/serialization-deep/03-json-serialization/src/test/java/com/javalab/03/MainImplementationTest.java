package com.javalab.03;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    
    private MainImplementation serializer;
    
    @BeforeEach
    void setUp() {
        serializer = new MainImplementation();
    }
    
    @Test
    @DisplayName("Should serialize POJO to JSON string")
    void testToJson() throws JsonProcessingException {
        MainImplementation.Person person = new MainImplementation.Person("Alice", 30, "alice@test.com");
        String json = serializer.toJson(person);
        assertTrue(json.contains("Alice"));
        assertTrue(json.contains("30"));
    }
    
    @Test
    @DisplayName("Should deserialize JSON string to POJO")
    void testFromJson() throws JsonProcessingException {
        String json = "{\"name\":\"Bob\",\"age\":25,\"email\":\"bob@test.com\"}";
        MainImplementation.Person person = serializer.fromJson(json);
        assertEquals("Bob", person.getName());
        assertEquals(25, person.getAge());
        assertEquals("bob@test.com", person.getEmail());
    }
    
    @Test
    @DisplayName("Should round-trip through byte array")
    void testByteRoundTrip() throws Exception {
        MainImplementation.Person original = new MainImplementation.Person("Charlie", 35, "charlie@test.com");
        byte[] data = serializer.toByteArray(original);
        MainImplementation.Person restored = serializer.fromByteArray(data);
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
    }
}
