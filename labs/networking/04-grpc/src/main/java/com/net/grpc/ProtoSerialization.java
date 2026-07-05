package com.net.grpc;

import java.io.*;
import java.util.*;

public class ProtoSerialization {

    public static class User {
        public int id;
        public String name;
        public String email;
        public List<String> roles;

        public User(int id, String name, String email, List<String> roles) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.roles = roles;
        }

        public byte[] serialize() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            writeVarint(dos, id);
            writeString(dos, name);
            writeString(dos, email);

            dos.writeInt(roles.size());
            for (String role : roles) {
                writeString(dos, role);
            }

            return bos.toByteArray();
        }

        public static User deserialize(byte[] data) throws IOException {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);

            int id = readVarint(dis);
            String name = readString(dis);
            String email = readString(dis);
            int roleCount = dis.readInt();
            List<String> roles = new ArrayList<>();
            for (int i = 0; i < roleCount; i++) {
                roles.add(readString(dis));
            }

            return new User(id, name, email, roles);
        }

        private void writeVarint(DataOutputStream dos, int value) throws IOException {
            while (value > 0x7F) {
                dos.writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
            dos.writeByte(value);
        }

        private static int readVarint(DataInputStream dis) throws IOException {
            int result = 0;
            int shift = 0;
            while (true) {
                int b = dis.readByte() & 0xFF;
                result |= (b & 0x7F) << shift;
                if ((b & 0x80) == 0) return result;
                shift += 7;
            }
        }

        private void writeString(DataOutputStream dos, String s) throws IOException {
            byte[] bytes = s.getBytes("UTF-8");
            writeVarint(dos, bytes.length);
            dos.write(bytes);
        }

        private static String readString(DataInputStream dis) throws IOException {
            int len = readVarint(dis);
            byte[] bytes = new byte[len];
            dis.readFully(bytes);
            return new String(bytes, "UTF-8");
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "', roles=" + roles + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        User original = new User(42, "Alice", "alice@example.com", Arrays.asList("admin", "user"));

        byte[] serialized = original.serialize();
        System.out.println("Serialized size: " + serialized.length + " bytes");

        User deserialized = User.deserialize(serialized);
        System.out.println("Deserialized: " + deserialized);

        System.out.println("Match: " + (original.id == deserialized.id
            && original.name.equals(deserialized.name)));
    }
}
