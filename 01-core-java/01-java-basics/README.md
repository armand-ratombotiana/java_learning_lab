# 01 - Java Basics

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)
![Coverage](https://img.shields.io/badge/Coverage-80%25+-green?style=for-the-badge)

**Foundation module covering essential Java programming concepts**

</div>

---

## 📚 Overview

This module introduces the fundamental concepts of Java programming, including variables, data types, operators, control flow, arrays, and strings. It provides a solid foundation for all subsequent Java learning.

### 🎯 Learning Objectives

By completing this module, you will:

- ✅ Understand Java variables and their scope
- ✅ Master all 8 primitive data types and wrapper classes
- ✅ Use all Java operators effectively
- ✅ Control program flow with conditionals and loops
- ✅ Work with single and multi-dimensional arrays
- ✅ Manipulate strings efficiently
- ✅ Apply best practices for Java basics

---

## 📖 Topics Covered

### 1. Variables (`Variables.java`)

- **Local Variables:** Method-scoped variables
- **Instance Variables:** Object-level variables
- **Static Variables:** Class-level variables
- **Constants:** Final variables
- **Variable Scope:** Understanding accessibility
- **Naming Conventions:** Java coding standards

**Key Concepts:**
```java
// Local variable
int localVar = 10;

// Instance variable
private int instanceVar = 100;

// Static variable
private static int staticVar = 200;

// Constant
private static final String CONSTANT = "VALUE";
```

---

### 2. Data Types (`DataTypes.java`)

#### Primitive Types (8 total):
| Type | Size | Range | Default |
|------|------|-------|---------|
| `byte` | 8-bit | -128 to 127 | 0 |
| `short` | 16-bit | -32,768 to 32,767 | 0 |
| `int` | 32-bit | -2³¹ to 2³¹-1 | 0 |
| `long` | 64-bit | -2⁶³ to 2⁶³-1 | 0L |
| `float` | 32-bit | IEEE 754 | 0.0f |
| `double` | 64-bit | IEEE 754 | 0.0d |
| `char` | 16-bit | 0 to 65,535 | '\u0000' |
| `boolean` | 1-bit | true/false | false |

#### Reference Types:
- **String:** Immutable character sequences
- **Arrays:** Fixed-size collections
- **Objects:** Custom data types

#### Type Conversion:
- **Implicit (Widening):** Automatic conversion
- **Explicit (Narrowing):** Manual casting
- **Wrapper Classes:** Autoboxing/Unboxing

---

### 3. Operators (`Operators.java`)

#### Arithmetic Operators
```java
+ (addition)
- (subtraction)
* (multiplication)
/ (division)
% (modulus)
++ (increment)
-- (decrement)
```

#### Relational Operators
```java
== (equal to)
!= (not equal to)
> (greater than)
< (less than)
>= (greater than or equal to)
<= (less than or equal to)
```

#### Logical Operators
```java
&& (logical AND)
|| (logical OR)
! (logical NOT)
```

#### Bitwise Operators
```java
& (bitwise AND)
| (bitwise OR)
^ (bitwise XOR)
~ (bitwise NOT)
<< (left shift)
>> (right shift)
>>> (unsigned right shift)
```

#### Assignment Operators
```java
= (simple assignment)
+= (add and assign)
-= (subtract and assign)
*= (multiply and assign)
/= (divide and assign)
%= (modulus and assign)
```

#### Special Operators
```java
? : (ternary operator)
instanceof (type checking)
```

---

### 4. Control Flow (`ControlFlow.java`)

#### Conditional Statements
```java
// if-else
if (condition) {
    // code
} else if (condition) {
    // code
} else {
    // code
}

// switch (traditional)
switch (value) {
    case 1:
        // code
        break;
    default:
        // code
}

// switch (enhanced - Java 14+)
int result = switch (value) {
    case 1, 2, 3 -> 10;
    case 4, 5 -> 20;
    default -> 0;
};
```

#### Loops
```java
// for loop
for (int i = 0; i < 10; i++) {
    // code
}

// enhanced for loop
for (int num : numbers) {
    // code
}

// while loop
while (condition) {
    // code
}

// do-while loop
do {
    // code
} while (condition);
```

#### Jump Statements
```java
break;      // Exit loop
continue;   // Skip iteration
return;     // Exit method
```

---

### 5. Arrays (`ArraysDemo.java`)

#### Single-Dimensional Arrays
```java
// Declaration and initialization
int[] numbers = new int[5];
int[] values = {1, 2, 3, 4, 5};

// Accessing elements
int first = numbers[0];
int last = numbers[numbers.length - 1];

// Iterating
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}

// Enhanced for loop
for (int num : numbers) {
    System.out.println(num);
}
```

#### Multi-Dimensional Arrays
```java
// 2D array
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Jagged array
int[][] jagged = {
    {1, 2},
    {3, 4, 5},
    {6, 7, 8, 9}
};

// 3D array
int[][][] cube = new int[2][3][4];
```

#### Array Operations
- **Searching:** Linear search, binary search
- **Sorting:** Arrays.sort()
- **Copying:** clone(), Arrays.copyOf()
- **Comparing:** Arrays.equals()
- **Filling:** Arrays.fill()

---

### 6. Strings (`StringsDemo.java`)

#### String Creation
```java
// String literal (String pool)
String str1 = "Hello";

// Using new keyword
String str2 = new String("Hello");

// From char array
char[] chars = {'H', 'e', 'l', 'l', 'o'};
String str3 = new String(chars);
```

#### Common String Methods
```java
length()              // Get string length
charAt(index)         // Get character at index
substring(start, end) // Extract substring
indexOf(str)          // Find index of substring
contains(str)         // Check if contains substring
startsWith(str)       // Check if starts with
endsWith(str)         // Check if ends with
isEmpty()             // Check if empty
isBlank()             // Check if blank (Java 11+)
```

#### String Manipulation
```java
trim()                // Remove whitespace
toUpperCase()         // Convert to uppercase
toLowerCase()         // Convert to lowercase
replace(old, new)     // Replace characters
split(delimiter)      // Split into array
join(delimiter, arr)  // Join array elements
repeat(count)         // Repeat string (Java 11+)
```

#### StringBuilder
```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.insert(5, " World");
sb.delete(0, 5);
sb.reverse();
String result = sb.toString();
```

#### String Formatting
```java
// String.format()
String formatted = String.format("Name: %s, Age: %d", name, age);

// printf()
System.out.printf("Value: %.2f%n", 3.14159);

// Text blocks (Java 15+)
String json = """
    {
        "name": "John",
        "age": 30
    }
    """;
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Running the Module

```bash
# Navigate to module directory
cd 01-core-java/01-java-basics

# Compile and run
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report
```

### Expected Output

```
=== Java Basics Module ===

1. Variables Demo:
Local Variable: 10
Local String: Local Variable
Static Variable: 200
Constant: CONSTANT_VALUE
...

2. Data Types Demo:
Primitive Data Types:
byte: 127 (Range: -128 to 127)
short: 32000 (Range: -32,768 to 32,767)
...

3. Operators Demo:
Arithmetic Operators:
a = 10, b = 3
Addition (a + b): 13
...

4. Control Flow Demo:
If-Else Statements:
Grade: B
...

5. Arrays Demo:
Single-Dimensional Arrays:
First element of numbers2: 1
...

6. Strings Demo:
String Creation:
str1 == str2 (same reference): true
...

=== Module Complete ===
```

---

## 📝 Code Examples

### Example 1: Variables and Scope

```java
public class ScopeExample {
    // Instance variable
    private int instanceVar = 100;
    
    // Static variable
    private static int staticVar = 200;
    
    public void demonstrateScope() {
        // Local variable
        int localVar = 10;
        
        if (true) {
            // Block-scoped variable
            int blockVar = 20;
            System.out.println(localVar);  // Accessible
            System.out.println(blockVar);  // Accessible
        }
        
        // System.out.println(blockVar); // Error: not accessible
    }
}
```

### Example 2: Type Conversion

```java
// Implicit conversion (widening)
int intValue = 100;
long longValue = intValue;      // int to long
double doubleValue = longValue; // long to double

// Explicit conversion (narrowing)
double doubleVal = 100.99;
int intVal = (int) doubleVal;   // 100 (loses decimal)

// String conversion
String numberStr = "123";
int number = Integer.parseInt(numberStr);
String str = String.valueOf(number);
```

### Example 3: Control Flow

```java
// Enhanced switch (Java 14+)
String dayType = switch (dayOfWeek) {
    case 1, 2, 3, 4, 5 -> "Weekday";
    case 6, 7 -> "Weekend";
    default -> "Invalid";
};

// For-each loop
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}
```

### Example 4: Array Operations

```java
int[] numbers = {5, 2, 8, 1, 9};

// Find min and max
int min = Arrays.stream(numbers).min().getAsInt();
int max = Arrays.stream(numbers).max().getAsInt();

// Sort
Arrays.sort(numbers);

// Search (requires sorted array)
int index = Arrays.binarySearch(numbers, 5);

// Copy
int[] copy = Arrays.copyOf(numbers, numbers.length);
```

### Example 5: String Manipulation

```java
String text = "  Hello World  ";

// Trim whitespace
String trimmed = text.trim();

// Split into words
String[] words = text.trim().split("\\s+");

// Join words
String joined = String.join("-", words);

// StringBuilder for efficiency
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append("a");
}
String result = sb.toString();
```

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=VariablesTest

# Run with coverage
mvn clean test jacoco:report
```

### Test Coverage

The module includes comprehensive tests with 80%+ coverage:

- `VariablesTest.java` - Tests for variable concepts
- `DataTypesTest.java` - Tests for data types
- `OperatorsTest.java` - Tests for operators
- `ControlFlowTest.java` - Tests for control flow
- `ArraysDemoTest.java` - Tests for arrays
- `StringsDemoTest.java` - Tests for strings

### Coverage Report

After running tests, view the coverage report:

```bash
open target/site/jacoco/index.html
```

---

## 💡 Best Practices

### 1. Variable Naming
```java
// Good
int studentAge;
String firstName;
final double PI = 3.14159;

// Bad
int a;
String s;
double pi;
```

### 2. Type Selection
```java
// Use appropriate types
byte age = 25;           // Small range
int population = 1000000; // Standard integer
long distance = 9999999999L; // Large numbers
double price = 19.99;    // Decimal values
```

### 3. String Handling
```java
// Use StringBuilder for concatenation in loops
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append("value");
}

// Avoid string concatenation in loops
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "value"; // Inefficient!
}
```

### 4. Array Operations
```java
// Check array bounds
if (index >= 0 && index < array.length) {
    int value = array[index];
}

// Use enhanced for loop when index not needed
for (int num : numbers) {
    System.out.println(num);
}
```

### 5. Control Flow
```java
// Use enhanced switch when possible (Java 14+)
String result = switch (value) {
    case 1, 2, 3 -> "Low";
    case 4, 5, 6 -> "Medium";
    case 7, 8, 9 -> "High";
    default -> "Unknown";
};
```

---

## 🎯 Exercises

### Exercise 1: Temperature Converter
Create a program that converts temperatures between Celsius and Fahrenheit.

### Exercise 2: Array Statistics
Write methods to calculate mean, median, and mode of an integer array.

### Exercise 3: String Analyzer
Create a program that analyzes a string and reports:
- Character count
- Word count
- Vowel count
- Palindrome check

### Exercise 4: Pattern Printer
Write a program that prints various patterns using loops:
- Right triangle
- Pyramid
- Diamond

### Exercise 5: Array Sorter
Implement bubble sort and selection sort algorithms.

---

## 📚 Additional Resources

### Official Documentation
- [Java Language Specification](https://docs.oracle.com/javase/specs/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)

### Recommended Reading
- "Effective Java" by Joshua Bloch
- "Java: The Complete Reference" by Herbert Schildt
- "Head First Java" by Kathy Sierra

### Online Resources
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Baeldung Java Basics](https://www.baeldung.com/java-basics)
- [GeeksforGeeks Java](https://www.geeksforgeeks.org/java/)

---

## 🤝 Contributing

Contributions are welcome! Please read the [Contributing Guidelines](../../CONTRIBUTING.md) before submitting pull requests.

---

## 📄 License

This module is part of the Java Learning Journey project. See [LICENSE](../../LICENSE) for details.

---

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/yourusername/JavaLearning/issues)
- **Discussions:** [GitHub Discussions](https://github.com/yourusername/JavaLearning/discussions)

---

<div align="center">

**[⬅️ Previous Module](../README.md)** | **[Next Module ➡️](../02-oop-concepts/README.md)**

Made with ❤️ by the Java Learning Team

</div>