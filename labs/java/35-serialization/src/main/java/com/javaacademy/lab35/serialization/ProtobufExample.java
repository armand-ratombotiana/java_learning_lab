package com.javaacademy.lab35.serialization;

import com.google.protobuf.*;
import java.util.Arrays;

public class ProtobufExample {

    public static class ProtoPerson {
        private final String name;
        private final int age;
        private final String email;

        public ProtoPerson(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public byte[] toByteArray() {
            byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] emailBytes = email.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            try {
                writeVarint(bos, (nameBytes.length << 3) | 1);
                writeLengthDelimited(bos, nameBytes);
                writeVarint(bos, (age << 3) | 0);
                writeVarint(bos, age);
                writeVarint(bos, (emailBytes.length << 3) | 1);
                writeLengthDelimited(bos, emailBytes);
            } catch (Exception ignored) { }
            return bos.toByteArray();
        }

        public static ProtoPerson fromByteArray(byte[] data) {
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
            String name = "", email = "";
            int age = 0;
            int pos = 0;
            try {
                while (bis.available() > 0) {
                    int tag = readVarint(bis);
                    int fieldNum = tag >> 3;
                    int wireType = tag & 7;
                    if (wireType == 0) {
                        age = (int) readVarint(bis);
                    } else if (wireType == 1) {
                        int len = (int) readVarint(bis);
                        byte[] buf = new byte[len];
                        bis.read(buf);
                        if (fieldNum == 1) name = new String(buf);
                        else if (fieldNum == 3) email = new String(buf);
                    }
                }
            } catch (Exception ignored) { }
            return new ProtoPerson(name, age, email);
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }

        private static void writeVarint(java.io.OutputStream out, long value) throws Exception {
            while (value > 0x7F) {
                out.write((byte) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
            out.write((byte) value);
        }

        private static long readVarint(java.io.InputStream in) throws Exception {
            long result = 0;
            int shift = 0;
            while (true) {
                int b = in.read();
                result |= (long) (b & 0x7F) << shift;
                if ((b & 0x80) == 0) return result;
                shift += 7;
            }
        }

        private static void writeLengthDelimited(java.io.OutputStream out, byte[] data) throws Exception {
            writeVarint(out, data.length);
            out.write(data);
        }
    }

    public byte[] serialize(ProtoPerson person) {
        return person.toByteArray();
    }

    public ProtoPerson deserialize(byte[] data) {
        return ProtoPerson.fromByteArray(data);
    }
}
