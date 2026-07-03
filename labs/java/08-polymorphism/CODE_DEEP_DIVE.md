# Polymorphism — Code Deep Dive

## Example 1: Runtime Polymorphism

```java
abstract class Shape {
    protected String color;
    
    public Shape(String color) { this.color = color; }
    public abstract double area();
    public abstract double perimeter();
    
    public String getColor() { return color; }
}

class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double area() { return Math.PI * radius * radius; }
    
    @Override
    public double perimeter() { return 2 * Math.PI * radius; }
}

class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double area() { return width * height; }
    
    @Override
    public double perimeter() { return 2 * (width + height); }
}

class Triangle extends Shape {
    private double a, b, c;
    
    public Triangle(String color, double a, double b, double c) {
        super(color);
        this.a = a; this.b = b; this.c = c;
    }
    
    @Override
    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    
    @Override
    public double perimeter() { return a + b + c; }
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle("Red", 5),
            new Rectangle("Blue", 4, 6),
            new Triangle("Green", 3, 4, 5)
        };
        
        // Polymorphic loop — same method call, different behavior
        for (Shape shape : shapes) {
            System.out.printf("%s %s: area=%.2f, perimeter=%.2f%n",
                shape.getColor(), shape.getClass().getSimpleName(),
                shape.area(), shape.perimeter());
        }
        
        // Method accepting Shape (polymorphic parameter)
        printShapeInfo(new Circle("Yellow", 10));
    }
    
    public static void printShapeInfo(Shape s) {
        System.out.println("Shape: " + s.getClass().getSimpleName());
        System.out.println("  Area: " + s.area());
        System.out.println("  Perimeter: " + s.perimeter());
    }
}
```

## Example 2: Overloading vs Overriding

```java
public class OverloadVsOverride {
    
    static class Parent {
        // Overrideable
        public void show(String s) {
            System.out.println("Parent.show(String): " + s);
        }
        
        // Overloadable
        public void print(int x) {
            System.out.println("Parent.print(int): " + x);
        }
        
        public void print(double x) {
            System.out.println("Parent.print(double): " + x);
        }
    }
    
    static class Child extends Parent {
        // Override — same signature
        @Override
        public void show(String s) {
            System.out.println("Child.show(String): " + s);
        }
        
        // Overload — NOT override (different parameter type)
        // This is a new method, not overriding print(double)
        public void print(String s) {
            System.out.println("Child.print(String): " + s);
        }
    }
    
    public static void main(String[] args) {
        Parent ref = new Child();
        
        ref.show("hello");  // Override: Child's version (runtime)
        ref.print(5);       // Overload: Parent.print(int) (compile-time)
        ref.print(3.14);    // Overload: Parent.print(double) (compile-time)
        // ref.print("hi"); // Error! Parent has no print(String)
        
        Child child = new Child();
        child.print("hi");  // OK: Child has print(String)
    }
}
```

## Example 3: Covariant Return Types

```java
class Animal {
    protected String name;
    
    public Animal(String name) { this.name = name; }
    
    public Animal reproduce() {
        System.out.println(name + " reproduces");
        return new Animal("offspring");
    }
}

class Dog extends Animal {
    public Dog(String name) { super(name); }
    
    @Override
    public Dog reproduce() {  // Returns Dog, not Animal!
        System.out.println(name + " has a puppy");
        return new Dog("puppy");
    }
    
    public void bark() { System.out.println(name + " barks"); }
}

public class CovariantDemo {
    public static void main(String[] args) {
        Animal a = new Dog("Max");
        // Animal offspring = a.reproduce();  // Returns Animal
        
        Dog d = new Dog("Rex");
        Dog puppy = d.reproduce();  // No cast needed!
        puppy.bark();               // Can call Dog-specific methods
        
        // Without covariant returns, would need:
        // Dog puppy = (Dog) d.reproduce();
    }
}
```

## Example 4: Interface Polymorphism

```java
interface PaymentMethod {
    void pay(double amount);
    String getPaymentType();
}

class CreditCard implements PaymentMethod {
    private String cardNumber;
    
    public CreditCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f via Credit Card %s%n", amount,
            cardNumber.substring(cardNumber.length() - 4));
    }
    
    @Override
    public String getPaymentType() { return "Credit Card"; }
}

class PayPal implements PaymentMethod {
    private String email;
    
    public PayPal(String email) { this.email = email; }
    
    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f via PayPal (%s)%n", amount, email);
    }
    
    @Override
    public String getPaymentType() { return "PayPal"; }
}

class Cash implements PaymentMethod {
    @Override
    public void pay(double amount) {
        System.out.printf("Paid $%.2f in cash%n", amount);
    }
    
    @Override
    public String getPaymentType() { return "Cash"; }
}

public class InterfacePolyDemo {
    public static void main(String[] args) {
        PaymentMethod[] methods = {
            new CreditCard("1234-5678-9012-3456"),
            new PayPal("alice@example.com"),
            new Cash()
        };
        
        // Process polymorphically
        for (PaymentMethod m : methods) {
            processPayment(m, 100.0);
        }
    }
    
    static void processPayment(PaymentMethod method, double amount) {
        System.out.print("Processing via " + method.getPaymentType() + ": ");
        method.pay(amount);
    }
}
```
