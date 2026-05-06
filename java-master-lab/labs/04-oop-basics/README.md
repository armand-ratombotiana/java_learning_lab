# Lab 04: OOP Basics

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a student management system with object-oriented design |
| **Prerequisites** | Lab 03: Methods & Scope |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Design and implement classes** with proper encapsulation
2. **Create and use objects** effectively in Java programs
3. **Implement constructors** (default, parameterized, copy)
4. **Use access modifiers** to control visibility
5. **Build a complete student management system** with OOP principles

## 📚 Prerequisites

- Lab 03: Methods & Scope completed
- Understanding of methods and variable scope
- Basic Java syntax knowledge
- Familiarity with control flow

## 🧠 Concept Theory

### 1. Classes and Objects

A class is a blueprint for creating objects. Objects are instances of classes:

```java
// Class definition
public class Student {
    // Class members (variables and methods)
    private String name;
    private int age;
    
    // Constructor
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Method
    public void displayInfo() {
        System.out.println("Name: " + name + ", Age: " + age);
    }
}

// Creating objects (instances)
Student student1 = new Student("John", 20);
Student student2 = new Student("Jane", 21);

// Using objects
student1.displayInfo();  // Name: John, Age: 20
student2.displayInfo();  // Name: Jane, Age: 21
```

**Key Concepts**:
- **Class**: Template for objects
- **Object**: Instance of a class
- **State**: Object's data (variables)
- **Behavior**: Object's actions (methods)

### 2. Instance Variables and Methods

Instance variables belong to objects, not the class:

```java
public class Car {
    // Instance variables (each object has its own)
    private String color;
    private String model;
    private int year;
    
    // Constructor
    public Car(String color, String model, int year) {
        this.color = color;
        this.model = model;
        this.year = year;
    }
    
    // Instance method (can access instance variables)
    public void displayInfo() {
        System.out.println(year + " " + color + " " + model);
    }
    
    // Instance method that modifies state
    public void repaint(String newColor) {
        this.color = newColor;
    }
}

// Each object has its own state
Car car1 = new Car("Red", "Toyota", 2020);
Car car2 = new Car("Blue", "Honda", 2021);

car1.displayInfo();  // 2020 Red Toyota
car2.displayInfo();  // 2021 Blue Honda

car1.repaint("Green");
car1.displayInfo();  // 2020 Green Toyota
car2.displayInfo();  // 2021 Blue Honda (unchanged)
```

### 3. Constructors

Constructors initialize objects when they're created:

```java
public class Person {
    private String name;
    private int age;
    private String email;
    
    // Default constructor (no parameters)
    public Person() {
        this.name = "Unknown";
        this.age = 0;
        this.email = "unknown@example.com";
    }
    
    // Parameterized constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        this.email = "unknown@example.com";
    }
    
    // Full constructor
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    // Copy constructor
    public Person(Person other) {
        this.name = other.name;
        this.age = other.age;
        this.email = other.email;
    }
}

// Using different constructors
Person p1 = new Person();                           // Default
Person p2 = new Person("John", 25);                 // Parameterized
Person p3 = new Person("Jane", 30, "jane@example.com"); // Full
Person p4 = new Person(p3);                         // Copy
```

**Constructor Rules**:
- Same name as class
- No return type
- Called when object is created
- Can be overloaded
- If not defined, default constructor is provided

### 4. Access Modifiers

Control visibility of class members:

```java
public class BankAccount {
    // Public: accessible from anywhere
    public String accountNumber;
    
    // Private: accessible only within this class
    private double balance;
    
    // Protected: accessible within package and subclasses
    protected String accountType;
    
    // Package-private (default): accessible within package
    String creationDate;
    
    // Constructor
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.accountType = "Savings";
        this.creationDate = new java.time.LocalDate.now().toString();
    }
    
    // Public method to access private variable
    public double getBalance() {
        return balance;
    }
    
    // Public method to modify private variable
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    // Private method (helper method)
    private void logTransaction(String type, double amount) {
        System.out.println(type + ": " + amount);
    }
}

// Usage
BankAccount account = new BankAccount("123456", 1000);
System.out.println(account.getBalance());  // 1000
account.deposit(500);
System.out.println(account.getBalance());  // 1500
// account.balance = 2000;  // ERROR: balance is private
```

**Access Modifier Levels**:
1. **public**: Accessible everywhere
2. **protected**: Accessible in package and subclasses
3. **package-private** (default): Accessible in package only
4. **private**: Accessible only in class

### 5. Encapsulation

Hide internal details and provide controlled access:

```java
public class Student {
    // Private variables (hidden from outside)
    private String name;
    private int age;
    private double gpa;
    
    // Constructor
    public Student(String name, int age, double gpa) {
        setName(name);      // Use setter for validation
        setAge(age);
        setGPA(gpa);
    }
    
    // Getter for name
    public String getName() {
        return name;
    }
    
    // Setter for name with validation
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
    
    // Getter for age
    public int getAge() {
        return age;
    }
    
    // Setter for age with validation
    public void setAge(int age) {
        if (age > 0 && age < 150) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be between 1 and 149");
        }
    }
    
    // Getter for GPA
    public double getGPA() {
        return gpa;
    }
    
    // Setter for GPA with validation
    public void setGPA(double gpa) {
        if (gpa >= 0.0 && gpa <= 4.0) {
            this.gpa = gpa;
        } else {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
        }
    }
    
    // Business method
    public String getGradeLevel() {
        if (gpa >= 3.5) return "Excellent";
        if (gpa >= 3.0) return "Good";
        if (gpa >= 2.0) return "Average";
        return "Poor";
    }
}

// Usage with validation
Student student = new Student("John", 20, 3.8);
System.out.println(student.getGradeLevel());  // Excellent

student.setGPA(3.2);
System.out.println(student.getGradeLevel());  // Good

// student.setGPA(5.0);  // ERROR: Invalid GPA
```

**Benefits of Encapsulation**:
- Data protection
- Validation of input
- Flexibility to change implementation
- Control over object state
- Reduced coupling

### 6. The this Keyword

Refers to the current object:

```java
public class Rectangle {
    private double width;
    private double height;
    
    // Constructor using this
    public Rectangle(double width, double height) {
        this.width = width;    // this.width refers to instance variable
        this.height = height;  // this.height refers to instance variable
    }
    
    // Method using this
    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // Method returning this (for method chaining)
    public Rectangle scale(double factor) {
        this.width *= factor;
        this.height *= factor;
        return this;  // Return current object
    }
    
    // Method calling another method using this
    public double getArea() {
        return calculateArea();  // Implicitly uses this
    }
    
    private double calculateArea() {
        return this.width * this.height;
    }
}

// Usage
Rectangle rect = new Rectangle(5, 10);
rect.scale(2).scale(1.5);  // Method chaining
System.out.println(rect.getArea());  // 150
```

### 7. Static Variables and Methods

Belong to the class, not instances:

```java
public class Student {
    // Static variable (shared by all instances)
    private static int totalStudents = 0;
    
    // Instance variables
    private String name;
    private int id;
    
    // Constructor
    public Student(String name) {
        this.name = name;
        this.id = ++totalStudents;  // Increment shared counter
    }
    
    // Static method (can access static variables)
    public static int getTotalStudents() {
        return totalStudents;
    }
    
    // Instance method
    public int getID() {
        return id;
    }
    
    // Static method cannot access instance variables
    // public static void displayName() {
    //     System.out.println(name);  // ERROR: Cannot access instance variable
    // }
}

// Usage
Student s1 = new Student("John");
System.out.println(s1.getID());                    // 1
System.out.println(Student.getTotalStudents());    // 1

Student s2 = new Student("Jane");
System.out.println(s2.getID());                    // 2
System.out.println(Student.getTotalStudents());    // 2

Student s3 = new Student("Bob");
System.out.println(Student.getTotalStudents());    // 3
```

**Static vs Instance**:
- **Static**: Shared by all instances, accessed via class name
- **Instance**: Unique to each object, accessed via object reference

### 8. Object Equality

Comparing objects:

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Override equals method
    @Override
    public boolean equals(Object obj) {
        // Check if same object
        if (this == obj) return true;
        
        // Check if null or different class
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Cast and compare fields
        Person other = (Person) obj;
        return name.equals(other.name) && age == other.age;
    }
    
    // Override hashCode (required when overriding equals)
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, age);
    }
    
    // Override toString
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

// Usage
Person p1 = new Person("John", 25);
Person p2 = new Person("John", 25);
Person p3 = new Person("Jane", 30);

System.out.println(p1 == p2);           // false (different objects)
System.out.println(p1.equals(p2));      // true (same content)
System.out.println(p1.equals(p3));      // false (different content)
System.out.println(p1.toString());      // Person{name='John', age=25}
```

### 9. Immutable Objects

Objects that cannot be changed after creation:

```java
public final class ImmutablePoint {
    private final int x;
    private final int y;
    
    // Constructor
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // Getters only (no setters)
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    // Methods that return new objects
    public ImmutablePoint translate(int dx, int dy) {
        return new ImmutablePoint(x + dx, y + dy);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

// Usage
ImmutablePoint p1 = new ImmutablePoint(0, 0);
ImmutablePoint p2 = p1.translate(5, 10);

System.out.println(p1);  // (0, 0) - unchanged
System.out.println(p2);  // (5, 10) - new object
```

### 10. Object Composition

Using objects within other objects:

```java
public class Address {
    private String street;
    private String city;
    private String zipCode;
    
    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    @Override
    public String toString() {
        return street + ", " + city + " " + zipCode;
    }
}

public class Person {
    private String name;
    private Address address;  // Composition
    
    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
    }
}

// Usage
Address addr = new Address("123 Main St", "New York", "10001");
Person person = new Person("John", addr);
person.displayInfo();
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create a Basic Class

**Objective**: Define a class with instance variables, constructor, and methods

**Acceptance Criteria**:
- [ ] Class defined with proper structure
- [ ] Instance variables declared
- [ ] Constructor implemented
- [ ] Methods work correctly
- [ ] Code compiles without errors

**Instructions**:
1. Create a `Book` class
2. Add instance variables (title, author, pages, price)
3. Create a parameterized constructor
4. Add getter methods
5. Add a method to display book information

**Code Template**:
```java
package com.learning;

public class Book {
    // TODO: Declare instance variables
    
    // TODO: Create constructor
    
    // TODO: Create getter methods
    
    // TODO: Create display method
}
```

**Expected Output**:
```
Title: Java Programming
Author: John Doe
Pages: 500
Price: $49.99
```

### Task 2: Implement Encapsulation

**Objective**: Use access modifiers and validation

**Acceptance Criteria**:
- [ ] Private instance variables
- [ ] Public getter/setter methods
- [ ] Input validation in setters
- [ ] Proper error handling
- [ ] Data protection

**Instructions**:
1. Create a `BankAccount` class
2. Make variables private
3. Implement getters and setters
4. Add validation for balance
5. Add deposit and withdraw methods

**Code Template**:
```java
package com.learning;

public class BankAccount {
    // TODO: Declare private variables
    
    // TODO: Create constructor
    
    // TODO: Create getters and setters with validation
    
    // TODO: Create deposit and withdraw methods
}
```

**Expected Output**:
```
Account: 123456
Balance: $1000.00
After deposit: $1500.00
After withdrawal: $1200.00
```

### Task 3: Use Static Members

**Objective**: Understand class-level variables and methods

**Acceptance Criteria**:
- [ ] Static variable tracks count
- [ ] Static method returns count
- [ ] Each object has unique ID
- [ ] Counter increments correctly
- [ ] Static access works properly

**Instructions**:
1. Create an `Employee` class
2. Add static variable for employee count
3. Add instance variable for employee ID
4. Increment count in constructor
5. Create static method to get total employees

**Code Template**:
```java
package com.learning;

public class Employee {
    // TODO: Declare static variable
    // TODO: Declare instance variables
    
    // TODO: Create constructor
    
    // TODO: Create static method
    
    // TODO: Create instance methods
}
```

---

## 🎨 Mini-Project: Student Management System

### Project Overview

**Description**: Create a comprehensive student management system with classes for students, courses, and enrollment tracking.

**Real-World Application**: Foundation for educational management systems, registration systems, and student information systems.

**Learning Value**: Master OOP principles including classes, objects, encapsulation, and composition.

### Project Requirements

#### Functional Requirements
- [ ] Create Student class with properties
- [ ] Create Course class with properties
- [ ] Implement enrollment system
- [ ] Track student grades
- [ ] Calculate GPA
- [ ] Display student information
- [ ] Display course information
- [ ] Generate reports

#### Non-Functional Requirements
- [ ] Clean, well-organized code
- [ ] Proper encapsulation
- [ ] Input validation
- [ ] Comprehensive documentation
- [ ] Unit tests

### Project Structure

```
student-management-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Student.java
│   │           ├── Course.java
│   │           ├── Enrollment.java
│   │           ├── StudentManager.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── StudentTest.java
│               ├── CourseTest.java
│               └── EnrollmentTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Student Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student in the system.
 */
public class Student {
    private static int nextId = 1000;
    
    private int id;
    private String name;
    private String email;
    private int age;
    private List<Enrollment> enrollments;
    
    /**
     * Constructor for Student.
     *
     * @param name student's name
     * @param email student's email
     * @param age student's age
     */
    public Student(String name, String email, int age) {
        this.id = nextId++;
        setName(name);
        setEmail(email);
        setAge(age);
        this.enrollments = new ArrayList<>();
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public int getAge() {
        return age;
    }
    
    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
    }
    
    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public void setAge(int age) {
        if (age < 5 || age > 100) {
            throw new IllegalArgumentException("Age must be between 5 and 100");
        }
        this.age = age;
    }
    
    /**
     * Enrolls student in a course.
     */
    public void enrollInCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        // Check if already enrolled
        for (Enrollment e : enrollments) {
            if (e.getCourse().equals(course)) {
                throw new IllegalArgumentException("Already enrolled in this course");
            }
        }
        
        Enrollment enrollment = new Enrollment(this, course);
        enrollments.add(enrollment);
    }
    
    /**
     * Calculates student's GPA.
     */
    public double calculateGPA() {
        if (enrollments.isEmpty()) {
            return 0.0;
        }
        
        double totalGrade = 0;
        for (Enrollment e : enrollments) {
            totalGrade += e.getGrade();
        }
        
        return totalGrade / enrollments.size();
    }
    
    /**
     * Gets grade level based on GPA.
     */
    public String getGradeLevel() {
        double gpa = calculateGPA();
        if (gpa >= 3.5) return "Excellent";
        if (gpa >= 3.0) return "Good";
        if (gpa >= 2.0) return "Average";
        return "Poor";
    }
    
    /**
     * Displays student information.
     */
    public void displayInfo() {
        System.out.println("\n=== Student Information ===");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Age: " + age);
        System.out.println("GPA: " + String.format("%.2f", calculateGPA()));
        System.out.println("Grade Level: " + getGradeLevel());
        System.out.println("Enrolled Courses: " + enrollments.size());
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gpa=" + String.format("%.2f", calculateGPA()) +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student other = (Student) obj;
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
```

#### Step 2: Create Course Class

```java
package com.learning;

/**
 * Represents a course in the system.
 */
public class Course {
    private String courseCode;
    private String courseName;
    private String instructor;
    private int credits;
    private int maxStudents;
    private int enrolledStudents;
    
    /**
     * Constructor for Course.
     */
    public Course(String courseCode, String courseName, String instructor, 
                  int credits, int maxStudents) {
        setCourseCode(courseCode);
        setCourseName(courseName);
        setInstructor(instructor);
        setCredits(credits);
        setMaxStudents(maxStudents);
        this.enrolledStudents = 0;
    }
    
    // Getters
    public String getCourseCode() {
        return courseCode;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public int getCredits() {
        return credits;
    }
    
    public int getMaxStudents() {
        return maxStudents;
    }
    
    public int getEnrolledStudents() {
        return enrolledStudents;
    }
    
    // Setters with validation
    public void setCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        this.courseCode = courseCode;
    }
    
    public void setCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        this.courseName = courseName;
    }
    
    public void setInstructor(String instructor) {
        if (instructor == null || instructor.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor cannot be empty");
        }
        this.instructor = instructor;
    }
    
    public void setCredits(int credits) {
        if (credits < 1 || credits > 4) {
            throw new IllegalArgumentException("Credits must be between 1 and 4");
        }
        this.credits = credits;
    }
    
    public void setMaxStudents(int maxStudents) {
        if (maxStudents < 1) {
            throw new IllegalArgumentException("Max students must be at least 1");
        }
        this.maxStudents = maxStudents;
    }
    
    /**
     * Checks if course has available seats.
     */
    public boolean hasAvailableSeats() {
        return enrolledStudents < maxStudents;
    }
    
    /**
     * Increments enrolled student count.
     */
    public void addStudent() {
        if (!hasAvailableSeats()) {
            throw new IllegalStateException("Course is full");
        }
        enrolledStudents++;
    }
    
    /**
     * Displays course information.
     */
    public void displayInfo() {
        System.out.println("\n=== Course Information ===");
        System.out.println("Code: " + courseCode);
        System.out.println("Name: " + courseName);
        System.out.println("Instructor: " + instructor);
        System.out.println("Credits: " + credits);
        System.out.println("Enrolled: " + enrolledStudents + "/" + maxStudents);
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "code='" + courseCode + '\'' +
                ", name='" + courseName + '\'' +
                ", instructor='" + instructor + '\'' +
                ", credits=" + credits +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course other = (Course) obj;
        return courseCode.equals(other.courseCode);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(courseCode);
    }
}
```

#### Step 3: Create Enrollment Class

```java
package com.learning;

/**
 * Represents a student's enrollment in a course.
 */
public class Enrollment {
    private Student student;
    private Course course;
    private double grade;
    private String enrollmentDate;
    
    /**
     * Constructor for Enrollment.
     */
    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.grade = 0.0;
        this.enrollmentDate = java.time.LocalDate.now().toString();
        course.addStudent();
    }
    
    // Getters
    public Student getStudent() {
        return student;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public double getGrade() {
        return grade;
    }
    
    public String getEnrollmentDate() {
        return enrollmentDate;
    }
    
    // Setters
    public void setGrade(double grade) {
        if (grade < 0 || grade > 4.0) {
            throw new IllegalArgumentException("Grade must be between 0 and 4.0");
        }
        this.grade = grade;
    }
    
    /**
     * Displays enrollment information.
     */
    public void displayInfo() {
        System.out.println("Student: " + student.getName() + 
                         " | Course: " + course.getCourseName() + 
                         " | Grade: " + String.format("%.2f", grade));
    }
}
```

#### Step 4: Create StudentManager Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages students and courses.
 */
public class StudentManager {
    private List<Student> students;
    private List<Course> courses;
    
    public StudentManager() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
    }
    
    /**
     * Adds a student.
     */
    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        students.add(student);
    }
    
    /**
     * Adds a course.
     */
    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        courses.add(course);
    }
    
    /**
     * Finds student by ID.
     */
    public Student findStudent(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Finds course by code.
     */
    public Course findCourse(String code) {
        for (Course c : courses) {
            if (c.getCourseCode().equals(code)) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Displays all students.
     */
    public void displayAllStudents() {
        System.out.println("\n=== All Students ===");
        for (Student s : students) {
            s.displayInfo();
        }
    }
    
    /**
     * Displays all courses.
     */
    public void displayAllCourses() {
        System.out.println("\n=== All Courses ===");
        for (Course c : courses) {
            c.displayInfo();
        }
    }
    
    /**
     * Calculates average GPA.
     */
    public double calculateAverageGPA() {
        if (students.isEmpty()) {
            return 0.0;
        }
        
        double totalGPA = 0;
        for (Student s : students) {
            totalGPA += s.calculateGPA();
        }
        
        return totalGPA / students.size();
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Student Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Create manager
        StudentManager manager = new StudentManager();
        
        // Create students
        Student s1 = new Student("John Doe", "john@example.com", 20);
        Student s2 = new Student("Jane Smith", "jane@example.com", 21);
        Student s3 = new Student("Bob Johnson", "bob@example.com", 19);
        
        // Add students
        manager.addStudent(s1);
        manager.addStudent(s2);
        manager.addStudent(s3);
        
        // Create courses
        Course c1 = new Course("CS101", "Introduction to Java", "Dr. Smith", 3, 30);
        Course c2 = new Course("CS102", "Data Structures", "Dr. Johnson", 3, 25);
        Course c3 = new Course("CS103", "Algorithms", "Dr. Williams", 4, 20);
        
        // Add courses
        manager.addCourse(c1);
        manager.addCourse(c2);
        manager.addCourse(c3);
        
        // Enroll students in courses
        s1.enrollInCourse(c1);
        s1.enrollInCourse(c2);
        
        s2.enrollInCourse(c1);
        s2.enrollInCourse(c3);
        
        s3.enrollInCourse(c2);
        s3.enrollInCourse(c3);
        
        // Set grades
        s1.getEnrollments().get(0).setGrade(3.8);
        s1.getEnrollments().get(1).setGrade(3.5);
        
        s2.getEnrollments().get(0).setGrade(3.9);
        s2.getEnrollments().get(1).setGrade(3.7);
        
        s3.getEnrollments().get(0).setGrade(3.2);
        s3.getEnrollments().get(1).setGrade(3.4);
        
        // Display information
        manager.displayAllStudents();
        manager.displayAllCourses();
        
        System.out.println("\n=== Class Statistics ===");
        System.out.println("Average GPA: " + String.format("%.2f", manager.calculateAverageGPA()));
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Student class.
 */
public class StudentTest {
    
    private Student student;
    
    @BeforeEach
    void setUp() {
        student = new Student("John Doe", "john@example.com", 20);
    }
    
    @Test
    void testStudentCreation() {
        assertEquals("John Doe", student.getName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals(20, student.getAge());
    }
    
    @Test
    void testInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.setName("");
        });
    }
    
    @Test
    void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.setEmail("invalid-email");
        });
    }
    
    @Test
    void testInvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> {
            student.setAge(150);
        });
    }
    
    @Test
    void testGPACalculation() {
        Course course = new Course("CS101", "Java", "Dr. Smith", 3, 30);
        student.enrollInCourse(course);
        student.getEnrollments().get(0).setGrade(3.5);
        
        assertEquals(3.5, student.calculateGPA());
    }
}

/**
 * Unit tests for Course class.
 */
public class CourseTest {
    
    private Course course;
    
    @BeforeEach
    void setUp() {
        course = new Course("CS101", "Java", "Dr. Smith", 3, 30);
    }
    
    @Test
    void testCourseCreation() {
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Java", course.getCourseName());
        assertEquals("Dr. Smith", course.getInstructor());
    }
    
    @Test
    void testAvailableSeats() {
        assertTrue(course.hasAvailableSeats());
        course.addStudent();
        assertTrue(course.hasAvailableSeats());
    }
    
    @Test
    void testCourseFullException() {
        Course smallCourse = new Course("CS102", "Small", "Dr. X", 3, 1);
        smallCourse.addStudent();
        
        assertThrows(IllegalStateException.class, () -> {
            smallCourse.addStudent();
        });
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

### Expected Output

```
=== Student Information ===
ID: 1000
Name: John Doe
Email: john@example.com
Age: 20
GPA: 3.65
Grade Level: Excellent
Enrolled Courses: 2

=== Student Information ===
ID: 1001
Name: Jane Smith
Email: jane@example.com
Age: 21
GPA: 3.80
Grade Level: Excellent
Enrolled Courses: 2

=== Student Information ===
ID: 1002
Name: Bob Johnson
Email: bob@example.com
Age: 19
GPA: 3.30
Grade Level: Good
Enrolled Courses: 2

=== Course Information ===
Code: CS101
Name: Introduction to Java
Instructor: Dr. Smith
Credits: 3
Enrolled: 2/30

=== Course Information ===
Code: CS102
Name: Data Structures
Instructor: Dr. Johnson
Credits: 3
Enrolled: 2/25

=== Course Information ===
Code: CS103
Name: Algorithms
Instructor: Dr. Williams
Credits: 4
Enrolled: 2/20

=== Class Statistics ===
Average GPA: 3.58
```

---

## 📝 Exercises

### Exercise 1: Bank Account System

**Objective**: Practice encapsulation and validation

**Task Description**:
Create a BankAccount class with deposit, withdrawal, and balance inquiry methods.

**Acceptance Criteria**:
- [ ] Private balance variable
- [ ] Public getter for balance
- [ ] Deposit method with validation
- [ ] Withdrawal method with validation
- [ ] Transaction history tracking

**Starter Code**:
```java
public class BankAccount {
    // TODO: Implement encapsulation
    // TODO: Implement deposit/withdrawal
    // TODO: Track transactions
}
```

**Reflection Prompt**:
- Why is encapsulation important for a bank account?
- What validations are necessary?
- How would you prevent fraud?

### Exercise 2: Library Book System

**Objective**: Practice composition and object relationships

**Task Description**:
Create Book and Library classes where Library manages a collection of Books.

**Acceptance Criteria**:
- [ ] Book class with properties
- [ ] Library class managing books
- [ ] Add/remove books
- [ ] Search functionality
- [ ] Display library inventory

**Starter Code**:
```java
public class Book {
    // TODO: Implement book properties
}

public class Library {
    // TODO: Implement library management
}
```

**Reflection Prompt**:
- How does composition differ from inheritance?
- What are the benefits of composition?
- How would you extend this system?

### Exercise 3: Employee Payroll System

**Objective**: Practice static members and calculations

**Task Description**:
Create an Employee class that tracks total employees and calculates payroll.

**Acceptance Criteria**:
- [ ] Static employee counter
- [ ] Unique employee IDs
- [ ] Salary calculation
- [ ] Tax deduction
- [ ] Net pay calculation

**Starter Code**:
```java
public class Employee {
    // TODO: Implement static counter
    // TODO: Implement payroll calculations
}
```

**Reflection Prompt**:
- Why use static variables for counters?
- How would you handle different employee types?
- What other calculations might be needed?

---

## 🧪 Quiz

### Question 1: What is the purpose of a constructor?

A) To declare variables  
B) To initialize objects when created  
C) To define methods  
D) To set access modifiers  

**Answer**: B) To initialize objects when created

**Explanation**: Constructors are special methods that initialize objects when they're instantiated using the `new` keyword.

### Question 2: What is encapsulation?

A) Combining data and methods in a class  
B) Hiding internal details and providing controlled access  
C) Creating objects from classes  
D) Inheriting from parent classes  

**Answer**: B) Hiding internal details and providing controlled access

**Explanation**: Encapsulation is the practice of hiding internal implementation details and providing public methods to access and modify data safely.

### Question 3: What is the difference between instance and static variables?

A) No difference  
B) Instance variables are shared; static are unique  
C) Instance variables are unique; static are shared  
D) Static variables are private  

**Answer**: C) Instance variables are unique; static are shared

**Explanation**: Each object has its own instance variables, but all objects share the same static variables.

### Question 4: What does the this keyword refer to?

A) The class  
B) The current object  
C) The parent class  
D) The method  

**Answer**: B) The current object

**Explanation**: The `this` keyword refers to the current object instance and is used to access instance variables and methods.

### Question 5: What is the correct way to compare two objects for equality?

A) Use ==  
B) Use .equals()  
C) Use .compareTo()  
D) Use instanceof  

**Answer**: B) Use .equals()

**Explanation**: The `==` operator compares references, while `.equals()` compares the content of objects. For meaningful comparison, override `.equals()`.

---

## 🚀 Advanced Challenge

### Challenge: Advanced Student Management System

**Difficulty**: Intermediate

**Objective**: Extend the student management system with advanced features

**Description**:
Add features like:
- Student transcripts
- Grade point calculations
- Academic standing (good standing, probation, suspension)
- Course prerequisites
- Student schedules
- Conflict detection

**Requirements**:
- [ ] Transcript generation
- [ ] Academic standing determination
- [ ] Prerequisite checking
- [ ] Schedule conflict detection
- [ ] GPA-based scholarships

**Hints**:
1. Create a Transcript class
2. Implement prerequisite checking
3. Create a Schedule class
4. Implement conflict detection algorithm
5. Add scholarship calculation

**Stretch Goals**:
- [ ] Export transcripts to PDF
- [ ] Email notifications
- [ ] Web interface
- [ ] Database integration

---

## 🏆 Best Practices

### Class Design

1. **Single Responsibility**
   - Each class should have one reason to change
   - Focus on one concept per class
   - Avoid god objects

2. **Meaningful Names**
   ```java
   // ❌ Bad
   public class S { }
   
   // ✅ Good
   public class Student { }
   ```

3. **Proper Encapsulation**
   ```java
   // ❌ Bad: Public variables
   public class Person {
       public String name;
   }
   
   // ✅ Good: Private with getters/setters
   public class Person {
       private String name;
       public String getName() { return name; }
       public void setName(String name) { this.name = name; }
   }
   ```

### Constructor Design

1. **Initialization**
   - Initialize all fields
   - Validate input
   - Establish invariants

2. **Overloading**
   ```java
   // Multiple constructors for flexibility
   public Student(String name) { }
   public Student(String name, int age) { }
   public Student(String name, int age, String email) { }
   ```

### Object Equality

1. **Override equals() and hashCode()**
   - Consistent implementation
   - Required for collections
   - Document equality semantics

2. **Override toString()**
   - Useful for debugging
   - Provides readable representation
   - Helps with logging

---

## 🔗 Next Steps

### Ready for Lab 05?

You've mastered OOP basics! Next, learn about inheritance and polymorphism.

**Next Lab**: [Lab 05: Inheritance](../05-inheritance/README.md)

### Additional Resources

- [Java Classes and Objects](https://docs.oracle.com/javase/tutorial/java/javaOO/classes.html)
- [Encapsulation](https://docs.oracle.com/javase/tutorial/java/javaOO/index.html)
- [Object-Oriented Programming Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)

---

## ✅ Completion Checklist

- [ ] Completed all step-by-step coding tasks
- [ ] Built and tested the student management system
- [ ] Solved all exercises
- [ ] Passed the quiz (80%+)
- [ ] Attempted the advanced challenge
- [ ] Reviewed best practices
- [ ] Added project to portfolio
- [ ] Reflected on learning outcomes

---

**Congratulations on completing Lab 04! 🎉**

You've successfully mastered OOP basics including classes, objects, encapsulation, and composition. You can now design and implement well-structured object-oriented programs. Ready for the next challenge? Move on to [Lab 05: Inheritance](../05-inheritance/README.md).