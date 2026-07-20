package com.javalab.04;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    
    @Test
    @DisplayName("Should marshal object to XML")
    void testToXml() throws JAXBException {
        MainImplementation person = new MainImplementation(1, "Alice", 30);
        String xml = person.toXml();
        assertTrue(xml.contains("Alice"));
        assertTrue(xml.contains("30"));
        assertTrue(xml.contains("id=\"1\""));
    }
    
    @Test
    @DisplayName("Should unmarshal XML to object")
    void testFromXml() throws JAXBException {
        MainImplementation person = new MainImplementation(2, "Bob", 25);
        String xml = person.toXml();
        MainImplementation restored = MainImplementation.fromXml(xml);
        assertEquals(person.getId(), restored.getId());
        assertEquals(person.getName(), restored.getName());
        assertEquals(person.getAge(), restored.getAge());
    }
    
    @Test
    @DisplayName("Should round-trip through byte array")
    void testByteRoundTrip() throws JAXBException {
        MainImplementation original = new MainImplementation(3, "Charlie", 35);
        byte[] data = original.toByteArray();
        MainImplementation restored = MainImplementation.fromByteArray(data);
        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
    }
}
