package com.learning.lab07;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class InheritanceTest {

    @Test
    @DisplayName("Subclass inherits parent methods")
    void subclassInheritsMethods() {
        Dog dog = new Dog("Buddy");
        assertEquals("Buddy", dog.getName());
    }

    @Test
    @DisplayName("Subclass overrides parent method")
    void subclassOverrides() {
        Animal animal = new Animal("Generic");
        Dog dog = new Dog("Rex");
        assertEquals("Animal sound", animal.makeSound());
        assertEquals("Bark", dog.makeSound());
    }

    @Test
    @DisplayName("Polymorphism with superclass reference")
    void polymorphism() {
        Animal animal = new Dog("Max");
        assertEquals("Bark", animal.makeSound());
    }

    @Test
    @DisplayName("Super keyword calls parent constructor")
    void superConstructor() {
        Dog dog = new Dog("Charlie");
        assertEquals("Charlie", dog.getName());
    }

    @Test
    @DisplayName("Super keyword calls parent method")
    void superMethod() {
        Dog dog = new Dog("Rocky");
        assertEquals("Animal: Rocky, Dog: Rocky", dog.describe());
    }

    @Test
    @DisplayName("Constructor chaining in inheritance")
    void constructorChaining() {
        Puppy puppy = new Puppy("Max");
        assertNotNull(puppy);
        assertEquals("Max", puppy.getName());
    }

    @Test
    @DisplayName("Instanceof checks type")
    void instanceofCheck() {
        Animal a = new Dog("Rex");
        assertTrue(a instanceof Dog);
        assertTrue(a instanceof Animal);
        assertFalse(a instanceof Cat);
    }

    @Test
    @DisplayName("Object class methods are inherited")
    void objectMethodsInherited() {
        Dog dog = new Dog("Bella");
        assertNotNull(dog.toString());
        assertNotNull(dog.hashCode());
    }

    @Test
    @DisplayName("Protected members accessible in subclass")
    void protectedAccess() {
        Dog dog = new Dog("Shadow");
        assertEquals("Shadow", dog.getName());
    }

    @Test
    @DisplayName("Final method cannot be overridden")
    void finalMethod() {
        Dog dog = new Dog("Final");
        assertEquals("Animal final", dog.finalDescription());
    }
}

class Animal {
    private String name;

    public Animal(String name) { this.name = name; }
    public String getName() { return name; }
    public String makeSound() { return "Animal sound"; }
    public final String finalDescription() { return "Animal final"; }
}

class Dog extends Animal {
    public Dog(String name) { super(name); }
    @Override
    public String makeSound() { return "Bark"; }
    public String describe() { return "Animal: " + getName() + ", Dog: " + getName(); }
}

class Puppy extends Dog {
    public Puppy(String name) { super(name); }
}

class Cat extends Animal {
    public Cat(String name) { super(name); }
    @Override
    public String makeSound() { return "Meow"; }
}
