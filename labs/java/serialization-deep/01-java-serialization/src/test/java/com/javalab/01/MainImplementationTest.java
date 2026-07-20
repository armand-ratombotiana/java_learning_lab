package com.javalab.01;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    
    @Test
    @DisplayName("Should serialize and deserialize a simple object")
    void testRoundTrip() throws Exception {
        MainImplementation original = new MainImplementation(1, "Alice");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        MainImplementation restored = (MainImplementation) ois.readObject();
        ois.close();
        
        assertEquals(original, restored);
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getCachedValue(), restored.getCachedValue());
    }
    
    @Test
    @DisplayName("Should reject invalid deserialized data")
    void testValidation() {
        assertThrows(InvalidObjectException.class, () -> {
            MainImplementation bad = new MainImplementation(-1, "");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(bad);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            ois.readObject();
        });
    }
    
    @Test
    @DisplayName("Should handle null fields")
    void testNullFields() throws Exception {
        MainImplementation obj = new MainImplementation(2, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        assertThrows(InvalidObjectException.class, () -> ois.readObject());
    }
}
