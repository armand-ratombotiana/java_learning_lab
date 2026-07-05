package com.learning.lab22;

import java.io.*;

/**
 * Demonstrates record serialization and deserialization (records are naturally serializable).
 */
public class RecordSerializationExample {

    public static void showRecordSerialization() throws Exception {
        System.out.println("=== Record Serialization ===");

        SerializedRecord original = new SerializedRecord("Alice", 30, "alice@example.com");
        String filename = "record.ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(original);
            System.out.println("Serialized: " + original);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            SerializedRecord deserialized = (SerializedRecord) ois.readObject();
            System.out.println("Deserialized: " + deserialized);
            System.out.println("Equal? " + original.equals(deserialized));
        }

        new File(filename).delete();
    }
}

record SerializedRecord(String name, int age, String email) implements Serializable {}
