# Java Basics - Deep Dive

## Table of Contents

1. [Variables and Data Types](#variables-and-data-types)
2. [Operators](#operators)
3. [Control Flow](#control-flow)
4. [Methods](#methods)
5. [Arrays](#arrays)
6. [Basic OOP](#basic-oop)

---

## Variables and Data Types

### Primitive Types

Java has 8 primitive types:

| Type | Size | Range | Default |
|------|------|-------|---------|
| `byte` | 1 byte | -128 to 127 | 0 |
| `short` | 2 bytes | -32,768 to 32,767 | 0 |
| `int` | 4 bytes | -2^31 to 2^31-1 | 0 |
| `long` | 8 bytes | -2^63 to 2^63-1 | 0L |
| `float` | 4 bytes | ~±3.4E38 | 0.0f |
| `double` | 8 bytes | ~±1.8E308 | 0.0d |
| `boolean` | 1 bit | true/false | false |
| `char` | 2 bytes | Unicode characters | '\u0000' |

### Reference Types

```java
// String is immutable
String name = "John";
String greeting = new String("Hello");

// Object references
Object obj = new Object();
Integer wrapper = 42;  // Autoboxing
```

### Type Conversion

```java
// Implicit (widening)
int i = 100;
long l = i;  // int to long - automatic

// Explicit (narrowing)
double d = 65.9;
int c = (int) d;  // double to int - requires cast
```

### Variable Scopes

```java
public class ScopeExample {
    static int classScope = 10;  // Class scope
    
    public void methodScope() {
        int methodVar = 20;  // Method scope
        
        if (true) {
            int blockVar = 30;  // Block scope
            System.out.println(classScope);  // ✓
            System.out.println(methodVar);   // ✓
            System.out.println(blockVar);     // ✓
        }
        // System.out.println(blockVar);  // ✗ Error!
    }
}
```

---

## Operators

### Arithmetic Operators

```java
int a = 10, b = 3;

System.out.println(a + b);   // 13 (addition)
System.out.println(a - b);   // 7  (subtraction)
System.out.println(a * b);   // 30 (multiplication)
System.out.println(a / b);   // 3  (integer division)
System.out.println(a % b);   // 1  (modulo/remainder)
```

### Increment/Decrement

```java
int x = 5;
System.out.println(x++);  // 5 (post-increment, returns before)
System.out.println(x);    // 6

int y = 5;
System.out.println(++y);  // 6 (pre-increment, returns after)
System.out.println(y);    // 6
```

### Comparison Operators

```java
int a = 5, b = 10;

System.out.println(a == b);  // false (equal)
System.out.println(a != b);  // true  (not equal)
System.out.println(a > b);   // false (greater than)
System.out.println(a < b);   // true  (less than)
System.out.println(a >= b);  // false
System.out.println(a <= b);  // true
```

### Logical Operators

```java
boolean x = true, y = false;

System.out.println(x && y);  // false (AND)
System.out.println(x || y);  // true  (OR)
System.out.println(!x);      // false (NOT)
System.out.println(x ^ y);   // true  (XOR)
```

### Bitwise Operators

```java
int a = 5;   // 0101 in binary
int b = 3;   // 0011 in binary

System.out.println(a & b);   // 1  (0101 & 0011 = 0001)
System.out.println(a | b);   // 7  (0101 | 0011 = 0111)
System.out.println(a ^ b);   // 6  (0101 ^ 0011 = 0110)
System.out.println(~a);      // -6 (bitwise NOT)
System.out.println(a << 1);  // 10 (left shift)
System.out.println(a >> 1);  // 2  (right shift)
```

### Ternary Operator

```java
int age = 20;
String status = (age >= 18) ? "Adult" : "Minor";

// Equivalent to:
String status2;
if (age >= 18) {
    status2 = "Adult";
} else {
    status2 = "Minor";
}
```

---

## Control Flow

### If-Else Statements

```java
int score = 85;

if (score >= 90) {
    grade = 'A';
} else if (score >= 80) {
    grade = 'B';
} else if (score >= 70) {
    grade = 'C';
} else if (score >= 60) {
    grade = 'D';
} else {
    grade = 'F';
}
```

### Switch Statement

```java
int day = 3;
String dayName;

switch (day) {
    case 1:
        dayName = "Monday";
        break;
    case 2:
        dayName = "Tuesday";
        break;
    case 3:
        dayName = "Wednesday";
        break;
    case 4:
        dayName = "Thursday";
        break;
    case 5:
        dayName = "Friday";
        break;
    case 6:
    case 7:
        dayName = "Weekend";
        break;
    default:
        dayName = "Invalid";
}

// Java 14+ switch expression
String result = switch (day) {
    case 1, 2, 3, 4, 5 -> "Weekday";
    case 6, 7 -> "Weekend";
    default -> "Invalid";
};
```

### For Loop

```java
// Traditional for loop
for (int i = 0; i < 5; i++) {
    System.out.println("Iteration: " + i);
}

// Enhanced for loop (for-each)
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

// Infinite loop with break
for (;;) {
    if (condition) {
        break;
    }
}
```

### While Loop

```java
int count = 0;
while (count < 5) {
    System.out.println("Count: " + count);
    count++;
}

// Do-while (always executes at least once)
int n = 0;
do {
    System.out.println(n);
    n++;
} while (n < 5);
```

### Break and Continue

```java
// Break - exit loop entirely
for (int i = 0; i < 10; i++) {
    if (i == 5) {
        break;  // Exit when i is 5
    }
    System.out.println(i);
}

// Continue - skip current iteration
for (int i = 0; i < 5; i++) {
    if (i == 2) {
        continue;  // Skip when i is 2
    }
    System.out.println(i);
}

// Labeled break (exit outer loop)
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) {
            break outer;  // Exit both loops
        }
        System.out.println(i + "," + j);
    }
}
```

---

## Methods

### Method Declaration

```java
public class Calculator {
    
    // Simple method
    public int add(int a, int b) {
        return a + b;
    }
    
    // Method with no return value
    public void printMessage(String message) {
        System.out.println(message);
    }
    
    // Method with multiple parameters
    public double calculateArea(double radius) {
        return Math.PI * radius * radius;
    }
    
    // Static method
    public static int multiply(int a, int b) {
        return a * b;
    }
}
```

### Method Overloading

```java
public class OverloadExample {
    
    // Different parameter types
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    // Different number of parameters
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // Compile error: same signature (return type doesn't matter)
    // public double add(int a, int b) { return (double)(a + b); }
}
```

### Varargs (Variable Arguments)

```java
public class VarargsDemo {
    
    public int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    public static void main(String[] args) {
        VarargsDemo demo = new VarargsDemo();
        System.out.println(demo.sum(1, 2, 3));       // 6
        System.out.println(demo.sum(1, 2, 3, 4, 5)); // 15
        System.out.println(demo.sum());             // 0
    }
}
```

### Recursion

```java
public class RecursionDemo {
    
    // Factorial
    public long factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    
    // Fibonacci
    public int fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    // Tail recursion optimization example
    public long factorialTail(int n, long accumulator) {
        if (n <= 1) {
            return accumulator;
        }
        return factorialTail(n - 1, n * accumulator);
    }
}
```

---

## Arrays

### Array Declaration and Initialization

```java
// Declaration
int[] numbers;

// Initialization
numbers = new int[5];  // Array of 5 ints, all 0

// Combined
int[] values = {1, 2, 3, 4, 5};

// Anonymous array
int[] nums = new int[]{1, 2, 3};

// Array of objects
String[] names = new String[3];
names[0] = "Alice";
names[1] = "Bob";
names[2] = "Charlie";
```

### Multi-Dimensional Arrays

```java
// 2D array
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Access element
int element = matrix[1][2];  // 6

// Jagged array (rows of different lengths)
int[][] jagged = new int[3][];
jagged[0] = new int[2];
jagged[1] = new int[4];
jagged[2] = new int[1];

// 3D array
int[][][] cube = new int[2][3][4];
```

### Common Array Operations

```java
// Find maximum
public int findMax(int[] arr) {
    int max = arr[0];
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] > max) {
            max = arr[i];
        }
    }
    return max;
}

// Reverse array
public void reverse(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
    }
}

// Search (linear)
public int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;
}

// Binary search (array must be sorted)
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;
}
```

### Arrays Utility Class

```java
import java.util.Arrays;

int[] arr = {5, 2, 8, 1, 9};

Arrays.sort(arr);  // Sort in place
Arrays.fill(arr, 0);  // Fill with 0
Arrays.copyOf(arr, 10);  // Copy with new length
Arrays.copyOfRange(arr, 2, 5);  // Copy range
Arrays.binarySearch(arr, 5);  // Binary search
Arrays.equals(arr1, arr2);  // Compare arrays
Arrays.toString(arr);  // "[1, 2, 5, 8, 9]"
Arrays.deepToString(matrix);  // For multi-dimensional
```

---

## Basic OOP

### Class Declaration

```java
public class Person {
    // Fields (instance variables)
    private String name;
    private int age;
    private static int count;  // Class variable
    
    // Static initializer
    static {
        count = 0;
    }
    
    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }
    
    // Static method
    public static int getCount() {
        return count;
    }
    
    // Instance method
    public void introduce() {
        System.out.println("Hi, I'm " + name + " and I'm " + age + " years old.");
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

### Using the Class

```java
public class Main {
    public static void main(String[] args) {
        // Create objects
        Person person1 = new Person("Alice", 25);
        Person person2 = new Person("Bob", 30);
        
        // Call methods
        person1.introduce();
        
        // Access fields through methods
        person1.setAge(26);
        System.out.println(person1.getAge());
        
        // Static access
        System.out.println(Person.getCount());
        
        // toString
        System.out.println(person1);
    }
}
```

### Access Modifiers

```java
public class AccessDemo {
    public String publicField = "Public";
    protected String protectedField = "Protected";
    private String privateField = "Private";
    String defaultField = "Default";  // package-private
    
    public void publicMethod() {}
    protected void protectedMethod() {}
    private void privateMethod() {}
    void defaultMethod() {}
}
```

### Static vs Instance

```java
public class StaticVsInstance {
    static int staticCounter = 0;  // Shared by all instances
    int instanceCounter = 0;       // Unique to each instance
    
    public StaticVsInstance() {
        staticCounter++;
        instanceCounter++;
    }
    
    public static void staticMethod() {
        // Can only access static members
        // System.out.println(instanceCounter);  // Error!
        System.out.println("Static counter: " + staticCounter);
    }
    
    public void instanceMethod() {
        // Can access both
        System.out.println("Instance: " + instanceCounter);
        System.out.println("Static: " + staticCounter);
    }
}
```

---

## Best Practices

1. **Naming Conventions**
   - Classes: PascalCase (e.g., `Person`, `BankAccount`)
   - Methods/Variables: camelCase (e.g., `getName`, `accountBalance`)
   - Constants: UPPER_SNAKE_CASE (e.g., `MAX_SIZE`, `DEFAULT_RATE`)

2. **Code Style**
   - Use meaningful variable names
   - Keep methods small and focused
   - Use comments for complex logic
   - Follow Java naming conventions

3. **Error Prevention**
   - Always initialize variables
   - Check array bounds
   - Use meaningful default values
   - Validate inputs to methods

4. **Performance**
   - Use StringBuilder for multiple concatenations
   - Avoid creating unnecessary objects
   - Use primitive types when possible
