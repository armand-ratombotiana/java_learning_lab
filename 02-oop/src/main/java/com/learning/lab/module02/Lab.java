package com.learning.lab.module02;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 02: OOP Concepts ===");
        encapsulationDemo();
        inheritanceDemo();
        polymorphismDemo();
        abstractionDemo();
        interfaceDemo();
    }

    static void encapsulationDemo() {
        System.out.println("\n--- Encapsulation ---");
        Person person = new Person("John", 30);
        System.out.println("Name: " + person.getName() + ", Age: " + person.getAge());
        person.setAge(31);
        System.out.println("Updated Age: " + person.getAge());
    }

    static void inheritanceDemo() {
        System.out.println("\n--- Inheritance ---");
        Dog dog = new Dog("Buddy", 3);
        dog.display();
        dog.makeSound();
    }

    static void polymorphismDemo() {
        System.out.println("\n--- Polymorphism ---");
        Animal[] animals = {new Dog("Rex"), new Cat("Whiskers")};
        for (Animal a : animals) {
            a.makeSound();
        }
    }

    static void abstractionDemo() {
        System.out.println("\n--- Abstraction ---");
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        System.out.println("Circle area: " + circle.calculateArea());
        System.out.println("Rectangle area: " + rectangle.calculateArea());
    }

    static void interfaceDemo() {
        System.out.println("\n--- Interface ---");
        Drawable drawable = () -> System.out.println("Drawing shape");
        drawable.draw();
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}

class Animal {
    protected String name;
    public Animal(String name) { this.name = name; }
    public void makeSound() { System.out.println("Some sound"); }
}

class Dog extends Animal {
    private int age;
    public Dog(String name, int age) {
        super(name);
        this.age = age;
    }
    public void display() { System.out.println("Dog: " + name + ", Age: " + age); }
    @Override
    public void makeSound() { System.out.println(name + " barks"); }
}

class Cat extends Animal {
    public Cat(String name) { super(name); }
    @Override
    public void makeSound() { System.out.println(name + " meows"); }
}

abstract class Shape {
    public abstract double calculateArea();
}

class Circle extends Shape {
    private double radius;
    public Circle(double radius) { this.radius = radius; }
    @Override
    public double calculateArea() { return Math.PI * radius * radius; }
}

class Rectangle extends Shape {
    private double width, height;
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    @Override
    public double calculateArea() { return width * height; }
}

interface Drawable {
    void draw();
}