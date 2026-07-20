package com.javaacademy.lab35.serialization;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

class SerializationUltraDeepTest {

    @Test
    void serializationRoundtrip() throws Exception {
        var original = new SerializationTestModel("test", 42);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }
        SerializationTestModel deserialized;
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            deserialized = (SerializationTestModel) ois.readObject();
        }
        assertEquals(original, deserialized);
    }

    @Test
    void serialVersionUIDConsistency() throws Exception {
        var ois = new ObjectInputStream(new ByteArrayInputStream(new byte[0])) {};
        // Verify serialVersionUID field exists
        var clazz = SerializationTestModel.class;
        var serialFields = clazz.getDeclaredFields();
        boolean hasSerialVersionUID = false;
        for (var f : serialFields) {
            if (f.getName().equals("serialVersionUID")) {
                hasSerialVersionUID = true;
                break;
            }
        }
        assertTrue(hasSerialVersionUID);
    }

    @Test
    void transientFieldsNotSerialized() throws Exception {
        var obj = new SerializationTestModel("data", 99);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        SerializationTestModel deserialized;
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            deserialized = (SerializationTestModel) ois.readObject();
        }
        assertEquals(0, deserialized.getTransientValue());
    }
}

class SerializationTestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private int value;
    private transient int transientValue = 42;

    public SerializationTestModel(String data, int value) {
        this.data = data;
        this.value = value;
        this.transientValue = 42;
    }

    public String getData() { return data; }
    public int getValue() { return value; }
    public int getTransientValue() { return transientValue; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SerializationTestModel s)) return false;
        return value == s.value && data.equals(s.data);
    }
}
