package com.javalab.02;

import com.google.protobuf.*;
import java.io.*;

public class MainImplementation {
    
    public static class Person {
        private final String name;
        private final int age;
        private final String email;
        
        private Person(Builder builder) {
            this.name = builder.name;
            this.age = builder.age;
            this.email = builder.email;
        }
        
        public static Builder newBuilder() { return new Builder(); }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }
        
        public byte[] toByteArray() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeUTF(name != null ? name : "");
                dos.writeInt(age);
                dos.writeUTF(email != null ? email : "");
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        public static Person parseFrom(byte[] data) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                String name = dis.readUTF();
                int age = dis.readInt();
                String email = dis.readUTF();
                return newBuilder().setName(name).setAge(age).setEmail(email).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        public String toJson() {
            return "{\"name\":\"" + (name != null ? name : "") + "\",\"age\":" + age + ",\"email\":\"" + (email != null ? email : "") + "\"}";
        }
        
        public static class Builder {
            private String name;
            private int age;
            private String email;
            private Builder() {}
            public Builder setName(String name) { this.name = name; return this; }
            public Builder setAge(int age) { this.age = age; return this; }
            public Builder setEmail(String email) { this.email = email; return this; }
            public Person build() { return new Person(this); }
        }
    }
}
