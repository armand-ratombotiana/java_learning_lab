package com.javaacademy.lab35.serialization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("Alice Smith", 30, "secret123!", "alice@example.com");
    }

    @Test
    @DisplayName("Java serialization roundtrip preserves fields")
    void serializationRoundtrip() throws Exception {
        byte[] data = SerializationUtil.serialize(person);
        Person deserialized = SerializationUtil.deserialize(data);
        assertEquals(person.getName(), deserialized.getName());
        assertEquals(person.getAge(), deserialized.getAge());
        assertEquals(person.getEmail(), deserialized.getEmail());
    }

    @Test
    @DisplayName("Transient fields are not serialized by default")
    void transientFieldNotSerialized() throws Exception {
        byte[] data = SerializationUtil.serialize(person);
        Person deserialized = SerializationUtil.deserialize(data);
        assertNotNull(deserialized.getPassword());
    }

    @Test
    @DisplayName("Base64 serialization roundtrip")
    void base64Roundtrip() throws Exception {
        String base64 = SerializationUtil.serializeToBase64(person);
        Person deserialized = SerializationUtil.deserializeFromBase64(base64);
        assertEquals(person.getName(), deserialized.getName());
    }

    @Test
    @DisplayName("Deep copy produces equal but not identical object")
    void deepCopy() throws Exception {
        Person copy = SerializationUtil.deepCopy(person);
        assertEquals(person, copy);
        assertNotSame(person, copy);
    }

    @Test
    @DisplayName("JSON serialization roundtrip with Jackson")
    void jsonRoundtrip() throws Exception {
        JsonSerializationExample jsonEx = new JsonSerializationExample();
        String json = jsonEx.toJson(person);
        assertTrue(json.contains("Alice Smith"));
        assertTrue(json.contains("alice@example.com"));
        Person deserialized = jsonEx.fromJson(json, Person.class);
        assertEquals(person.getName(), deserialized.getName());
    }

    @Test
    @DisplayName("JSON pretty print produces formatted output")
    void jsonPrettyPrint() throws Exception {
        JsonSerializationExample jsonEx = new JsonSerializationExample();
        String pretty = jsonEx.prettyPrint(person);
        assertTrue(pretty.contains(System.lineSeparator()));
    }

    @Test
    @DisplayName("Protocol Buffers serialization roundtrip")
    void protobufRoundtrip() {
        ProtobufExample protobuf = new ProtobufExample();
        ProtobufExample.ProtoPerson original = new ProtobufExample.ProtoPerson("Bob", 25, "bob@test.com");
        byte[] data = protobuf.serialize(original);
        ProtobufExample.ProtoPerson deserialized = protobuf.deserialize(data);
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getAge(), deserialized.getAge());
    }

    @Test
    @DisplayName("Protobuf serialization is compact")
    void protobufCompact() {
        ProtobufExample protobuf = new ProtobufExample();
        ProtobufExample.ProtoPerson p = new ProtobufExample.ProtoPerson("Test", 42, "t@t.com");
        byte[] data = protobuf.serialize(p);
        assertTrue(data.length < 100, "Protobuf should be compact");
    }

    @Test
    @DisplayName("SerializationUtil checks serializable support")
    void supportsSerialization() {
        assertTrue(SerializationUtil.supportsSerialization(person));
        assertFalse(SerializationUtil.supportsSerialization(new Object()));
    }
}
