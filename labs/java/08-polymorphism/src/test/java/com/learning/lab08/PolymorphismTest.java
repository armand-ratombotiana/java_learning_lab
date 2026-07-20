package com.learning.lab08;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PolymorphismTest {

    @Test
    @DisplayName("Dynamic dispatch calls overridden method")
    void dynamicDispatch() {
        Shape shape = new Circle(5.0);
        assertEquals("Circle area: 78.54", shape.displayArea());
        shape = new Rectangle(4.0, 6.0);
        assertEquals("Rectangle area: 24.00", shape.displayArea());
    }

    @Test
    @DisplayName("Polymorphic collection")
    void polymorphicCollection() {
        Shape[] shapes = {new Circle(3.0), new Rectangle(4.0, 5.0)};
        assertTrue(shapes[0] instanceof Circle);
        assertTrue(shapes[1] instanceof Rectangle);
    }

    @Test
    @DisplayName("Interface polymorphism")
    void interfacePolymorphism() {
        Drawable d = new Circle(2.0);
        assertEquals("Drawing Circle", d.draw());
        d = new Rectangle(1.0, 2.0);
        assertEquals("Drawing Rectangle", d.draw());
    }

    @Test
    @DisplayName("Abstract class cannot be instantiated")
    void abstractCannotBeInstantiated() {
        assertThrows(InstantiationError.class, () -> {
            if (false) new Shape("dummy");
        });
        // Shape is abstract - cannot be instantiated directly
        Shape s = new Circle(1.0);
        assertNotNull(s);
    }

    @Test
    @DisplayName("Covariant return type in override")
    void covariantReturn() {
        AnimalProducer producer = new DogProducer();
        Animal animal = producer.produce();
        assertTrue(animal instanceof Dog);
    }

    @Test
    @DisplayName("Method overloading with different types")
    void methodOverloading() {
        Printer p = new Printer();
        assertEquals("printing string", p.print("hello"));
        assertEquals("printing number", p.print(42));
        assertEquals("printing double", p.print(3.14));
    }

    @Test
    @DisplayName("Superclass reference to subclass object")
    void superclassReference() {
        Animal a = new Dog("Rex");
        assertTrue(a instanceof Animal);
        assertTrue(a instanceof Dog);
    }

    @Test
    @DisplayName("Polymorphic method parameter")
    void polymorphicParameter() {
        ShapeProcessor sp = new ShapeProcessor();
        assertEquals("Processed Circle area", sp.process(new Circle(2.0)));
        assertEquals("Processed Rectangle area", sp.process(new Rectangle(2.0, 3.0)));
    }

    @Test
    @DisplayName("Late binding of overridden methods")
    void lateBinding() {
        Animal a = new Dog("Max");
        String result = useAnimal(a);
        assertEquals("Dog barks", result);
    }

    String useAnimal(Animal a) {
        return a.getAction();
    }
}

abstract class Shape implements Drawable {
    protected String type;
    public Shape(String type) { this.type = type; }
    public abstract double area();
    public String displayArea() {
        return String.format("%s area: %.2f", type, area());
    }
    public String draw() { return "Drawing " + type; }
}

class Circle extends Shape {
    private double radius;
    public Circle(double radius) { super("Circle"); this.radius = radius; }
    public double area() { return Math.PI * radius * radius; }
}

class Rectangle extends Shape {
    private double w, h;
    public Rectangle(double w, double h) { super("Rectangle"); this.w = w; this.h = h; }
    public double area() { return w * h; }
}

interface Drawable {
    String draw();
}

class Printer {
    public String print(String s) { return "printing string"; }
    public String print(int n) { return "printing number"; }
    public String print(double d) { return "printing double"; }
}

class Animal {
    protected String name;
    public Animal(String name) { this.name = name; }
    public String getAction() { return "Animal moves"; }
}

class Dog extends Animal {
    public Dog(String name) { super(name); }
    public String getAction() { return "Dog barks"; }
}

class AnimalProducer { public Animal produce() { return new Animal("generic"); } }
class DogProducer extends AnimalProducer {
    @Override
    public Dog produce() { return new Dog("puppy"); }
}

class ShapeProcessor {
    public String process(Shape s) { return "Processed " + s.type + " area"; }
}
