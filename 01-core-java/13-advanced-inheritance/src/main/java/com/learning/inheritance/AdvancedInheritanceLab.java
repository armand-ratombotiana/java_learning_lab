package com.learning.inheritance;

public class AdvancedInheritanceLab {

    public static void main(String[] args) {
        System.out.println("=== Advanced Inheritance Lab ===\n");

        System.out.println("1. Abstract Classes:");
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);
        System.out.println("   Circle area: " + circle.area());
        System.out.println("   Rectangle area: " + rectangle.area());

        System.out.println("\n2. Method Overriding with super:");
        Manager mgr = new Manager("Alice", 75000, 15000);
        System.out.println("   " + mgr.getDetails());

        System.out.println("\n3. Polymorphism:");
        Animal[] animals = {new Dog(), new Cat(), new Bird()};
        for (Animal a : animals) System.out.println("   " + a.speak());

        System.out.println("\n4. Multiple Interface Implementation:");
        FlyingCar fc = new FlyingCar();
        System.out.println("   FlyingCar: " + fc.drive() + ", " + fc.fly());

        System.out.println("\n5. Sealed Classes (Java 17+):");
        Vehicle v = new Car("Toyota");
        System.out.println("   " + describeVehicle(v));

        System.out.println("\n6. Covariant Return Types:");
        BaseProducer base = new BaseProducer();
        DerivedProducer derived = new DerivedProducer();
        System.out.println("   Base produces: " + base.produce().getClass().getSimpleName());
        System.out.println("   Derived produces: " + derived.produce().getClass().getSimpleName());

        System.out.println("\n=== Advanced Inheritance Lab Complete ===");
    }

    static abstract class Shape {
        abstract double area();
        String getName() { return getClass().getSimpleName(); }
    }

    static class Circle extends Shape {
        final double radius;
        Circle(double r) { this.radius = r; }
        double area() { return Math.PI * radius * radius; }
    }

    static class Rectangle extends Shape {
        final double w, h;
        Rectangle(double w, double h) { this.w = w; this.h = h; }
        double area() { return w * h; }
    }

    static class Employee {
        String name; double salary;
        Employee(String n, double s) { this.name = n; this.salary = s; }
        String getDetails() { return name + " earns $" + salary; }
    }

    static class Manager extends Employee {
        double bonus;
        Manager(String n, double s, double b) { super(n, s); this.bonus = b; }
        String getDetails() { return super.getDetails() + " + $" + bonus + " bonus (total: $" + (salary + bonus) + ")"; }
    }

    interface Animal { String speak(); }
    static class Dog implements Animal { public String speak() { return "Dog: Woof!"; } }
    static class Cat implements Animal { public String speak() { return "Cat: Meow!"; } }
    static class Bird implements Animal { public String speak() { return "Bird: Chirp!"; } }

    interface Drivable { default String drive() { return "Driving on road"; } }
    interface Flyable { default String fly() { return "Flying in sky"; } }
    static class FlyingCar implements Drivable, Flyable {}

    sealed interface Vehicle permits Car, Truck {}
    static final class Car implements Vehicle { String brand; Car(String b) { brand = b; } public String toString() { return brand + " Car"; } }
    static final class Truck implements Vehicle { int tons; Truck(int t) { tons = t; } public String toString() { return tons + "t Truck"; } }

    static String describeVehicle(Vehicle v) {
        return switch (v) {
            case Car c -> "Car from " + c.brand;
            case Truck t -> t.tons + "-ton truck";
        };
    }

    static class Product { String name() { return "generic"; } }
    static class Book extends Product { String name() { return "book"; } }

    static class BaseProducer { Product produce() { return new Product(); } }
    static class DerivedProducer extends BaseProducer {
        Book produce() { return new Book(); }
    }
}