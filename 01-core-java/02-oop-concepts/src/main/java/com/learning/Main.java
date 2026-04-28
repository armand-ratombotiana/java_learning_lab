package com.learning;

/**
 * Main entry point for OOP Concepts module.
 * Demonstrates all object-oriented programming concepts.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== OOP Concepts Module ===\n");
        
        // 1. Classes and Objects
        System.out.println("1. Classes and Objects Demo:");
        Person person = new Person("John Doe", 30);
        person.displayInfo();
        System.out.println();
        
        // 2. Encapsulation
        System.out.println("2. Encapsulation Demo:");
        BankAccount account = new BankAccount("ACC001", 1000.0);
        account.deposit(500.0);
        account.withdraw(200.0);
        System.out.println("Final Balance: $" + account.getBalance());
        System.out.println();
        
        // 3. Inheritance
        System.out.println("3. Inheritance Demo:");
        Dog dog = new Dog("Buddy", 5, "Golden Retriever");
        dog.makeSound();
        dog.displayInfo();
        System.out.println();
        
        // 4. Polymorphism
        System.out.println("4. Polymorphism Demo:");
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);
        System.out.println("Circle area: " + circle.calculateArea());
        System.out.println("Rectangle area: " + rectangle.calculateArea());
        System.out.println();
        
        // 5. Abstraction
        System.out.println("5. Abstraction Demo:");
        Vehicle car = new Car("Toyota", "Camry", 2023);
        Vehicle motorcycle = new Motorcycle("Honda", "CBR", 2023);
        car.start();
        motorcycle.start();
        System.out.println();
        
        // 6. Interfaces
        System.out.println("6. Interfaces Demo:");
        Flyable bird = new Bird("Eagle");
        Flyable airplane = new Airplane("Boeing 747");
        bird.fly();
        airplane.fly();
        System.out.println();

        // 7. Elite OOP Training (Design Patterns & SOLID Principles)
        System.out.println("7. Elite OOP Training:");
        EliteOOPTraining.demonstrateEliteOOPTraining();

        System.out.println("\n\n=== OOP Concepts Module Complete ===");
        System.out.println("All OOP demonstrations executed successfully!");
        System.out.println("\n🎓 You've mastered:");
        System.out.println("   • Core OOP Principles (Encapsulation, Inheritance, Polymorphism, Abstraction)");
        System.out.println("   • Design Patterns (Singleton, Factory, Builder, Strategy)");
        System.out.println("   • SOLID Principles (All 5 principles)");
        System.out.println("   • Advanced OOP Concepts");
        System.out.println("\n🚀 Next: Practice Module 03 - Collections Framework!");
    }
}