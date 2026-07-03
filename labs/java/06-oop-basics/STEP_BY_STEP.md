# OOP Basics — Step-by-Step Tutorial

## Step 1: Define a Class

```java
public class Step1Class {
    public static void main(String[] args) {
        // Create an object
        Student alice = new Student();
        alice.name = "Alice";
        alice.age = 20;
        alice.grade = "A";
        
        System.out.println(alice.name + " is " + alice.age + " and got " + alice.grade);
    }
}

class Student {
    String name;
    int age;
    String grade;
}
```

## Step 2: Add Constructor

```java
public class Step2Constructor {
    public static void main(String[] args) {
        Student2 alice = new Student2("Alice", 20, "A");
        Student2 bob = new Student2("Bob", 22, "B");
        
        System.out.println(alice.name + " (age " + alice.age + ") got " + alice.grade);
        System.out.println(bob.name + " (age " + bob.age + ") got " + bob.grade);
    }
}

class Student2 {
    String name;
    int age;
    String grade;
    
    // Constructor
    public Student2(String name, int age, String grade) {
        this.name = name;
        this.age = age;
        this.grade = grade;
    }
}
```

## Step 3: Encapsulation (Private Fields + Getters/Setters)

```java
public class Step3Encapsulation {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("Alice", 1000);
        
        System.out.println("Owner: " + account.getOwner());
        System.out.println("Balance: $" + account.getBalance());
        
        account.deposit(500);
        account.withdraw(200);
        
        System.out.println("Final balance: $" + account.getBalance());
    }
}

class BankAccount {
    private String owner;
    private double balance;
    
    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
    }
    
    public String getOwner() { return owner; }
    public double getBalance() { return balance; }
    
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) balance -= amount;
    }
}
```

## Step 4: Static Members

```java
public class Step4Static {
    public static void main(String[] args) {
        // Static method called on class, not instance
        int max = Math.max(10, 20);
        int min = Math.min(10, 20);
        System.out.println("Max: " + max + ", Min: " + min);
        
        // Static variable
        Employee e1 = new Employee("Alice");
        Employee e2 = new Employee("Bob");
        Employee e3 = new Employee("Carol");
        
        System.out.println("Total employees: " + Employee.getCount());
        System.out.println("e1 id: " + e1.getId());
        System.out.println("e3 id: " + e3.getId());
    }
}

class Employee {
    private static int nextId = 1;
    private static int count = 0;
    
    private String name;
    private int id;
    
    public Employee(String name) {
        this.name = name;
        this.id = nextId++;
        count++;
    }
    
    public int getId() { return id; }
    public static int getCount() { return count; }
}
```

## Step 5: Constructor Overloading

```java
public class Step5Overload {
    public static void main(String[] args) {
        Rectangle r1 = new Rectangle();              // default
        Rectangle r2 = new Rectangle(5);             // square
        Rectangle r3 = new Rectangle(4, 6);          // custom
        
        System.out.println("r1 area: " + r1.getArea());
        System.out.println("r2 area: " + r2.getArea());
        System.out.println("r3 area: " + r3.getArea());
    }
}

class Rectangle {
    private double width;
    private double height;
    
    public Rectangle() {
        this(1, 1);  // Call the 2-param constructor
    }
    
    public Rectangle(double side) {
        this(side, side);  // Square
    }
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getArea() { return width * height; }
}
```

## Step 6: Build a Complete Class

```java
public class Step6Complete {
    public static void main(String[] args) {
        Circle c1 = new Circle("Red", 5.0);
        Circle c2 = new Circle("Blue", 3.0);
        
        System.out.println(c1.describe());
        System.out.println(c2.describe());
        
        System.out.println("Total circles created: " + Circle.getObjectCount());
        
        // Fluent API
        c1.setColor("Green").setRadius(10.0);
        System.out.println("Updated: " + c1.describe());
    }
}

class Circle {
    private static int objectCount = 0;
    
    private String color;
    private double radius;
    
    public Circle(String color, double radius) {
        this.color = color;
        this.radius = radius;
        objectCount++;
    }
    
    public static int getObjectCount() { return objectCount; }
    
    public String describe() {
        return String.format("%s circle (radius=%.1f, area=%.2f, circumference=%.2f)",
            color, radius, getArea(), getCircumference());
    }
    
    public double getArea() { return Math.PI * radius * radius; }
    public double getCircumference() { return 2 * Math.PI * radius; }
    
    // Fluent setters
    public Circle setColor(String color) { this.color = color; return this; }
    public Circle setRadius(double radius) { this.radius = radius; return this; }
}
```
