# Lab 05: Inheritance

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner-Intermediate |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building an employee hierarchy system with inheritance |
| **Prerequisites** | Lab 04: OOP Basics |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand inheritance** and class hierarchies
2. **Create parent and child classes** with proper relationships
3. **Override methods** in subclasses
4. **Use super keyword** to access parent functionality
5. **Implement polymorphism** through inheritance
6. **Build an employee hierarchy system** with different employee types

## 📚 Prerequisites

- Lab 04: OOP Basics completed
- Understanding of classes and objects
- Knowledge of encapsulation
- Familiarity with access modifiers

## 🧠 Concept Theory

### 1. Inheritance Basics

Inheritance allows a class to inherit properties and methods from another class:

```java
// Parent class (superclass)
public class Animal {
    private String name;
    private int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    public String getName() {
        return name;
    }
}

// Child class (subclass)
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
    
    public void bark() {
        System.out.println(getName() + " is barking: Woof!");
    }
}

// Usage
Dog dog = new Dog("Buddy", 5, "Golden Retriever");
dog.eat();      // Inherited from Animal
dog.sleep();    // Inherited from Animal
dog.bark();     // Defined in Dog
```

**Key Concepts**:
- **Superclass**: Parent class providing common functionality
- **Subclass**: Child class inheriting from parent
- **extends**: Keyword to establish inheritance
- **super**: Keyword to access parent class members

### 2. Method Overriding

Subclasses can override parent methods:

```java
public class Animal {
    public void makeSound() {
        System.out.println("Some generic sound");
    }
}

public class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}

// Usage
Animal dog = new Dog();
Animal cat = new Cat();

dog.makeSound();  // Woof!
cat.makeSound();  // Meow!
```

**Rules for Overriding**:
- Same method signature
- Same or covariant return type
- Cannot reduce access level
- Cannot throw new checked exceptions
- Use @Override annotation

### 3. The super Keyword

Access parent class members:

```java
public class Vehicle {
    protected String color;
    
    public Vehicle(String color) {
        this.color = color;
    }
    
    public void display() {
        System.out.println("Vehicle color: " + color);
    }
}

public class Car extends Vehicle {
    private int doors;
    
    public Car(String color, int doors) {
        super(color);  // Call parent constructor
        this.doors = doors;
    }
    
    @Override
    public void display() {
        super.display();  // Call parent method
        System.out.println("Doors: " + doors);
    }
}

// Usage
Car car = new Car("Red", 4);
car.display();
// Output:
// Vehicle color: Red
// Doors: 4
```

### 4. Polymorphism

Objects can be treated as instances of their parent class:

```java
public class Shape {
    public void draw() {
        System.out.println("Drawing a shape");
    }
    
    public double getArea() {
        return 0;
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a circle");
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a rectangle");
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
}

// Polymorphic usage
Shape[] shapes = {
    new Circle(5),
    new Rectangle(4, 6),
    new Circle(3)
};

for (Shape shape : shapes) {
    shape.draw();
    System.out.println("Area: " + shape.getArea());
}
```

### 5. Constructor Chaining

Constructors calling parent constructors:

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

public class Employee extends Person {
    private String employeeId;
    private double salary;
    
    public Employee(String name, int age, String employeeId, double salary) {
        super(name, age);  // Call parent constructor
        this.employeeId = employeeId;
        this.salary = salary;
    }
}

public class Manager extends Employee {
    private int teamSize;
    
    public Manager(String name, int age, String employeeId, 
                   double salary, int teamSize) {
        super(name, age, employeeId, salary);  // Call parent constructor
        this.teamSize = teamSize;
    }
}

// Usage
Manager manager = new Manager("John", 40, "EMP001", 80000, 5);
```

### 6. Access Modifiers in Inheritance

Understanding visibility in inheritance:

```java
public class Parent {
    public int publicVar = 1;           // Accessible everywhere
    protected int protectedVar = 2;     // Accessible in subclasses
    private int privateVar = 3;         // NOT accessible in subclasses
    int packageVar = 4;                 // Accessible in same package
}

public class Child extends Parent {
    public void test() {
        System.out.println(publicVar);      // ✅ OK
        System.out.println(protectedVar);   // ✅ OK
        // System.out.println(privateVar);  // ❌ ERROR
        System.out.println(packageVar);     // ✅ OK (same package)
    }
}
```

**Access Levels**:
- **public**: Accessible everywhere
- **protected**: Accessible in subclasses and same package
- **package-private**: Accessible in same package only
- **private**: NOT accessible in subclasses

### 7. Inheritance Hierarchies

Building multi-level inheritance:

```java
// Level 1: Base class
public class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
}

// Level 2: Intermediate class
public class Mammal extends Animal {
    public Mammal(String name) {
        super(name);
    }
    
    public void nurse() {
        System.out.println(name + " is nursing");
    }
}

// Level 3: Concrete class
public class Dog extends Mammal {
    private String breed;
    
    public Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }
    
    public void bark() {
        System.out.println(name + " is barking");
    }
}

// Usage
Dog dog = new Dog("Buddy", "Golden Retriever");
dog.eat();      // From Animal
dog.nurse();    // From Mammal
dog.bark();     // From Dog
```

### 8. Abstract Classes

Classes that cannot be instantiated:

```java
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract method (must be implemented by subclasses)
    public abstract double getArea();
    
    // Concrete method
    public void display() {
        System.out.println("Color: " + color);
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

// Usage
// Shape shape = new Shape("Red");  // ❌ ERROR: Cannot instantiate abstract class
Circle circle = new Circle("Red", 5);  // ✅ OK
System.out.println(circle.getArea());
```

### 9. instanceof Operator

Checking object types:

```java
public class Animal { }
public class Dog extends Animal { }
public class Cat extends Animal { }

// Usage
Animal animal = new Dog();

if (animal instanceof Dog) {
    System.out.println("It's a dog");
    Dog dog = (Dog) animal;  // Safe casting
}

if (animal instanceof Cat) {
    System.out.println("It's a cat");
}

if (animal instanceof Animal) {
    System.out.println("It's an animal");
}
```

### 10. Type Casting

Converting between parent and child types:

```java
public class Animal {
    public void eat() {
        System.out.println("Eating");
    }
}

public class Dog extends Animal {
    public void bark() {
        System.out.println("Barking");
    }
}

// Upcasting (safe)
Dog dog = new Dog();
Animal animal = dog;  // Implicit upcasting
animal.eat();         // ✅ OK

// Downcasting (requires explicit cast)
Animal animal2 = new Dog();
Dog dog2 = (Dog) animal2;  // Explicit downcasting
dog2.bark();               // ✅ OK

// Unsafe downcasting
Animal animal3 = new Animal();
// Dog dog3 = (Dog) animal3;  // ❌ ClassCastException at runtime
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create a Simple Inheritance Hierarchy

**Objective**: Implement basic inheritance with parent and child classes

**Acceptance Criteria**:
- [ ] Parent class defined with properties and methods
- [ ] Child class extends parent
- [ ] Constructor chaining works
- [ ] Methods inherited correctly
- [ ] Code compiles without errors

**Instructions**:
1. Create a `Vehicle` parent class
2. Create `Car` and `Motorcycle` child classes
3. Implement constructors with super
4. Add specific methods to each class
5. Test inheritance

**Code Template**:
```java
package com.learning;

public class Vehicle {
    // TODO: Declare properties
    // TODO: Create constructor
    // TODO: Create methods
}

public class Car extends Vehicle {
    // TODO: Add car-specific properties
    // TODO: Create constructor with super
    // TODO: Add car-specific methods
}
```

### Task 2: Override Methods

**Objective**: Implement method overriding in subclasses

**Acceptance Criteria**:
- [ ] Parent method defined
- [ ] Child methods override parent
- [ ] @Override annotation used
- [ ] Polymorphism works correctly
- [ ] super keyword used appropriately

**Instructions**:
1. Create a `Shape` parent class
2. Create `Circle` and `Square` child classes
3. Override getArea() method
4. Override display() method
5. Test polymorphic behavior

**Code Template**:
```java
package com.learning;

public class Shape {
    // TODO: Declare properties
    // TODO: Create abstract methods
}

public class Circle extends Shape {
    // TODO: Override methods
}
```

### Task 3: Use Abstract Classes

**Objective**: Implement abstract classes and methods

**Acceptance Criteria**:
- [ ] Abstract class defined
- [ ] Abstract methods declared
- [ ] Concrete methods implemented
- [ ] Subclasses implement abstract methods
- [ ] Cannot instantiate abstract class

**Instructions**:
1. Create an abstract `Employee` class
2. Define abstract methods
3. Create `Manager` and `Developer` subclasses
4. Implement abstract methods
5. Test polymorphic behavior

**Code Template**:
```java
package com.learning;

public abstract class Employee {
    // TODO: Declare properties
    // TODO: Define abstract methods
    // TODO: Define concrete methods
}
```

---

## 🎨 Mini-Project: Employee Hierarchy System

### Project Overview

**Description**: Create a comprehensive employee management system with different employee types and inheritance hierarchy.

**Real-World Application**: HR systems, payroll management, organizational structure.

**Learning Value**: Master inheritance, polymorphism, and abstract classes.

### Project Requirements

#### Functional Requirements
- [ ] Create Employee hierarchy
- [ ] Implement different employee types
- [ ] Calculate salaries and bonuses
- [ ] Track employee information
- [ ] Generate reports
- [ ] Calculate department statistics

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Proper encapsulation
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
employee-hierarchy-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Employee.java (abstract)
│   │           ├── Manager.java
│   │           ├── Developer.java
│   │           ├── Designer.java
│   │           ├── Department.java
│   │           ├── EmployeeManager.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── EmployeeTest.java
│               ├── ManagerTest.java
│               └── DeveloperTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Abstract Employee Class

```java
package com.learning;

/**
 * Abstract base class for all employees.
 */
public abstract class Employee {
    protected String employeeId;
    protected String name;
    protected String department;
    protected double baseSalary;
    protected String hireDate;
    
    /**
     * Constructor for Employee.
     */
    public Employee(String employeeId, String name, String department, 
                    double baseSalary) {
        this.employeeId = employeeId;
        setName(name);
        setDepartment(department);
        setBaseSalary(baseSalary);
        this.hireDate = java.time.LocalDate.now().toString();
    }
    
    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public double getBaseSalary() {
        return baseSalary;
    }
    
    public String getHireDate() {
        return hireDate;
    }
    
    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.department = department;
    }
    
    public void setBaseSalary(double baseSalary) {
        if (baseSalary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.baseSalary = baseSalary;
    }
    
    /**
     * Abstract method to calculate salary.
     */
    public abstract double calculateSalary();
    
    /**
     * Abstract method to get job title.
     */
    public abstract String getJobTitle();
    
    /**
     * Concrete method to display employee info.
     */
    public void displayInfo() {
        System.out.println("\n=== Employee Information ===");
        System.out.println("ID: " + employeeId);
        System.out.println("Name: " + name);
        System.out.println("Title: " + getJobTitle());
        System.out.println("Department: " + department);
        System.out.println("Base Salary: $" + String.format("%.2f", baseSalary));
        System.out.println("Total Salary: $" + String.format("%.2f", calculateSalary()));
        System.out.println("Hire Date: " + hireDate);
    }
    
    /**
     * Concrete method to give raise.
     */
    public void giveRaise(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        baseSalary += baseSalary * (percentage / 100);
    }
    
    @Override
    public String toString() {
        return getJobTitle() + "{" +
                "id='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", salary=" + String.format("%.2f", calculateSalary()) +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        return employeeId.equals(other.employeeId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(employeeId);
    }
}
```

#### Step 2: Create Manager Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class extending Employee.
 */
public class Manager extends Employee {
    private int teamSize;
    private double bonusPercentage;
    private List<Employee> team;
    
    /**
     * Constructor for Manager.
     */
    public Manager(String employeeId, String name, String department, 
                   double baseSalary, int teamSize) {
        super(employeeId, name, department, baseSalary);
        setTeamSize(teamSize);
        this.bonusPercentage = 15.0;  // 15% bonus
        this.team = new ArrayList<>();
    }
    
    // Getters
    public int getTeamSize() {
        return teamSize;
    }
    
    public double getBonusPercentage() {
        return bonusPercentage;
    }
    
    public List<Employee> getTeam() {
        return new ArrayList<>(team);
    }
    
    // Setters
    public void setTeamSize(int teamSize) {
        if (teamSize < 0) {
            throw new IllegalArgumentException("Team size cannot be negative");
        }
        this.teamSize = teamSize;
    }
    
    public void setBonusPercentage(double bonusPercentage) {
        if (bonusPercentage < 0 || bonusPercentage > 100) {
            throw new IllegalArgumentException("Bonus must be between 0 and 100");
        }
        this.bonusPercentage = bonusPercentage;
    }
    
    /**
     * Add employee to team.
     */
    public void addTeamMember(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        team.add(employee);
    }
    
    /**
     * Calculate manager salary with bonus.
     */
    @Override
    public double calculateSalary() {
        double bonus = baseSalary * (bonusPercentage / 100);
        return baseSalary + bonus;
    }
    
    /**
     * Get job title.
     */
    @Override
    public String getJobTitle() {
        return "Manager";
    }
    
    /**
     * Display manager info.
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Team Size: " + teamSize);
        System.out.println("Bonus: " + bonusPercentage + "%");
    }
}
```

#### Step 3: Create Developer Class

```java
package com.learning;

/**
 * Developer class extending Employee.
 */
public class Developer extends Employee {
    private String programmingLanguage;
    private int yearsOfExperience;
    private double bonusPercentage;
    
    /**
     * Constructor for Developer.
     */
    public Developer(String employeeId, String name, String department, 
                     double baseSalary, String programmingLanguage, 
                     int yearsOfExperience) {
        super(employeeId, name, department, baseSalary);
        setProgrammingLanguage(programmingLanguage);
        setYearsOfExperience(yearsOfExperience);
        this.bonusPercentage = 10.0;  // 10% base bonus
    }
    
    // Getters
    public String getProgrammingLanguage() {
        return programmingLanguage;
    }
    
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public double getBonusPercentage() {
        return bonusPercentage;
    }
    
    // Setters
    public void setProgrammingLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be empty");
        }
        this.programmingLanguage = language;
    }
    
    public void setYearsOfExperience(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative");
        }
        this.yearsOfExperience = years;
    }
    
    /**
     * Calculate developer salary with experience bonus.
     */
    @Override
    public double calculateSalary() {
        // Bonus increases with experience
        double experienceBonus = yearsOfExperience * 2.0;  // 2% per year
        double totalBonus = bonusPercentage + experienceBonus;
        return baseSalary + (baseSalary * (totalBonus / 100));
    }
    
    /**
     * Get job title.
     */
    @Override
    public String getJobTitle() {
        return "Developer";
    }
    
    /**
     * Display developer info.
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Language: " + programmingLanguage);
        System.out.println("Experience: " + yearsOfExperience + " years");
    }
}
```

#### Step 4: Create Designer Class

```java
package com.learning;

/**
 * Designer class extending Employee.
 */
public class Designer extends Employee {
    private String designSpecialty;
    private int projectsCompleted;
    private double bonusPercentage;
    
    /**
     * Constructor for Designer.
     */
    public Designer(String employeeId, String name, String department, 
                    double baseSalary, String designSpecialty, 
                    int projectsCompleted) {
        super(employeeId, name, department, baseSalary);
        setDesignSpecialty(designSpecialty);
        setProjectsCompleted(projectsCompleted);
        this.bonusPercentage = 8.0;  // 8% base bonus
    }
    
    // Getters
    public String getDesignSpecialty() {
        return designSpecialty;
    }
    
    public int getProjectsCompleted() {
        return projectsCompleted;
    }
    
    public double getBonusPercentage() {
        return bonusPercentage;
    }
    
    // Setters
    public void setDesignSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be empty");
        }
        this.designSpecialty = specialty;
    }
    
    public void setProjectsCompleted(int projects) {
        if (projects < 0) {
            throw new IllegalArgumentException("Projects cannot be negative");
        }
        this.projectsCompleted = projects;
    }
    
    /**
     * Calculate designer salary with project bonus.
     */
    @Override
    public double calculateSalary() {
        // Bonus increases with projects
        double projectBonus = projectsCompleted * 0.5;  // 0.5% per project
        double totalBonus = bonusPercentage + projectBonus;
        return baseSalary + (baseSalary * (totalBonus / 100));
    }
    
    /**
     * Get job title.
     */
    @Override
    public String getJobTitle() {
        return "Designer";
    }
    
    /**
     * Display designer info.
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Specialty: " + designSpecialty);
        System.out.println("Projects Completed: " + projectsCompleted);
    }
}
```

#### Step 5: Create Department Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Department class managing employees.
 */
public class Department {
    private String departmentName;
    private String manager;
    private List<Employee> employees;
    
    /**
     * Constructor for Department.
     */
    public Department(String departmentName, String manager) {
        this.departmentName = departmentName;
        this.manager = manager;
        this.employees = new ArrayList<>();
    }
    
    // Getters
    public String getDepartmentName() {
        return departmentName;
    }
    
    public String getManager() {
        return manager;
    }
    
    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }
    
    /**
     * Add employee to department.
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employees.add(employee);
    }
    
    /**
     * Calculate total payroll.
     */
    public double calculateTotalPayroll() {
        double total = 0;
        for (Employee emp : employees) {
            total += emp.calculateSalary();
        }
        return total;
    }
    
    /**
     * Calculate average salary.
     */
    public double calculateAverageSalary() {
        if (employees.isEmpty()) {
            return 0;
        }
        return calculateTotalPayroll() / employees.size();
    }
    
    /**
     * Display department info.
     */
    public void displayInfo() {
        System.out.println("\n=== Department: " + departmentName + " ===");
        System.out.println("Manager: " + manager);
        System.out.println("Employees: " + employees.size());
        System.out.println("Total Payroll: $" + String.format("%.2f", calculateTotalPayroll()));
        System.out.println("Average Salary: $" + String.format("%.2f", calculateAverageSalary()));
    }
}
```

#### Step 6: Create EmployeeManager Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages employees and departments.
 */
public class EmployeeManager {
    private List<Employee> employees;
    private List<Department> departments;
    
    public EmployeeManager() {
        this.employees = new ArrayList<>();
        this.departments = new ArrayList<>();
    }
    
    /**
     * Add employee.
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employees.add(employee);
    }
    
    /**
     * Add department.
     */
    public void addDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        departments.add(department);
    }
    
    /**
     * Find employee by ID.
     */
    public Employee findEmployee(String id) {
        for (Employee emp : employees) {
            if (emp.getEmployeeId().equals(id)) {
                return emp;
            }
        }
        return null;
    }
    
    /**
     * Calculate total company payroll.
     */
    public double calculateTotalPayroll() {
        double total = 0;
        for (Employee emp : employees) {
            total += emp.calculateSalary();
        }
        return total;
    }
    
    /**
     * Display all employees.
     */
    public void displayAllEmployees() {
        System.out.println("\n=== All Employees ===");
        for (Employee emp : employees) {
            emp.displayInfo();
        }
    }
    
    /**
     * Display all departments.
     */
    public void displayAllDepartments() {
        System.out.println("\n=== All Departments ===");
        for (Department dept : departments) {
            dept.displayInfo();
        }
    }
    
    /**
     * Get employee count by type.
     */
    public void displayEmployeeStats() {
        int managers = 0, developers = 0, designers = 0;
        
        for (Employee emp : employees) {
            if (emp instanceof Manager) managers++;
            else if (emp instanceof Developer) developers++;
            else if (emp instanceof Designer) designers++;
        }
        
        System.out.println("\n=== Employee Statistics ===");
        System.out.println("Total Employees: " + employees.size());
        System.out.println("Managers: " + managers);
        System.out.println("Developers: " + developers);
        System.out.println("Designers: " + designers);
        System.out.println("Total Payroll: $" + String.format("%.2f", calculateTotalPayroll()));
    }
}
```

#### Step 7: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Employee Hierarchy System.
 */
public class Main {
    public static void main(String[] args) {
        // Create manager
        EmployeeManager manager = new EmployeeManager();
        
        // Create employees
        Manager m1 = new Manager("EMP001", "Alice Johnson", "Engineering", 100000, 5);
        Developer d1 = new Developer("EMP002", "Bob Smith", "Engineering", 80000, "Java", 5);
        Developer d2 = new Developer("EMP003", "Carol White", "Engineering", 75000, "Python", 3);
        Designer des1 = new Designer("EMP004", "David Brown", "Design", 70000, "UI/UX", 10);
        
        Manager m2 = new Manager("EMP005", "Eve Davis", "Sales", 95000, 3);
        
        // Add employees
        manager.addEmployee(m1);
        manager.addEmployee(d1);
        manager.addEmployee(d2);
        manager.addEmployee(des1);
        manager.addEmployee(m2);
        
        // Create departments
        Department engineering = new Department("Engineering", "Alice Johnson");
        engineering.addEmployee(d1);
        engineering.addEmployee(d2);
        
        Department design = new Department("Design", "Alice Johnson");
        design.addEmployee(des1);
        
        Department sales = new Department("Sales", "Eve Davis");
        
        manager.addDepartment(engineering);
        manager.addDepartment(design);
        manager.addDepartment(sales);
        
        // Display information
        manager.displayAllEmployees();
        manager.displayAllDepartments();
        manager.displayEmployeeStats();
        
        // Give raises
        System.out.println("\n=== After 10% Raise ===");
        d1.giveRaise(10);
        d1.displayInfo();
    }
}
```

#### Step 8: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Employee hierarchy.
 */
public class EmployeeTest {
    
    private Manager manager;
    private Developer developer;
    private Designer designer;
    
    @BeforeEach
    void setUp() {
        manager = new Manager("EMP001", "John", "Engineering", 100000, 5);
        developer = new Developer("EMP002", "Jane", "Engineering", 80000, "Java", 5);
        designer = new Designer("EMP003", "Bob", "Design", 70000, "UI/UX", 10);
    }
    
    @Test
    void testManagerSalaryCalculation() {
        double expected = 100000 + (100000 * 0.15);  // Base + 15% bonus
        assertEquals(expected, manager.calculateSalary());
    }
    
    @Test
    void testDeveloperSalaryCalculation() {
        // Base + 10% + (5 years * 2%)
        double expected = 80000 + (80000 * 0.20);
        assertEquals(expected, developer.calculateSalary());
    }
    
    @Test
    void testDesignerSalaryCalculation() {
        // Base + 8% + (10 projects * 0.5%)
        double expected = 70000 + (70000 * 0.13);
        assertEquals(expected, designer.calculateSalary());
    }
    
    @Test
    void testGiveRaise() {
        double originalSalary = developer.getBaseSalary();
        developer.giveRaise(10);
        assertEquals(originalSalary * 1.1, developer.getBaseSalary());
    }
    
    @Test
    void testInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.setName("");
        });
    }
    
    @Test
    void testPolymorphism() {
        Employee[] employees = {manager, developer, designer};
        
        for (Employee emp : employees) {
            assertNotNull(emp.getJobTitle());
            assertTrue(emp.calculateSalary() > 0);
        }
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Vehicle Rental System

**Objective**: Practice inheritance with different vehicle types

**Task Description**:
Create a Vehicle hierarchy with Car, Truck, and Motorcycle classes.

**Acceptance Criteria**:
- [ ] Abstract Vehicle class
- [ ] Concrete vehicle types
- [ ] Rental calculation
- [ ] Damage assessment
- [ ] Rental history

### Exercise 2: Bank Account Hierarchy

**Objective**: Practice inheritance with different account types

**Task Description**:
Create account types: Savings, Checking, Money Market

**Acceptance Criteria**:
- [ ] Abstract Account class
- [ ] Different account types
- [ ] Interest calculation
- [ ] Fee calculation
- [ ] Account statements

### Exercise 3: Shape Hierarchy

**Objective**: Practice polymorphism with shapes

**Task Description**:
Create shapes: Circle, Rectangle, Triangle with area and perimeter

**Acceptance Criteria**:
- [ ] Abstract Shape class
- [ ] Concrete shapes
- [ ] Area calculation
- [ ] Perimeter calculation
- [ ] Shape comparison

---

## 🧪 Quiz

### Question 1: What is inheritance?

A) Creating multiple objects  
B) A class inheriting properties from another class  
C) Overloading methods  
D) Encapsulation  

**Answer**: B) A class inheriting properties from another class

### Question 2: What keyword is used for inheritance?

A) implements  
B) extends  
C) inherits  
D) super  

**Answer**: B) extends

### Question 3: What is method overriding?

A) Calling parent method  
B) Redefining parent method in child class  
C) Creating multiple methods  
D) Hiding variables  

**Answer**: B) Redefining parent method in child class

### Question 4: Can you instantiate an abstract class?

A) Yes  
B) No  
C) Only with super  
D) Only in main  

**Answer**: B) No

### Question 5: What does instanceof do?

A) Creates an instance  
B) Checks object type  
C) Casts objects  
D) Compares objects  

**Answer**: B) Checks object type

---

## 🚀 Advanced Challenge

### Challenge: Complete HR Management System

**Difficulty**: Intermediate

**Objective**: Build a comprehensive HR system with inheritance

**Requirements**:
- [ ] Multiple employee types
- [ ] Salary management
- [ ] Performance tracking
- [ ] Leave management
- [ ] Promotion system

---

## 🏆 Best Practices

### Inheritance Design

1. **Use IS-A Relationship**
   - Only inherit if child IS-A parent
   - Don't use inheritance for code reuse alone

2. **Favor Composition Over Inheritance**
   - Use composition when appropriate
   - Avoid deep inheritance hierarchies

3. **Abstract Classes**
   - Use for common functionality
   - Define contracts with abstract methods

---

## 🔗 Next Steps

**Next Lab**: [Lab 06: Interfaces](../06-interfaces/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built employee hierarchy system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 05! 🎉**

You've mastered inheritance and polymorphism. Ready for interfaces? Move on to [Lab 06: Interfaces](../06-interfaces/README.md).