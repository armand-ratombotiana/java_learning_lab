package com.learning.lab06;

/**
 * Demonstrates a class with fields, constructors, methods, and the 'this' keyword.
 */
public class ClassExample {

    public static void showClassBasics() {
        System.out.println("=== Class with Fields, Constructors, Methods ===");

        Person alice = new Person("Alice", 30);
        Person bob = new Person("Bob");

        System.out.println(alice.greet());
        System.out.println(bob.greet());
        alice.happyBirthday();
        System.out.println("After birthday: " + alice);
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name) {
        this(name, 0);
    }

    public String greet() {
        return "Hello, my name is " + name + " and I am " + age + " years old.";
    }

    public void happyBirthday() {
        this.age++;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
