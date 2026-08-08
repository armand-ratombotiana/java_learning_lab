package com.javalab.lab03;

import java.nio.charset.StandardCharsets;
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
    
    public MainImplementation() {}
    
    public String toJson(Person person) {
        return "{\"name\":\"" + escape(person.name) + "\",\"age\":" + person.age
                + ",\"email\":\"" + escape(person.email) + "\"}";
    }
    
    public Person fromJson(String json) {
        Person person = new Person();
        int keyStart = json.indexOf('"');
        while (keyStart >= 0) {
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd < 0) break;
            String key = json.substring(keyStart + 1, keyEnd);
            int colon = json.indexOf(':', keyEnd);
            int cursor = colon + 1;
            while (cursor < json.length() && Character.isWhitespace(json.charAt(cursor))) cursor++;
            if (cursor < json.length() && json.charAt(cursor) == '"') {
                int valueStart = cursor + 1;
                int valueEnd = valueStart;
                while (valueEnd < json.length() && json.charAt(valueEnd) != '"') {
                    if (json.charAt(valueEnd) == '\\') valueEnd++;
                    valueEnd++;
                }
                String value = unescape(json.substring(valueStart, valueEnd));
                switch (key) {
                    case "name" -> person.setName(value);
                    case "email" -> person.setEmail(value);
                }
                cursor = valueEnd + 1;
            } else {
                int valueEnd = json.indexOf(',', cursor);
                if (valueEnd < 0) valueEnd = json.indexOf('}', cursor);
                if (valueEnd < 0) valueEnd = json.length();
                String value = json.substring(cursor, valueEnd).trim();
                if (key.equals("age")) person.setAge(Integer.parseInt(value));
                cursor = valueEnd;
            }
            keyStart = json.indexOf('"', cursor);
        }
        return person;
    }
    
    public byte[] toByteArray(Person person) {
        return toJson(person).getBytes(StandardCharsets.UTF_8);
    }
    
    public Person fromByteArray(byte[] data) {
        return fromJson(new String(data, StandardCharsets.UTF_8));
    }
    
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}