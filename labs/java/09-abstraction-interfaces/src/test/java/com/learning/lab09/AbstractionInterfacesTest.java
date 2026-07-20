package com.learning.lab09;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AbstractionInterfacesTest {

    @Test
    @DisplayName("Interface default method provides implementation")
    void interfaceDefaultMethod() {
        Greeting g = new EnglishGreeting();
        assertEquals("Hello, Guest!", g.greet("Guest"));
        assertEquals("Welcome!", g.welcome());
    }

    @Test
    @DisplayName("Interface static method")
    void interfaceStaticMethod() {
        assertEquals("Hello", Greeting.defaultGreeting());
    }

    @Test
    @DisplayName("Interface private helper method")
    void interfacePrivateMethod() {
        Greeting g = new EnglishGreeting();
        assertEquals("Hello, Test!", g.greet("Test"));
    }

    @Test
    @DisplayName("Abstract class provides partial implementation")
    void abstractClassPartial() {
        Shape s = new Square(5.0);
        assertEquals("Square", s.getType());
        assertEquals(25.0, s.area(), 1e-9);
    }

    @Test
    @DisplayName("Abstract class constructor initializes state")
    void abstractClassConstructor() {
        Shape s = new Circle(3.0);
        assertEquals("Circle", s.getType());
        assertEquals(Math.PI * 9.0, s.area(), 1e-9);
    }

    @Test
    @DisplayName("Class implementing multiple interfaces")
    void multipleInterfaces() {
        var robot = new Robot();
        assertTrue(robot instanceof Workable);
        assertTrue(robot instanceof Maintainable);
        assertEquals("Working...", robot.work());
        assertEquals("Maintenance done", robot.maintain());
    }

    @Test
    @DisplayName("Interface extending another interface")
    void interfaceExtension() {
        AdvancedPrinter ap = new AdvancedPrinter();
        assertTrue(ap instanceof Printable);
        assertTrue(ap instanceof Scannable);
    }

    @Test
    @DisplayName("Default method override in implementing class")
    void defaultMethodOverride() {
        Greeting g = new FrenchGreeting();
        assertEquals("Bonjour, Guest!", g.greet("Guest"));
        assertEquals("Bienvenue!", g.welcome());
    }

    @Test
    @DisplayName("Functional interface with lambda")
    void functionalInterface() {
        Calculator add = (a, b) -> a + b;
        assertEquals(5, add.calculate(2, 3));
        Calculator multiply = (a, b) -> a * b;
        assertEquals(6, multiply.calculate(2, 3));
    }

    @Test
    @DisplayName("Interface constant fields")
    void interfaceConstants() {
        assertEquals(0, Status.UNKNOWN);
        assertEquals(1, Status.ACTIVE);
    }
}

interface Greeting {
    String greet(String name);
    default String welcome() { return "Welcome!"; }
    static String defaultGreeting() { return "Hello"; }
    private String format(String msg) { return msg; }
}

class EnglishGreeting implements Greeting {
    public String greet(String name) { return "Hello, " + name + "!"; }
}

class FrenchGreeting implements Greeting {
    public String greet(String name) { return "Bonjour, " + name + "!"; }
    @Override
    public String welcome() { return "Bienvenue!"; }
}

abstract class Shape {
    private String type;
    public Shape(String type) { this.type = type; }
    public String getType() { return type; }
    public abstract double area();
}

class Square extends Shape {
    private double side;
    public Square(double side) { super("Square"); this.side = side; }
    public double area() { return side * side; }
}

class Circle extends Shape {
    private double radius;
    public Circle(double radius) { super("Circle"); this.radius = radius; }
    public double area() { return Math.PI * radius * radius; }
}

interface Workable { String work(); }
interface Maintainable { String maintain(); }

class Robot implements Workable, Maintainable {
    public String work() { return "Working..."; }
    public String maintain() { return "Maintenance done"; }
}

interface Printable { void print(); }
interface Scannable { void scan(); }
class AdvancedPrinter implements Printable, Scannable {
    public void print() {}
    public void scan() {}
}

@FunctionalInterface
interface Calculator { int calculate(int a, int b); }

interface Status {
    int UNKNOWN = 0;
    int ACTIVE = 1;
}
