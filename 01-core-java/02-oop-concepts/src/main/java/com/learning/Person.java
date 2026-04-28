package com.learning;

/**
 * Demonstrates basic class and object concepts.
 * Shows constructors, instance variables, and methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Person {
    // Instance variables (fields)
    private String name;
    private int age;
    private String email;
    
    // Static variable (class-level)
    private static int personCount = 0;
    
    // Default constructor
    public Person() {
        this("Unknown", 0);
    }
    
    // Parameterized constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        personCount++;
    }
    
    // Full constructor
    public Person(String name, int age, String email) {
        this(name, age);
        this.email = email;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        if (age >= 0) {
        this.age = age;
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Static method
    public static int getPersonCount() {
        return personCount;
    }
    
    // Instance methods
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        if (email != null) {
        System.out.println("Email: " + email);
        }
    }
    
    public void celebrateBirthday() {
        age++;
        System.out.println("Happy Birthday " + name + "! You are now " + age + " years old.");
    }
    
    public boolean isAdult() {
        return age >= 18;
    }
    
    // Override toString()
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }
    
    // Override equals()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }
    
    // Override hashCode()
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }
}