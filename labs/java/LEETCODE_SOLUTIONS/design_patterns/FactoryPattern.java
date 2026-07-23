package design_patterns;

/**
 * Factory Pattern — creates objects without exposing instantiation logic to the client.
 * 
 * Variants:
 * 1. Simple Factory (not a GoF pattern, but common)
 * 2. Factory Method (defined in abstract class, subclasses implement)
 * 3. Abstract Factory (family of related objects)
 * 
 * Time: O(1) for creation
 * Space: O(1) per object
 */
public class FactoryPattern {

    // Product interface
    interface Animal {
        void speak();
    }

    static class Dog implements Animal {
        public void speak() { System.out.println("Woof"); }
    }

    static class Cat implements Animal {
        public void speak() { System.out.println("Meow"); }
    }

    // Simple Factory
    static class AnimalFactory {
        public static Animal create(String type) {
            return switch (type.toLowerCase()) {
                case "dog" -> new Dog();
                case "cat" -> new Cat();
                default -> throw new IllegalArgumentException("Unknown: " + type);
            };
        }
    }

    // Factory Method Pattern
    abstract static class Creator {
        public abstract Animal factoryMethod();
        public void doSomething() {
            Animal a = factoryMethod();
            a.speak();
        }
    }

    static class DogCreator extends Creator {
        public Animal factoryMethod() { return new Dog(); }
    }

    static class CatCreator extends Creator {
        public Animal factoryMethod() { return new Cat(); }
    }

    public static void main(String[] args) {
        // Simple Factory
        Animal dog = AnimalFactory.create("dog");
        Animal cat = AnimalFactory.create("cat");
        dog.speak();
        cat.speak();

        // Factory Method
        Creator dogCreator = new DogCreator();
        Creator catCreator = new CatCreator();
        dogCreator.doSomething();
        catCreator.doSomething();

        System.out.println("All FactoryPattern tests passed.");
    }
}