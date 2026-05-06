# Lab 03: Methods & Scope

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a scientific calculator with modular functions |
| **Prerequisites** | Lab 02: Operators & Control Flow |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Define and call methods** with parameters and return types
2. **Understand method scope** and variable lifetime
3. **Master parameter passing** (pass-by-value, pass-by-reference)
4. **Use method overloading** effectively
5. **Build a scientific calculator** with modular functions

## 📚 Prerequisites

- Lab 02: Operators & Control Flow completed
- Understanding of operators and control flow
- Basic Java syntax knowledge

## 🧠 Concept Theory

### 1. Method Basics

A method is a reusable block of code that performs a specific task:

```java
// Method syntax
[access modifier] [return type] [method name] ([parameters]) {
    // Method body
    return [value];  // if return type is not void
}

// Example
public int add(int a, int b) {
    return a + b;
}

// Calling the method
int result = add(5, 3);  // result = 8
```

**Key Components**:
- **Access Modifier**: `public`, `private`, `protected`, or package-private
- **Return Type**: Data type of the return value (or `void` if no return)
- **Method Name**: Follows camelCase convention
- **Parameters**: Input values (optional)
- **Method Body**: Code to execute
- **Return Statement**: Returns a value (if return type is not void)

### 2. Method Parameters and Return Types

```java
// Method with no parameters, no return
public void greet() {
    System.out.println("Hello!");
}

// Method with parameters, no return
public void printSum(int a, int b) {
    System.out.println("Sum: " + (a + b));
}

// Method with no parameters, with return
public int getRandomNumber() {
    return new Random().nextInt(100);
}

// Method with parameters and return
public double calculateAverage(double[] numbers) {
    double sum = 0;
    for (double num : numbers) {
        sum += num;
    }
    return sum / numbers.length;
}

// Method with multiple parameters and return
public boolean isEligible(int age, double income) {
    return age >= 18 && income >= 30000;
}
```

### 3. Variable Scope

Scope determines where a variable can be accessed:

```java
public class ScopeExample {
    // Class variable (accessible throughout the class)
    public static int classVariable = 10;
    
    // Instance variable (accessible in all methods of the instance)
    private int instanceVariable = 20;
    
    public void methodOne() {
        // Local variable (accessible only in this method)
        int localVariable = 30;
        
        System.out.println(classVariable);      // ✓ Accessible
        System.out.println(instanceVariable);   // ✓ Accessible
        System.out.println(localVariable);      // ✓ Accessible
    }
    
    public void methodTwo() {
        System.out.println(classVariable);      // ✓ Accessible
        System.out.println(instanceVariable);   // ✓ Accessible
        // System.out.println(localVariable);   // ✗ Not accessible (different method)
    }
}

// Scope levels (from broadest to narrowest)
// 1. Class scope (static variables)
// 2. Instance scope (instance variables)
// 3. Method scope (local variables)
// 4. Block scope (variables in if/for/while blocks)
```

**Scope Rules**:
- Variables are accessible from their declaration point to the end of their scope
- Inner scopes can access outer scopes
- Outer scopes cannot access inner scopes
- Variables with the same name in different scopes are different variables

### 4. Variable Lifetime

Variables exist only during their scope:

```java
public void demonstrateLifetime() {
    int x = 10;  // x is created
    
    if (x > 5) {
        int y = 20;  // y is created
        System.out.println(x + y);  // Both accessible
    }
    // y is destroyed here
    
    System.out.println(x);  // ✓ x still exists
    // System.out.println(y);  // ✗ y no longer exists
}
```

### 5. Pass-by-Value vs Pass-by-Reference

Java passes primitives by value and objects by reference:

```java
// Pass-by-value (primitives)
public void modifyPrimitive(int num) {
    num = 100;  // Changes only the local copy
}

int x = 5;
modifyPrimitive(x);
System.out.println(x);  // Still 5

// Pass-by-reference (objects)
public void modifyArray(int[] arr) {
    arr[0] = 100;  // Changes the actual array
}

int[] numbers = {1, 2, 3};
modifyArray(numbers);
System.out.println(numbers[0]);  // Now 100

// Important: Reassigning the reference doesn't affect original
public void reassignArray(int[] arr) {
    arr = new int[]{10, 20, 30};  // Creates new array, doesn't affect original
}

int[] numbers = {1, 2, 3};
reassignArray(numbers);
System.out.println(numbers[0]);  // Still 1
```

### 6. Method Overloading

Methods with the same name but different parameters:

```java
public class Calculator {
    // Overloaded add methods
    
    // Add two integers
    public int add(int a, int b) {
        return a + b;
    }
    
    // Add three integers
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // Add two doubles
    public double add(double a, double b) {
        return a + b;
    }
    
    // Add integer and double
    public double add(int a, double b) {
        return a + b;
    }
}

// Usage
Calculator calc = new Calculator();
System.out.println(calc.add(5, 3));           // Calls add(int, int)
System.out.println(calc.add(5, 3, 2));        // Calls add(int, int, int)
System.out.println(calc.add(5.5, 3.2));       // Calls add(double, double)
System.out.println(calc.add(5, 3.2));         // Calls add(int, double)
```

**Overloading Rules**:
- Methods must have the same name
- Methods must have different parameter lists
- Return type alone is not enough for overloading
- Parameter order matters

### 7. Varargs (Variable Arguments)

Methods that accept variable number of arguments:

```java
// Varargs syntax: type... paramName
public int sum(int... numbers) {
    int total = 0;
    for (int num : numbers) {
        total += num;
    }
    return total;
}

// Usage
System.out.println(sum(1, 2, 3));           // 6
System.out.println(sum(1, 2, 3, 4, 5));     // 15
System.out.println(sum());                  // 0

// Varargs with other parameters
public void printNumbers(String prefix, int... numbers) {
    System.out.print(prefix + ": ");
    for (int num : numbers) {
        System.out.print(num + " ");
    }
    System.out.println();
}

printNumbers("Numbers", 1, 2, 3);  // Numbers: 1 2 3
```

### 8. Method Documentation (JavaDoc)

Document methods with JavaDoc comments:

```java
/**
 * Calculates the factorial of a number.
 *
 * <p>The factorial of n is the product of all positive integers less than or equal to n.
 * For example, factorial(5) = 5 * 4 * 3 * 2 * 1 = 120.</p>
 *
 * @param n the number to calculate factorial for (must be non-negative)
 * @return the factorial of n
 * @throws IllegalArgumentException if n is negative
 *
 * @example
 * int result = factorial(5);  // Returns 120
 */
public int factorial(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("n must be non-negative");
    }
    if (n == 0 || n == 1) {
        return 1;
    }
    return n * factorial(n - 1);
}
```

### 9. Recursion

Methods that call themselves:

```java
// Factorial using recursion
public int factorial(int n) {
    if (n <= 1) {
        return 1;  // Base case
    }
    return n * factorial(n - 1);  // Recursive case
}

// Fibonacci using recursion
public int fibonacci(int n) {
    if (n <= 1) {
        return n;  // Base case
    }
    return fibonacci(n - 1) + fibonacci(n - 2);  // Recursive case
}

// Binary search using recursion
public int binarySearch(int[] arr, int target, int left, int right) {
    if (left > right) {
        return -1;  // Base case: not found
    }
    
    int mid = left + (right - left) / 2;
    
    if (arr[mid] == target) {
        return mid;  // Base case: found
    } else if (arr[mid] < target) {
        return binarySearch(arr, target, mid + 1, right);  // Recursive case
    } else {
        return binarySearch(arr, target, left, mid - 1);   // Recursive case
    }
}
```

**Recursion Best Practices**:
- Always have a base case
- Make progress toward base case
- Avoid infinite recursion
- Consider iterative alternatives for performance

### 10. Static Methods

Methods that belong to the class, not instances:

```java
public class MathUtils {
    // Static method (belongs to class)
    public static int add(int a, int b) {
        return a + b;
    }
    
    // Instance method (belongs to instance)
    public int multiply(int a, int b) {
        return a * b;
    }
}

// Usage
int sum = MathUtils.add(5, 3);  // Call static method on class
MathUtils utils = new MathUtils();
int product = utils.multiply(5, 3);  // Call instance method on object
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create Basic Methods

**Objective**: Define and call methods with different signatures

**Acceptance Criteria**:
- [ ] Methods with no parameters work
- [ ] Methods with parameters work
- [ ] Methods with return values work
- [ ] Method calls are correct

**Instructions**:
1. Create a class `MethodExamples`
2. Create method with no parameters, no return
3. Create method with parameters, no return
4. Create method with no parameters, with return
5. Create method with parameters and return
6. Call all methods from main

**Code Template**:
```java
package com.learning;

public class MethodExamples {
    // TODO: Create methods with different signatures
    
    public static void main(String[] args) {
        // TODO: Call all methods
    }
}
```

### Task 2: Understand Variable Scope

**Objective**: Demonstrate variable scope and lifetime

**Acceptance Criteria**:
- [ ] Class variables accessible everywhere
- [ ] Instance variables accessible in methods
- [ ] Local variables accessible only in scope
- [ ] Block variables have limited scope

**Instructions**:
1. Create a class with class variable
2. Create instance variable
3. Create local variables in methods
4. Demonstrate scope limitations
5. Show variable lifetime

**Code Template**:
```java
package com.learning;

public class ScopeDemo {
    // TODO: Declare class and instance variables
    
    public void demonstrateScope() {
        // TODO: Declare local variables
        // TODO: Show what's accessible
    }
}
```

### Task 3: Method Overloading

**Objective**: Create overloaded methods

**Acceptance Criteria**:
- [ ] Multiple methods with same name
- [ ] Different parameter lists
- [ ] Correct method called based on arguments
- [ ] All overloads work correctly

**Instructions**:
1. Create a class `Calculator`
2. Create overloaded add methods
3. Create overloaded multiply methods
4. Test all overloads
5. Demonstrate method selection

**Code Template**:
```java
package com.learning;

public class Calculator {
    // TODO: Create overloaded methods
    
    public static void main(String[] args) {
        // TODO: Test all overloads
    }
}
```

---

## 🎨 Mini-Project: Scientific Calculator

### Project Overview

**Description**: Create a scientific calculator with modular functions for basic and advanced operations.

**Real-World Application**: Foundation for calculator applications, mathematical libraries, and function composition.

**Learning Value**: Master methods, scope, overloading, and recursion.

### Project Requirements

#### Functional Requirements
- [ ] Basic operations (add, subtract, multiply, divide)
- [ ] Advanced operations (power, square root, factorial)
- [ ] Trigonometric functions (sin, cos, tan)
- [ ] Logarithmic functions (log, ln)
- [ ] Memory functions (store, recall, clear)
- [ ] History tracking
- [ ] Error handling

#### Non-Functional Requirements
- [ ] Clean, modular code
- [ ] Proper error handling
- [ ] User-friendly interface
- [ ] Comprehensive documentation

### Project Structure

```
scientific-calculator/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Calculator.java
│   │           ├── MathOperations.java
│   │           ├── CalculatorUI.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── CalculatorTest.java
│               └── MathOperationsTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create MathOperations Class

```java
package com.learning;

/**
 * Provides mathematical operations for the calculator.
 */
public class MathOperations {
    
    /**
     * Adds two numbers.
     */
    public static double add(double a, double b) {
        return a + b;
    }
    
    /**
     * Subtracts two numbers.
     */
    public static double subtract(double a, double b) {
        return a - b;
    }
    
    /**
     * Multiplies two numbers.
     */
    public static double multiply(double a, double b) {
        return a * b;
    }
    
    /**
     * Divides two numbers.
     *
     * @throws ArithmeticException if divisor is zero
     */
    public static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }
    
    /**
     * Calculates power (a^b).
     */
    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }
    
    /**
     * Calculates square root.
     *
     * @throws IllegalArgumentException if number is negative
     */
    public static double squareRoot(double num) {
        if (num < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        return Math.sqrt(num);
    }
    
    /**
     * Calculates factorial using recursion.
     *
     * @throws IllegalArgumentException if number is negative
     */
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial of negative number is undefined");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    
    /**
     * Calculates sine (in radians).
     */
    public static double sin(double angle) {
        return Math.sin(angle);
    }
    
    /**
     * Calculates cosine (in radians).
     */
    public static double cos(double angle) {
        return Math.cos(angle);
    }
    
    /**
     * Calculates tangent (in radians).
     */
    public static double tan(double angle) {
        return Math.tan(angle);
    }
    
    /**
     * Calculates natural logarithm.
     *
     * @throws IllegalArgumentException if number is not positive
     */
    public static double ln(double num) {
        if (num <= 0) {
            throw new IllegalArgumentException("Logarithm of non-positive number is undefined");
        }
        return Math.log(num);
    }
    
    /**
     * Calculates base-10 logarithm.
     *
     * @throws IllegalArgumentException if number is not positive
     */
    public static double log10(double num) {
        if (num <= 0) {
            throw new IllegalArgumentException("Logarithm of non-positive number is undefined");
        }
        return Math.log10(num);
    }
    
    /**
     * Calculates absolute value.
     */
    public static double abs(double num) {
        return Math.abs(num);
    }
    
    /**
     * Rounds to nearest integer.
     */
    public static long round(double num) {
        return Math.round(num);
    }
    
    /**
     * Converts degrees to radians.
     */
    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }
    
    /**
     * Converts radians to degrees.
     */
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
}
```

#### Step 2: Create Calculator Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Main calculator class with memory and history.
 */
public class Calculator {
    private double memory;
    private double lastResult;
    private List<String> history;
    
    public Calculator() {
        this.memory = 0;
        this.lastResult = 0;
        this.history = new ArrayList<>();
    }
    
    /**
     * Performs a calculation and stores in history.
     */
    public double calculate(String operation, double... operands) {
        double result;
        
        try {
            result = switch (operation.toLowerCase()) {
                case "add" -> MathOperations.add(operands[0], operands[1]);
                case "subtract" -> MathOperations.subtract(operands[0], operands[1]);
                case "multiply" -> MathOperations.multiply(operands[0], operands[1]);
                case "divide" -> MathOperations.divide(operands[0], operands[1]);
                case "power" -> MathOperations.power(operands[0], operands[1]);
                case "sqrt" -> MathOperations.squareRoot(operands[0]);
                case "factorial" -> MathOperations.factorial((int) operands[0]);
                case "sin" -> MathOperations.sin(operands[0]);
                case "cos" -> MathOperations.cos(operands[0]);
                case "tan" -> MathOperations.tan(operands[0]);
                case "ln" -> MathOperations.ln(operands[0]);
                case "log10" -> MathOperations.log10(operands[0]);
                case "abs" -> MathOperations.abs(operands[0]);
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            
            lastResult = result;
            addToHistory(operation, operands, result);
            return result;
            
        } catch (Exception e) {
            addToHistory(operation, operands, Double.NaN);
            throw e;
        }
    }
    
    /**
     * Stores a value in memory.
     */
    public void storeMemory(double value) {
        memory = value;
    }
    
    /**
     * Recalls value from memory.
     */
    public double recallMemory() {
        return memory;
    }
    
    /**
     * Adds to memory.
     */
    public void addToMemory(double value) {
        memory += value;
    }
    
    /**
     * Clears memory.
     */
    public void clearMemory() {
        memory = 0;
    }
    
    /**
     * Gets last result.
     */
    public double getLastResult() {
        return lastResult;
    }
    
    /**
     * Adds operation to history.
     */
    private void addToHistory(String operation, double[] operands, double result) {
        String entry = String.format("%s(%s) = %f", 
            operation, 
            formatOperands(operands), 
            result);
        history.add(entry);
    }
    
    /**
     * Formats operands for display.
     */
    private String formatOperands(double[] operands) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operands.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(operands[i]);
        }
        return sb.toString();
    }
    
    /**
     * Gets calculation history.
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Clears history.
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * Displays history.
     */
    public void displayHistory() {
        System.out.println("\n=== Calculation History ===");
        if (history.isEmpty()) {
            System.out.println("No history");
        } else {
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + ". " + history.get(i));
            }
        }
    }
}
```

#### Step 3: Create CalculatorUI Class

```java
package com.learning;

import java.util.Scanner;

/**
 * User interface for the calculator.
 */
public class CalculatorUI {
    private Calculator calculator;
    private Scanner scanner;
    
    public CalculatorUI() {
        this.calculator = new Calculator();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Displays the main menu.
     */
    public void displayMenu() {
        System.out.println("\n=== Scientific Calculator ===");
        System.out.println("1. Basic Operations (add, subtract, multiply, divide)");
        System.out.println("2. Advanced Operations (power, sqrt, factorial)");
        System.out.println("3. Trigonometric Functions (sin, cos, tan)");
        System.out.println("4. Logarithmic Functions (ln, log10)");
        System.out.println("5. Memory Operations");
        System.out.println("6. View History");
        System.out.println("7. Clear History");
        System.out.println("8. Exit");
        System.out.print("Choose option: ");
    }
    
    /**
     * Runs the calculator.
     */
    public void run() {
        System.out.println("Welcome to Scientific Calculator!");
        
        boolean running = true;
        while (running) {
            displayMenu();
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                
                switch (choice) {
                    case 1 -> basicOperations();
                    case 2 -> advancedOperations();
                    case 3 -> trigonometricFunctions();
                    case 4 -> logarithmicFunctions();
                    case 5 -> memoryOperations();
                    case 6 -> calculator.displayHistory();
                    case 7 -> {
                        calculator.clearHistory();
                        System.out.println("History cleared");
                    }
                    case 8 -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();  // Clear invalid input
            }
        }
    }
    
    private void basicOperations() {
        System.out.print("Enter first number: ");
        double a = scanner.nextDouble();
        System.out.print("Enter second number: ");
        double b = scanner.nextDouble();
        
        System.out.println("\n1. Add\n2. Subtract\n3. Multiply\n4. Divide");
        System.out.print("Choose operation: ");
        int op = scanner.nextInt();
        
        double result = switch (op) {
            case 1 -> calculator.calculate("add", a, b);
            case 2 -> calculator.calculate("subtract", a, b);
            case 3 -> calculator.calculate("multiply", a, b);
            case 4 -> calculator.calculate("divide", a, b);
            default -> throw new IllegalArgumentException("Invalid operation");
        };
        
        System.out.printf("Result: %.2f\n", result);
    }
    
    private void advancedOperations() {
        System.out.println("\n1. Power\n2. Square Root\n3. Factorial");
        System.out.print("Choose operation: ");
        int op = scanner.nextInt();
        
        double result = switch (op) {
            case 1 -> {
                System.out.print("Enter base: ");
                double base = scanner.nextDouble();
                System.out.print("Enter exponent: ");
                double exp = scanner.nextDouble();
                yield calculator.calculate("power", base, exp);
            }
            case 2 -> {
                System.out.print("Enter number: ");
                double num = scanner.nextDouble();
                yield calculator.calculate("sqrt", num);
            }
            case 3 -> {
                System.out.print("Enter number: ");
                int num = scanner.nextInt();
                yield calculator.calculate("factorial", num);
            }
            default -> throw new IllegalArgumentException("Invalid operation");
        };
        
        System.out.printf("Result: %.2f\n", result);
    }
    
    private void trigonometricFunctions() {
        System.out.println("\n1. Sin\n2. Cos\n3. Tan");
        System.out.print("Choose function: ");
        int op = scanner.nextInt();
        System.out.print("Enter angle in degrees: ");
        double degrees = scanner.nextDouble();
        double radians = MathOperations.toRadians(degrees);
        
        double result = switch (op) {
            case 1 -> calculator.calculate("sin", radians);
            case 2 -> calculator.calculate("cos", radians);
            case 3 -> calculator.calculate("tan", radians);
            default -> throw new IllegalArgumentException("Invalid operation");
        };
        
        System.out.printf("Result: %.4f\n", result);
    }
    
    private void logarithmicFunctions() {
        System.out.println("\n1. Natural Log (ln)\n2. Log Base 10");
        System.out.print("Choose function: ");
        int op = scanner.nextInt();
        System.out.print("Enter number: ");
        double num = scanner.nextDouble();
        
        double result = switch (op) {
            case 1 -> calculator.calculate("ln", num);
            case 2 -> calculator.calculate("log10", num);
            default -> throw new IllegalArgumentException("Invalid operation");
        };
        
        System.out.printf("Result: %.4f\n", result);
    }
    
    private void memoryOperations() {
        System.out.println("\n1. Store\n2. Recall\n3. Add to Memory\n4. Clear Memory");
        System.out.print("Choose operation: ");
        int op = scanner.nextInt();
        
        switch (op) {
            case 1 -> {
                System.out.print("Enter value to store: ");
                double value = scanner.nextDouble();
                calculator.storeMemory(value);
                System.out.println("Stored: " + value);
            }
            case 2 -> System.out.println("Memory: " + calculator.recallMemory());
            case 3 -> {
                System.out.print("Enter value to add: ");
                double value = scanner.nextDouble();
                calculator.addToMemory(value);
                System.out.println("Memory: " + calculator.recallMemory());
            }
            case 4 -> {
                calculator.clearMemory();
                System.out.println("Memory cleared");
            }
            default -> System.out.println("Invalid operation");
        }
    }
    
    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

/**
 * Main entry point for the Scientific Calculator.
 */
public class Main {
    public static void main(String[] args) {
        CalculatorUI ui = new CalculatorUI();
        ui.run();
        ui.close();
    }
}
```

#### Step 5: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MathOperations class.
 */
public class MathOperationsTest {
    
    @Test
    void testAdd() {
        assertEquals(8, MathOperations.add(5, 3));
        assertEquals(0, MathOperations.add(5, -5));
    }
    
    @Test
    void testDivideByZero() {
        assertThrows(ArithmeticException.class, () -> {
            MathOperations.divide(10, 0);
        });
    }
    
    @Test
    void testFactorial() {
        assertEquals(1, MathOperations.factorial(0));
        assertEquals(1, MathOperations.factorial(1));
        assertEquals(120, MathOperations.factorial(5));
    }
    
    @Test
    void testSquareRoot() {
        assertEquals(5, MathOperations.squareRoot(25));
        assertThrows(IllegalArgumentException.class, () -> {
            MathOperations.squareRoot(-1);
        });
    }
}

/**
 * Unit tests for Calculator class.
 */
public class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void testBasicOperation() {
        double result = calculator.calculate("add", 5, 3);
        assertEquals(8, result);
    }
    
    @Test
    void testMemory() {
        calculator.storeMemory(42);
        assertEquals(42, calculator.recallMemory());
    }
    
    @Test
    void testHistory() {
        calculator.calculate("add", 5, 3);
        assertEquals(1, calculator.getHistory().size());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the calculator
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Recursive Fibonacci

**Objective**: Practice recursion

**Task Description**:
Create a method that calculates Fibonacci numbers using recursion.

**Acceptance Criteria**:
- [ ] Correctly calculates Fibonacci numbers
- [ ] Has proper base case
- [ ] Handles edge cases

**Starter Code**:
```java
public class FibonacciCalculator {
    public static int fibonacci(int n) {
        // TODO: Implement recursive Fibonacci
    }
    
    public static void main(String[] args) {
        // TODO: Test Fibonacci
    }
}
```

**Reflection Prompt**:
- What's the time complexity?
- How would you optimize it?
- What's the difference between recursion and iteration?

### Exercise 2: Method Overloading

**Objective**: Practice method overloading

**Task Description**:
Create overloaded methods for calculating area of different shapes.

**Acceptance Criteria**:
- [ ] Circle area method
- [ ] Rectangle area method
- [ ] Triangle area method
- [ ] All overloads work correctly

**Starter Code**:
```java
public class AreaCalculator {
    // TODO: Create overloaded area methods
    
    public static void main(String[] args) {
        // TODO: Test all overloads
    }
}
```

**Reflection Prompt**:
- How does Java determine which method to call?
- What are the benefits of overloading?
- When would you use overloading vs different method names?

### Exercise 3: Scope and Lifetime

**Objective**: Understand variable scope

**Task Description**:
Create a program that demonstrates variable scope and lifetime.

**Acceptance Criteria**:
- [ ] Shows class variable scope
- [ ] Shows instance variable scope
- [ ] Shows local variable scope
- [ ] Shows block variable scope

**Starter Code**:
```java
public class ScopeDemo {
    // TODO: Declare variables at different scopes
    
    public void demonstrateScope() {
        // TODO: Show scope limitations
    }
}
```

**Reflection Prompt**:
- Why is scope important?
- How does scope affect memory usage?
- What are the benefits of limiting scope?

---

## 🧪 Quiz

### Question 1: What is the return type of a method that doesn't return anything?

A) null  
B) void  
C) undefined  
D) Object  

**Answer**: B) void

**Explanation**: Methods that don't return a value use `void` as the return type.

### Question 2: What is the scope of a local variable?

A) The entire class  
B) The entire method  
C) The block where it's declared  
D) The entire program  

**Answer**: C) The block where it's declared

**Explanation**: Local variables are accessible only within the block (method, if statement, loop, etc.) where they're declared.

### Question 3: What is method overloading?

A) Calling a method multiple times  
B) Methods with same name but different parameters  
C) Methods that call other methods  
D) Methods that return multiple values  

**Answer**: B) Methods with same name but different parameters

**Explanation**: Method overloading allows multiple methods with the same name as long as they have different parameter lists.

### Question 4: What happens when you pass a primitive to a method?

A) The original value is modified  
B) A copy of the value is passed  
C) The reference is passed  
D) Nothing happens  

**Answer**: B) A copy of the value is passed

**Explanation**: Java passes primitives by value, meaning a copy is passed to the method.

### Question 5: What is the base case in recursion?

A) The first call to the method  
B) The condition that stops recursion  
C) The recursive call  
D) The return statement  

**Answer**: B) The condition that stops recursion

**Explanation**: The base case is the condition that stops the recursion and prevents infinite loops.

---

## 🚀 Advanced Challenge

### Challenge: Advanced Calculator Features

**Difficulty**: Intermediate

**Objective**: Extend the calculator with advanced features

**Description**:
Add features like:
- Expression evaluation (e.g., "2 + 3 * 4")
- Variable storage (x = 5, y = 10)
- Function definitions
- Graphing capabilities

**Requirements**:
- [ ] Parse and evaluate expressions
- [ ] Store and use variables
- [ ] Define custom functions
- [ ] Display graphs

**Hints**:
1. Use a parser for expressions
2. Use a HashMap for variables
3. Implement a function registry
4. Use ASCII art for graphs

**Stretch Goals**:
- [ ] Support complex numbers
- [ ] Matrix operations
- [ ] Statistical functions
- [ ] Unit conversions

---

## 🏆 Best Practices

### Method Design

1. **Single Responsibility**
   - Each method should do one thing
   - Keep methods focused and small
   - Avoid methods that do too much

2. **Meaningful Names**
   ```java
   // ❌ Bad
   public void process(int x) { }
   
   // ✅ Good
   public void calculateFactorial(int n) { }
   ```

3. **Appropriate Parameters**
   ```java
   // ❌ Bad: Too many parameters
   public void calculate(int a, int b, int c, int d, int e) { }
   
   // ✅ Good: Use objects for related data
   public void calculate(Numbers numbers) { }
   ```

### Scope Management

1. **Minimize Scope**
   - Declare variables as close to use as possible
   - Use smallest scope necessary
   - Reduces memory usage and confusion

2. **Avoid Global Variables**
   ```java
   // ❌ Bad: Global variable
   public static int globalCounter = 0;
   
   // ✅ Good: Instance or local variable
   private int counter = 0;
   ```

3. **Use Meaningful Names**
   - Variable names should indicate scope
   - Avoid single-letter names (except loop counters)
   - Use descriptive names

### Recursion

1. **Always Have Base Case**
   - Prevents infinite recursion
   - Clearly defines when to stop

2. **Make Progress**
   - Each recursive call should move toward base case
   - Avoid infinite loops

3. **Consider Alternatives**
   - Recursion can be slower than iteration
   - Use iteration for performance-critical code
   - Use recursion for clarity when appropriate

---

## 🔗 Next Steps

### Ready for Lab 04?

You've mastered methods and scope! Next, learn about Object-Oriented Programming basics.

**Next Lab**: [Lab 04: OOP Basics](../04-oop-basics/README.md)

### Additional Resources

- [Java Methods](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)
- [Scope and Lifetime](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/scope.html)
- [Recursion](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)

---

## ✅ Completion Checklist

- [ ] Completed all step-by-step coding tasks
- [ ] Built and tested the scientific calculator
- [ ] Solved all exercises
- [ ] Passed the quiz (80%+)
- [ ] Attempted the advanced challenge
- [ ] Reviewed best practices
- [ ] Added project to portfolio
- [ ] Reflected on learning outcomes

---

**Congratulations on completing Lab 03! 🎉**

You've successfully mastered methods and scope. You can now write modular, well-organized code with proper variable management. Ready for the next challenge? Move on to [Lab 04: OOP Basics](../04-oop-basics/README.md).