package com.javaacademy.lab35.serialization;

import java.io.*;
import java.util.Base64;

public class SerializationUtil {

    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        }
    }

    public static String serializeToBase64(Object obj) throws IOException {
        return Base64.getEncoder().encodeToString(serialize(obj));
    }

    public static <T> T deserializeFromBase64(String base64) throws IOException, ClassNotFoundException {
        return deserialize(Base64.getDecoder().decode(base64));
    }

    public static <T> T deepCopy(T obj) throws IOException, ClassNotFoundException {
        return deserialize(serialize(obj));
    }

    public static boolean supportsSerialization(Object obj) {
        return obj instanceof Serializable;
    }
}
