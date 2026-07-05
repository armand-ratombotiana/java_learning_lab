package com.learning.lab08;

/**
 * Demonstrates covariant return types — overriding a method with a more specific return type.
 */
public class CovariantReturnExample {

    public static void showCovariantReturns() {
        System.out.println("=== Covariant Return Types ===");

        AnimalFactory factory = new DogFactory();
        Animal animal = factory.create();
        System.out.println("Created: " + animal.getClass().getSimpleName() + " -> " + animal.speak());

        factory = new CatFactory();
        animal = factory.create();
        System.out.println("Created: " + animal.getClass().getSimpleName() + " -> " + animal.speak());
    }
}

class Animal {
    public String speak() { return "Some sound"; }
}

class Dog extends Animal {
    @Override
    public String speak() { return "Woof!"; }

    public void fetch() { System.out.println("Fetching..."); }
}

class Cat extends Animal {
    @Override
    public String speak() { return "Meow!"; }
}

class AnimalFactory {
    public Animal create() { return new Animal(); }
}

class DogFactory extends AnimalFactory {
    @Override
    public Dog create() { return new Dog(); }
}

class CatFactory extends AnimalFactory {
    @Override
    public Cat create() { return new Cat(); }
}
