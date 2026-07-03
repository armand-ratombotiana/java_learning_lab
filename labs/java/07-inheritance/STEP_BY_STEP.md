# Inheritance — Step-by-Step Tutorial

## Step 1: Create a Base Class

```java
public class Step1Base {
    public static void main(String[] args) {
        Vehicle vehicle = new Vehicle("Generic", 2020);
        System.out.println(vehicle.getInfo());
    }
}

class Vehicle {
    protected String make;
    protected int year;
    
    public Vehicle(String make, int year) {
        this.make = make;
        this.year = year;
    }
    
    public void start() {
        System.out.println(make + " is starting...");
    }
    
    public String getInfo() {
        return year + " " + make;
    }
}
```

## Step 2: Extend the Class

```java
public class Step2Extend {
    public static void main(String[] args) {
        Car car = new Car("Toyota", 2024, 4);
        Motorcycle bike = new Motorcycle("Harley", 2023);
        
        car.start();
        System.out.println(car.getInfo());
        car.honk();
        
        bike.start();
        System.out.println(bike.getInfo());
    }
}

// Existing Vehicle class from Step 1...

class Car extends Vehicle {
    private int doors;
    
    public Car(String make, int year, int doors) {
        super(make, year);  // Must call super constructor first
        this.doors = doors;
    }
    
    public void honk() {
        System.out.println(make + " honks: Beep beep!");
    }
    
    @Override
    public String getInfo() {
        return super.getInfo() + " (" + doors + " doors)";
    }
}

class Motorcycle extends Vehicle {
    public Motorcycle(String make, int year) {
        super(make, year);  // Inherits superclass constructor
    }
    
    @Override
    public void start() {
        System.out.println(make + " motorcycle roars to life!");
    }
}
```

## Step 3: Method Overriding with super

```java
public class Step3Override {
    public static void main(String[] args) {
        Manager mgr = new Manager("Alice", 80000, 10000);
        System.out.println(mgr.getDetails());
    }
}

class Employee {
    protected String name;
    protected double salary;
    
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }
    
    public String getDetails() {
        return name + " earns $" + salary;
    }
}

class Manager extends Employee {
    private double bonus;
    
    public Manager(String name, double salary, double bonus) {
        super(name, salary);
        this.bonus = bonus;
    }
    
    @Override
    public String getDetails() {
        return super.getDetails() + " plus bonus of $" + bonus;
    }
}
```

## Step 4: Override Object Methods

```java
import java.util.Objects;

public class Step4Object {
    public static void main(String[] args) {
        Book b1 = new Book("1984", "George Orwell");
        Book b2 = new Book("1984", "George Orwell");
        Book b3 = new Book("Brave New World", "Aldous Huxley");
        
        System.out.println("b1: " + b1);
        System.out.println("b2: " + b2);
        System.out.println("b1 == b2: " + (b1 == b2));
        System.out.println("b1.equals(b2): " + b1.equals(b2));
        System.out.println("b1.equals(b3): " + b1.equals(b3));
        System.out.println("b1.hashCode(): " + b1.hashCode());
        System.out.println("b2.hashCode(): " + b2.hashCode());
    }
}

class Book {
    private String title;
    private String author;
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    
    @Override
    public String toString() {
        return "\"" + title + "\" by " + author;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
}
```

## Step 5: Final Classes and Methods

```java
public class Step5Final {
    public static void main(String[] args) {
        SecurityConfig config = new SecurityConfig();
        config.validate();
    }
}

class BaseConfig {
    public final void validate() {
        System.out.println("Running validation...");
        doValidate();
    }
    
    protected void doValidate() {
        System.out.println("Base validation");
    }
}

class SecurityConfig extends BaseConfig {
    @Override
    protected void doValidate() {
        System.out.println("Security validation");
    }
    // Cannot override validate() — it's final
}
```
