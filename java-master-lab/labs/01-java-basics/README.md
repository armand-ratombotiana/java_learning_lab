# Lab 01: Java Basics

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a personal information management CLI application |
| **Prerequisites** | Lab 00: Environment Setup |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand Java syntax and structure** including packages, classes, and methods
2. **Work with primitive data types** (int, double, boolean, char, String)
3. **Use variables and constants** effectively in Java programs
4. **Perform input/output operations** using Scanner and System.out
5. **Build a complete CLI application** that manages personal information

## 📚 Prerequisites

- Lab 00: Environment Setup completed
- Java 17+ installed and verified
- Maven or IDE configured
- Basic understanding of programming concepts

## 🧠 Concept Theory

### Java Program Structure

Every Java program follows a specific structure:

```java
// 1. Package declaration (optional)
package com.learning;

// 2. Import statements
import java.util.Scanner;

// 3. Class declaration
public class HelloWorld {
    
    // 4. Main method (entry point)
    public static void main(String[] args) {
        // Program code here
        System.out.println("Hello, World!");
    }
}
```

**Key Components**:
- **Package**: Organizes classes into namespaces
- **Import**: Brings external classes into scope
- **Class**: Container for code and data
- **Main Method**: Entry point of the program

### Primitive Data Types

Java has 8 primitive data types:

| Type | Size | Range | Example |
|------|------|-------|---------|
| byte | 1 byte | -128 to 127 | `byte age = 25;` |
| short | 2 bytes | -32,768 to 32,767 | `short year = 2024;` |
| int | 4 bytes | -2^31 to 2^31-1 | `int count = 1000;` |
| long | 8 bytes | -2^63 to 2^63-1 | `long bigNum = 1000000L;` |
| float | 4 bytes | ~7 decimal digits | `float price = 19.99f;` |
| double | 8 bytes | ~15 decimal digits | `double salary = 50000.50;` |
| boolean | 1 bit | true or false | `boolean active = true;` |
| char | 2 bytes | Unicode character | `char grade = 'A';` |

### Variables and Constants

**Variables**: Store data that can change

```java
int age = 25;           // Mutable variable
age = 26;               // Can be reassigned
```

**Constants**: Store data that cannot change

```java
final double PI = 3.14159;  // Immutable constant
// PI = 3.14;  // ERROR: Cannot reassign
```

**Naming Conventions**:
- Variables: camelCase (`firstName`, `studentAge`)
- Constants: UPPER_SNAKE_CASE (`MAX_STUDENTS`, `PI`)
- Classes: PascalCase (`StudentInfo`, `PersonalData`)

### Input and Output

**Output**:
```java
System.out.println("Hello");      // Prints with newline
System.out.print("Hello");        // Prints without newline
System.out.printf("%d\n", 42);    // Formatted output
```

**Input**:
```java
Scanner scanner = new Scanner(System.in);
String name = scanner.nextLine();     // Read entire line
int age = scanner.nextInt();          // Read integer
double salary = scanner.nextDouble(); // Read decimal
```

### String Operations

```java
String name = "John";
String greeting = "Hello, " + name;           // Concatenation
String formatted = String.format("Age: %d", 25); // Formatting
int length = name.length();                   // Length
String upper = name.toUpperCase();            // Convert to uppercase
boolean contains = name.contains("oh");       // Check substring
```

## 💻 Step-by-Step Coding Tasks

### Task 1: Create a Basic Java Class

**Objective**: Create your first Java class with variables and output

**Acceptance Criteria**:
- [ ] Class created with proper package structure
- [ ] Variables declared for personal information
- [ ] Output displays information correctly
- [ ] Code compiles without errors

**Instructions**:
1. Create a new Maven project (or use IDE)
2. Create a class `PersonalInfo` in package `com.learning`
3. Declare variables for name, age, email, and city
4. Initialize variables with sample data
5. Print the information using System.out.println()

**Code Template**:
```java
package com.learning;

public class PersonalInfo {
    public static void main(String[] args) {
        // TODO: Declare variables
        // TODO: Initialize with data
        // TODO: Print information
    }
}
```

**Expected Output**:
```
Name: John Doe
Age: 25
Email: john@example.com
City: New York
```

### Task 2: Accept User Input

**Objective**: Read user input and store in variables

**Acceptance Criteria**:
- [ ] Scanner created and configured
- [ ] User input accepted for multiple fields
- [ ] Input stored in appropriate variables
- [ ] Program handles input correctly

**Instructions**:
1. Import Scanner class
2. Create Scanner object for System.in
3. Prompt user for name, age, email, city
4. Read input using appropriate methods
5. Store in variables
6. Display the collected information

**Code Template**:
```java
import java.util.Scanner;

public class PersonalInfo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // TODO: Prompt and read user input
        // TODO: Store in variables
        // TODO: Display information
        
        scanner.close();
    }
}
```

**Expected Output**:
```
Enter your name: John Doe
Enter your age: 25
Enter your email: john@example.com
Enter your city: New York

Your Information:
Name: John Doe
Age: 25
Email: john@example.com
City: New York
```

### Task 3: Perform Calculations

**Objective**: Use variables in calculations

**Acceptance Criteria**:
- [ ] Age calculation works correctly
- [ ] Birth year calculated from current year
- [ ] Results displayed with proper formatting
- [ ] Code handles edge cases

**Instructions**:
1. Calculate birth year from age
2. Calculate years until retirement (assuming 65)
3. Create an email display with formatting
4. Display all calculated values

**Code Template**:
```java
public class PersonalInfo {
    public static void main(String[] args) {
        // ... previous code ...
        
        // TODO: Calculate birth year
        // TODO: Calculate years to retirement
        // TODO: Display calculations
    }
}
```

**Expected Output**:
```
Birth Year: 1999
Years to Retirement: 40
Email Domain: example.com
```

## 🎨 Mini-Project: Personal Information Manager

### Project Overview

**Description**: Create a CLI application that manages personal information. Users can input their details, and the application displays formatted information with calculations.

**Real-World Application**: Foundation for contact management systems, user registration forms, and data collection applications.

**Learning Value**: Understand Java basics including variables, input/output, and string operations.

### Project Requirements

#### Functional Requirements
- [ ] Accept user input for name, age, email, city, phone
- [ ] Validate input (age should be positive, email should contain @)
- [ ] Calculate and display birth year
- [ ] Calculate years until retirement
- [ ] Display formatted personal information
- [ ] Allow user to update information
- [ ] Save information to a file (bonus)

#### Non-Functional Requirements
- [ ] Clean, readable code with comments
- [ ] Proper variable naming conventions
- [ ] Efficient input handling
- [ ] User-friendly prompts and output

### Project Structure

```
personal-info-manager/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── PersonalInfo.java
│   │           ├── InfoManager.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── PersonalInfoTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create PersonalInfo Class

```java
package com.learning;

/**
 * Represents personal information of a person.
 */
public class PersonalInfo {
    private String name;
    private int age;
    private String email;
    private String city;
    private String phone;
    
    // Constructor
    public PersonalInfo(String name, int age, String email, String city, String phone) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.city = city;
        this.phone = phone;
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setEmail(String email) { this.email = email; }
    public void setCity(String city) { this.city = city; }
    public void setPhone(String phone) { this.phone = phone; }
    
    /**
     * Calculates birth year based on current year and age.
     */
    public int calculateBirthYear() {
        return 2024 - age;
    }
    
    /**
     * Calculates years until retirement (assuming retirement at 65).
     */
    public int yearsToRetirement() {
        return 65 - age;
    }
    
    /**
     * Displays personal information in formatted output.
     */
    public void displayInfo() {
        System.out.println("\n=== Personal Information ===");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Email: " + email);
        System.out.println("City: " + city);
        System.out.println("Phone: " + phone);
        System.out.println("Birth Year: " + calculateBirthYear());
        System.out.println("Years to Retirement: " + yearsToRetirement());
        System.out.println("============================\n");
    }
}
```

#### Step 2: Create InfoManager Class

```java
package com.learning;

import java.util.Scanner;

/**
 * Manages personal information input and operations.
 */
public class InfoManager {
    private Scanner scanner;
    private PersonalInfo info;
    
    public InfoManager() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Collects personal information from user input.
     */
    public void collectInfo() {
        System.out.println("=== Personal Information Manager ===\n");
        
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter your city: ");
        String city = scanner.nextLine();
        
        System.out.print("Enter your phone: ");
        String phone = scanner.nextLine();
        
        this.info = new PersonalInfo(name, age, email, city, phone);
    }
    
    /**
     * Displays the collected information.
     */
    public void displayInfo() {
        if (info != null) {
            info.displayInfo();
        } else {
            System.out.println("No information collected yet.");
        }
    }
    
    /**
     * Updates personal information.
     */
    public void updateInfo() {
        if (info == null) {
            System.out.println("Please collect information first.");
            return;
        }
        
        System.out.println("\n=== Update Information ===");
        System.out.println("1. Update Name");
        System.out.println("2. Update Age");
        System.out.println("3. Update Email");
        System.out.println("4. Update City");
        System.out.println("5. Update Phone");
        System.out.print("Choose option (1-5): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new name: ");
                info.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("Enter new age: ");
                info.setAge(scanner.nextInt());
                break;
            case 3:
                System.out.print("Enter new email: ");
                info.setEmail(scanner.nextLine());
                break;
            case 4:
                System.out.print("Enter new city: ");
                info.setCity(scanner.nextLine());
                break;
            case 5:
                System.out.print("Enter new phone: ");
                info.setPhone(scanner.nextLine());
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Closes the scanner resource.
     */
    public void close() {
        scanner.close();
    }
}
```

#### Step 3: Create Main Class

```java
package com.learning;

/**
 * Main entry point for the Personal Information Manager application.
 */
public class Main {
    public static void main(String[] args) {
        InfoManager manager = new InfoManager();
        
        manager.collectInfo();
        manager.displayInfo();
        manager.updateInfo();
        manager.displayInfo();
        
        manager.close();
    }
}
```

### Testing the Project

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
=== Personal Information Manager ===

Enter your name: John Doe
Enter your age: 25
Enter your email: john@example.com
Enter your city: New York
Enter your phone: 555-1234

=== Personal Information ===
Name: John Doe
Age: 25
Email: john@example.com
City: New York
Phone: 555-1234
Birth Year: 1999
Years to Retirement: 40

=== Update Information ===
1. Update Name
2. Update Age
3. Update Email
4. Update City
5. Update Phone
Choose option (1-5): 2
Enter new age: 26

=== Personal Information ===
Name: John Doe
Age: 26
Email: john@example.com
City: New York
Phone: 555-1234
Birth Year: 1998
Years to Retirement: 39
```

## 📝 Exercises

### Exercise 1: Temperature Converter

**Objective**: Practice variable operations and calculations

**Task Description**:
Create a program that converts temperature between Celsius and Fahrenheit.

**Acceptance Criteria**:
- [ ] Accepts temperature input from user
- [ ] Converts Celsius to Fahrenheit correctly
- [ ] Converts Fahrenheit to Celsius correctly
- [ ] Displays results with proper formatting

**Starter Code**:
```java
public class TemperatureConverter {
    public static void main(String[] args) {
        // TODO: Accept temperature input
        // TODO: Perform conversion
        // TODO: Display result
    }
}
```

**Reflection Prompt**:
- What formula did you use for conversion?
- How would you handle invalid input?
- What edge cases should you consider?

### Exercise 2: BMI Calculator

**Objective**: Practice calculations with multiple variables

**Task Description**:
Create a program that calculates Body Mass Index (BMI) from height and weight.

**Acceptance Criteria**:
- [ ] Accepts height and weight input
- [ ] Calculates BMI correctly (weight / (height * height))
- [ ] Categorizes BMI (underweight, normal, overweight, obese)
- [ ] Displays results with interpretation

**Starter Code**:
```java
public class BMICalculator {
    public static void main(String[] args) {
        // TODO: Accept height and weight
        // TODO: Calculate BMI
        // TODO: Categorize and display
    }
}
```

**Reflection Prompt**:
- What are the BMI categories?
- How would you improve the user interface?
- What additional health metrics could you calculate?

### Exercise 3: String Manipulation

**Objective**: Practice string operations

**Task Description**:
Create a program that manipulates strings (reverse, count characters, find substrings).

**Acceptance Criteria**:
- [ ] Accepts string input from user
- [ ] Reverses the string
- [ ] Counts vowels and consonants
- [ ] Finds and replaces substrings
- [ ] Displays all results

**Starter Code**:
```java
public class StringManipulator {
    public static void main(String[] args) {
        // TODO: Accept string input
        // TODO: Perform string operations
        // TODO: Display results
    }
}
```

**Reflection Prompt**:
- What string methods did you use?
- How would you optimize string operations?
- What other string manipulations could be useful?

## 🧪 Quiz

### Question 1: What is the correct syntax for declaring a variable in Java?

A) `int age;`  
B) `age: int;`  
C) `declare int age;`  
D) `int: age;`  

**Answer**: A) `int age;`

**Explanation**: Java uses the syntax `type variableName;` to declare variables. The type comes first, followed by the variable name.

### Question 2: Which of the following is NOT a primitive data type in Java?

A) int  
B) String  
C) boolean  
D) double  

**Answer**: B) String

**Explanation**: String is a reference type (class), not a primitive type. The 8 primitive types are: byte, short, int, long, float, double, boolean, and char.

### Question 3: What is the purpose of the main method?

A) To define the class structure  
B) To serve as the entry point of the program  
C) To declare variables  
D) To import packages  

**Answer**: B) To serve as the entry point of the program

**Explanation**: The main method is where program execution begins. It has the signature `public static void main(String[] args)`.

### Question 4: How do you read an integer from user input using Scanner?

A) `scanner.readInt();`  
B) `scanner.nextInt();`  
C) `scanner.getInt();`  
D) `scanner.inputInt();`  

**Answer**: B) `scanner.nextInt();`

**Explanation**: The Scanner class provides methods like `nextInt()`, `nextDouble()`, `nextLine()` to read different types of input.

### Question 5: What is the difference between `print()` and `println()`?

A) They are identical  
B) `print()` adds a newline, `println()` doesn't  
C) `println()` adds a newline, `print()` doesn't  
D) `println()` is faster  

**Answer**: C) `println()` adds a newline, `print()` doesn't

**Explanation**: `System.out.println()` prints the text and moves to the next line, while `System.out.print()` prints without adding a newline.

## 🚀 Advanced Challenge

### Challenge: Enhanced Personal Information Manager

**Difficulty**: Intermediate

**Objective**: Extend the mini-project with advanced features

**Description**:
Enhance the Personal Information Manager with:
- Input validation (age > 0, valid email format)
- Multiple person management (store multiple people)
- Search functionality
- Data persistence (save/load from file)

**Requirements**:
- [ ] Validate all user input
- [ ] Store multiple PersonalInfo objects
- [ ] Search by name or email
- [ ] Save data to a text file
- [ ] Load data from file on startup

**Hints**:
1. Use ArrayList to store multiple PersonalInfo objects
2. Create validation methods for each field
3. Use File I/O for persistence
4. Implement a menu system for operations

**Stretch Goals**:
- [ ] Add sorting by name or age
- [ ] Export data to CSV format
- [ ] Create a GUI interface
- [ ] Add password protection

## 🏆 Best Practices

### Code Organization

1. **Package Structure**
   - Use meaningful package names
   - Follow reverse domain naming (com.learning)
   - Organize classes logically

2. **Naming Conventions**
   - Variables: camelCase
   - Constants: UPPER_SNAKE_CASE
   - Classes: PascalCase
   - Methods: camelCase (usually verbs)

3. **Code Comments**
   - Write clear, concise comments
   - Explain the "why", not the "what"
   - Use JavaDoc for public methods

### Input Handling

1. **Validation**
   - Check for null values
   - Validate data types
   - Handle edge cases

2. **Error Handling**
   - Use try-catch for exceptions
   - Provide meaningful error messages
   - Allow user to retry

3. **Resource Management**
   - Close Scanner when done
   - Use try-with-resources (Java 7+)
   - Clean up resources properly

### Performance

1. **String Operations**
   - Use StringBuilder for concatenation in loops
   - Avoid creating unnecessary String objects
   - Use String methods efficiently

2. **Variable Scope**
   - Declare variables in smallest scope needed
   - Avoid global variables
   - Use local variables when possible

## 🔗 Next Steps

### Ready for Lab 02?

You've mastered Java basics! Next, learn about operators and control flow.

**Next Lab**: [Lab 02: Operators & Control Flow](../02-operators-control-flow/README.md)

### Additional Resources

- [Java Language Basics](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/)
- [Java Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-136091.html)
- [Scanner Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html)

## ✅ Completion Checklist

- [ ] Completed all step-by-step coding tasks
- [ ] Built and tested the mini-project
- [ ] Solved all exercises
- [ ] Passed the quiz (80%+)
- [ ] Attempted the advanced challenge
- [ ] Reviewed best practices
- [ ] Added project to portfolio
- [ ] Reflected on learning outcomes

---

**Congratulations on completing Lab 01! 🎉**

You've successfully mastered Java basics including variables, data types, input/output, and string operations. Ready for the next challenge? Move on to [Lab 02: Operators & Control Flow](../02-operators-control-flow/README.md).