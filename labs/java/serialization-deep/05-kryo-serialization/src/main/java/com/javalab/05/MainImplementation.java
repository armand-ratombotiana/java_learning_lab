package com.javalab.05;

import java.io.*;
import java.util.Objects;

public class MainImplementation {
    
    public static class Person {
        private String name;
        private int age;
        private String email;
        
        public Person() {}
        
        public Person(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name) && Objects.equals(email, person.email);
        }
        
        @Override
        public int hashCode() { return Objects.hash(name, age, email); }
    }
    
    public byte[] serialize(Person person) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(person.getName() != null ? person.getName() : "");
        dos.writeInt(person.getAge());
        dos.writeUTF(person.getEmail() != null ? person.getEmail() : "");
        dos.flush();
        return baos.toByteArray();
    }
    
    public Person deserialize(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        String name = dis.readUTF();
        int age = dis.readInt();
        String email = dis.readUTF();
        return new Person(name.isEmpty() ? null : name, age, email.isEmpty() ? null : email);
    }
    
    public byte[] serializeWithSize(Person person) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] nameBytes = (person.getName() != null ? person.getName() : "").getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] emailBytes = (person.getEmail() != null ? person.getEmail() : "").getBytes(java.nio.charset.StandardCharsets.UTF_8);
        dos.writeInt(nameBytes.length);
        dos.write(nameBytes);
        dos.writeInt(person.getAge());
        dos.writeInt(emailBytes.length);
        dos.write(emailBytes);
        dos.flush();
        return baos.toByteArray();
    }
    
    public Person deserializeWithSize(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        int nameLen = dis.readInt();
        byte[] nameBytes = new byte[nameLen];
        dis.readFully(nameBytes);
        String name = new String(nameBytes, java.nio.charset.StandardCharsets.UTF_8);
        int age = dis.readInt();
        int emailLen = dis.readInt();
        byte[] emailBytes = new byte[emailLen];
        dis.readFully(emailBytes);
        String email = new String(emailBytes, java.nio.charset.StandardCharsets.UTF_8);
        return new Person(name.isEmpty() ? null : name, age, email.isEmpty() ? null : email);
    }
}
