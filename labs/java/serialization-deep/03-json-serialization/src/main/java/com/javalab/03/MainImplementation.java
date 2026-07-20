package com.javalab.03;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
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
    
    private final ObjectMapper mapper;
    
    public MainImplementation() {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public String toJson(Person person) throws JsonProcessingException {
        return mapper.writeValueAsString(person);
    }
    
    public Person fromJson(String json) throws JsonProcessingException {
        return mapper.readValue(json, Person.class);
    }
    
    public byte[] toByteArray(Person person) throws JsonProcessingException {
        return mapper.writeValueAsBytes(person);
    }
    
    public Person fromByteArray(byte[] data) throws IOException {
        return mapper.readValue(data, Person.class);
    }
    
    public ObjectMapper getMapper() { return mapper; }
}
