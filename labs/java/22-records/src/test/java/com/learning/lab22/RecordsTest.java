package com.learning.lab22;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RecordsTest {

    @Test
    @DisplayName("Record auto-generates accessor methods")
    void recordAccessors() {
        var p = new PersonRecord("Alice", 30);
        assertEquals("Alice", p.name());
        assertEquals(30, p.age());
    }

    @Test
    @DisplayName("Record auto-generates toString")
    void recordToString() {
        var p = new PersonRecord("Bob", 25);
        assertEquals("PersonRecord[name=Bob, age=25]", p.toString());
    }

    @Test
    @DisplayName("Record auto-generates equals")
    void recordEquals() {
        var p1 = new PersonRecord("Alice", 30);
        var p2 = new PersonRecord("Alice", 30);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("Record with compact constructor validates")
    void recordCompactConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new ValidatedRecord("", 25));
        assertThrows(IllegalArgumentException.class, () -> new ValidatedRecord("Name", -1));
        assertThrows(IllegalArgumentException.class, () -> new ValidatedRecord("Name", 151));
        assertDoesNotThrow(() -> new ValidatedRecord("Valid", 30));
    }

    @Test
    @DisplayName("ValidatedRecord stores valid data")
    void validatedRecordStores() {
        var v = new ValidatedRecord("Alice", 25);
        assertEquals("Alice", v.name());
        assertEquals(25, v.age());
    }

    @Test
    @DisplayName("Local record defined inside method")
    void localRecord() {
        record Item(String name, double price) {
            public String formattedPrice() {
                return "$" + String.format("%.2f", price);
            }
        }
        var item = new Item("Apple", 0.99);
        assertEquals("Apple", item.name());
        assertEquals("$0.99", item.formattedPrice());
    }

    @Test
    @DisplayName("Record implements Serializable")
    void recordSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(SerializedRecord.class));
    }

    @Test
    @DisplayName("Record serialization roundtrip")
    void recordSerializationRoundtrip() throws Exception {
        var original = new SerializedRecord("Alice", 30, "alice@test.com");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var oos = new java.io.ObjectOutputStream(bytes)) {
            oos.writeObject(original);
        }
        SerializedRecord deserialized;
        try (var ois = new java.io.ObjectInputStream(
                new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            deserialized = (SerializedRecord) ois.readObject();
        }
        assertEquals(original, deserialized);
    }
}
