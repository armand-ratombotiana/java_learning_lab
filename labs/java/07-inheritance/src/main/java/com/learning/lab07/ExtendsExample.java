package com.learning.lab07;

/**
 * Demonstrates inheritance with 'extends', constructor chaining via super(), and method overriding.
 */
public class ExtendsExample {

    public static void showInheritance() {
        System.out.println("=== Inheritance ===");

        Dog dog = new Dog("Buddy", "Golden Retriever");
        System.out.println(dog.speak());
        System.out.println(dog);
    }
}

class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public String speak() {
        return name + " makes a sound";
    }

    @Override
    public String toString() {
        return "Animal{name='" + name + "'}";
    }
}

class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }

    @Override
    public String speak() {
        return name + " says Woof! (breed: " + breed + ")";
    }
}
