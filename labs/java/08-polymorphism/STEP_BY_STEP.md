# Polymorphism — Step-by-Step Tutorial

## Step 1: Understand Dynamic Dispatch

```java
public class Step1Dispatch {
    public static void main(String[] args) {
        Animal myAnimal = new Animal();
        Animal myDog = new Dog();
        Animal myCat = new Cat();
        
        myAnimal.sound();  // Animal makes sound
        myDog.sound();     // Dog barks
        myCat.sound();     // Cat meows
    }
}

class Animal {
    public void sound() {
        System.out.println("Animal makes sound");
    }
}

class Dog extends Animal {
    @Override
    public void sound() {
        System.out.println("Dog barks");
    }
}

class Cat extends Animal {
    @Override
    public void sound() {
        System.out.println("Cat meows");
    }
}
```

## Step 2: Polymorphic Parameters

```java
public class Step2Params {
    public static void main(String[] args) {
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        
        printArea(circle);
        printArea(rectangle);
    }
    
    // Accepts any Shape
    public static void printArea(Shape shape) {
        System.out.println("Area: " + shape.getArea());
    }
}

abstract class Shape {
    public abstract double getArea();
}

class Circle extends Shape {
    private double radius;
    public Circle(double r) { radius = r; }
    public double getArea() { return Math.PI * radius * radius; }
}

class Rectangle extends Shape {
    private double w, h;
    public Rectangle(double w, double h) { this.w = w; this.h = h; }
    public double getArea() { return w * h; }
}
```

## Step 3: Polymorphic Collections

```java
import java.util.*;

public class Step3Collections {
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog());
        animals.add(new Cat());
        animals.add(new Dog());
        
        // Polymorphic iteration
        for (Animal a : animals) {
            a.sound();
        }
        
        // Notice: ArrayList<Dog> cannot be assigned to List<Animal>
    }
}
```

## Step 4: Instanceof and Pattern Matching

```java
public class Step4Instanceof {
    public static void main(String[] args) {
        Object obj = "Hello, World!";
        
        // Traditional instanceof + cast
        if (obj instanceof String) {
            String s = (String) obj;
            System.out.println("Length: " + s.length());
        }
        
        // Pattern matching (Java 16+)
        if (obj instanceof String s) {
            System.out.println("Length: " + s.length());
        }
        
        // Pattern matching in if-else chain
        Object value = 42;
        String result = switch (value) {
            case String s -> "String: " + s;
            case Integer i -> "Integer: " + i;
            case Double d -> "Double: " + d;
            default -> "Unknown type";
        };
        System.out.println(result);
    }
}
```

## Step 5: Overloading Practice

```java
public class Step5Overloading {
    public static void main(String[] args) {
        Printer p = new Printer();
        p.print(5);            // int
        p.print(3.14);         // double
        p.print("Hello");      // String
        p.print(1, 2, 3);      // varargs
    }
}

class Printer {
    public void print(int x) {
        System.out.println("int: " + x);
    }
    
    public void print(double x) {
        System.out.println("double: " + x);
    }
    
    public void print(String x) {
        System.out.println("String: " + x);
    }
    
    public void print(int... numbers) {
        System.out.println("varargs: " + Arrays.toString(numbers));
    }
}
```

## Step 6: Covariant Return Types

```java
public class Step6Covariant {
    public static void main(String[] args) {
        AnimalBuilder builder = new DogBuilder();
        Animal animal = builder.build();
        System.out.println("Built: " + animal);
        
        DogBuilder dogBuilder = new DogBuilder();
        Dog dog = dogBuilder.build();  // No cast needed!
        dog.bark();
    }
}

class Animal {
    public String toString() { return "Animal"; }
}

class Dog extends Animal {
    public void bark() { System.out.println("Woof!"); }
    public String toString() { return "Dog"; }
}

class AnimalBuilder {
    public Animal build() { return new Animal(); }
}

class DogBuilder extends AnimalBuilder {
    @Override
    public Dog build() { return new Dog(); }  // Covariant!
}
```
