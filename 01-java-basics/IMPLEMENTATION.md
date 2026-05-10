# Java Basics - Implementation Guide

## Module Overview

This module covers the foundational concepts of Java programming: variables, data types, control flow, methods, arrays, and basic OOP principles. The implementations below demonstrate both pure Java approaches and production-ready patterns.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Variables and Data Types

```java
package com.learning.basics.implementation;

/**
 * Pure Java implementation of variable operations.
 * No external libraries used - demonstrating Java fundamentals.
 */
public class VariablesDemo {
    
    // Primitive types
    private int integerValue;
    private double doubleValue;
    private boolean booleanValue;
    private char charValue;
    private long longValue;
    private float floatValue;
    private short shortValue;
    private byte byteValue;
    
    // Reference types
    private String stringValue;
    
    // Constants
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_NAME = "Unknown";
    
    // Instance initializer block demonstration
    {
        integerValue = 0;
        doubleValue = 0.0;
        booleanValue = false;
        stringValue = DEFAULT_NAME;
    }
    
    // Constructor
    public VariablesDemo() {
    }
    
    public VariablesDemo(int integerValue, double doubleValue, String stringValue) {
        this.integerValue = integerValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
    }
    
    // Getters and Setters
    public int getIntegerValue() {
        return integerValue;
    }
    
    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue;
    }
    
    // Type conversion methods
    public int stringToInt(String str) {
        return Integer.parseInt(str);
    }
    
    public double stringToDouble(String str) {
        return Double.parseDouble(str);
    }
    
    public String intToString(int value) {
        return String.valueOf(value);
    }
    
    public int doubleToInt(double value) {
        return (int) value; // Explicit casting
    }
    
    public double intToDouble(int value) {
        return value; // Implicit widening
    }
    
    // Autoboxing and unboxing
    public Integer autoBox(int value) {
        return value; // Boxing - primitive to wrapper
    }
    
    public int unBox(Integer wrapper) {
        return wrapper; // Unboxing - wrapper to primitive
    }
    
    // Variable scope demonstration
    public void demonstrateScope() {
        // Block scope
        {
            int blockScoped = 10;
            System.out.println("Block scoped: " + blockScoped);
        }
        // blockScoped is not accessible here
        
        // Method scope
        int methodScoped = 20;
        System.out.println("Method scoped: " + methodScoped);
    }
    
    public static void demonstrateStaticContext() {
        // Static method can only access static members
        System.out.println("Max size: " + MAX_SIZE);
        System.out.println("Default name: " + DEFAULT_NAME);
    }
}
```

### 1.2 Control Flow Implementation

```java
package com.learning.basics.implementation;

/**
 * Control flow demonstration - if-else, switch, loops
 */
public class ControlFlowDemo {
    
    // If-else demonstration
    public String checkNumber(int number) {
        if (number > 0) {
            return "Positive";
        } else if (number < 0) {
            return "Negative";
        } else {
            return "Zero";
        }
    }
    
    // Switch expression (Java 14+)
    public String getDayType(int dayNumber) {
        return switch (dayNumber) {
            case 1, 2, 3, 4, 5 -> "Weekday";
            case 6, 7 -> "Weekend";
            default -> "Invalid";
        };
    }
    
    // Traditional switch statement
    public String getMonthName(int month) {
        String result;
        switch (month) {
            case 1: result = "January"; break;
            case 2: result = "February"; break;
            case 3: result = "March"; break;
            case 4: result = "April"; break;
            case 5: result = "May"; break;
            case 6: result = "June"; break;
            case 7: result = "July"; break;
            case 8: result = "August"; break;
            case 9: result = "September"; break;
            case 10: result = "October"; break;
            case 11: result = "November"; break;
            case 12: result = "December"; break;
            default: result = "Invalid";
        }
        return result;
    }
    
    // For loop
    public void printNumbers(int start, int end) {
        for (int i = start; i <= end; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
    
    // Enhanced for loop (for-each)
    public int sumArray(int[] numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
    
    // While loop
    public int countDigits(int number) {
        int count = 0;
        int n = Math.abs(number);
        while (n > 0) {
            count++;
            n /= 10;
        }
        return count == 0 ? 1 : count; // Handle zero
    }
    
    // Do-while loop
    public String getUserInput() {
        String input = "";
        int attempts = 0;
        do {
            attempts++;
            input = "user input"; // Simulated
        } while (input.isEmpty() && attempts < 3);
        return input;
    }
    
    // Break and continue
    public int findFirstEven(int[] numbers) {
        for (int num : numbers) {
            if (num % 2 == 0) {
                return num; // Break - exit loop
            }
        }
        return -1;
    }
    
    public void printOddNumbers(int limit) {
        for (int i = 1; i <= limit; i++) {
            if (i % 2 == 0) {
                continue; // Skip even numbers
            }
            System.out.print(i + " ");
        }
        System.out.println();
    }
    
    // Nested loops with label
    public void findPairSum(int[] arr, int target) {
        outerLoop:
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] + arr[j] == target) {
                    System.out.println("Found: " + arr[i] + " + " + arr[j]);
                    break outerLoop;
                }
            }
        }
    }
}
```

### 1.3 Methods Implementation

```java
package com.learning.basics.implementation;

/**
 * Method demonstrations - overloading, recursion, varargs
 */
public class MethodsDemo {
    
    // Method overloading - same name, different parameters
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public String add(String a, String b) {
        return a + b;
    }
    
    // Variable arguments (varargs)
    public int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    public double average(double... values) {
        if (values.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double val : values) {
            sum += val;
        }
        return sum / values.length;
    }
    
    // Recursion - factorial
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative number not allowed");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    
    // Recursion - Fibonacci
    public long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative index not allowed");
        }
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    // Recursion with memoization
    private long[] memo;
    
    public long fibonacciOptimized(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative index not allowed");
        }
        if (memo == null || memo.length <= n) {
            memo = new long[n + 1];
        }
        if (memo[n] != 0) {
            return memo[n];
        }
        if (n <= 1) {
            memo[n] = n;
        } else {
            memo[n] = fibonacciOptimized(n - 1) + fibonacciOptimized(n - 2);
        }
        return memo[n];
    }
    
    // Recursion - sum of digits
    public int sumOfDigits(int number) {
        number = Math.abs(number);
        if (number < 10) {
            return number;
        }
        return number % 10 + sumOfDigits(number / 10);
    }
    
    // Recursion - reverse string
    public String reverseString(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return reverseString(str.substring(1)) + str.charAt(0);
    }
    
    // Recursion - palindrome check
    public boolean isPalindrome(String str) {
        if (str == null || str.length() <= 1) {
            return true;
        }
        if (str.charAt(0) != str.charAt(str.length() - 1)) {
            return false;
        }
        return isPalindrome(str.substring(1, str.length() - 1));
    }
    
    // Method with primitive pass-by-value
    public void modifyPrimitive(int num) {
        num = 100; // Only modifies local copy
    }
    
    // Method with reference pass-by-value
    public void modifyObject(StringBuilder sb) {
        sb.append(" modified"); // Modifies the object
    }
    
    // Return multiple values using array
    public int[] getMinMax(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return new int[]{0, 0};
        }
        int min = numbers[0];
        int max = numbers[0];
        for (int num : numbers) {
            if (num < min) min = num;
            if (num > max) max = num;
        }
        return new int[]{min, max};
    }
}
```

### 1.4 Arrays Implementation

```java
package com.learning.basics.implementation;

/**
 * Array operations - creation, manipulation, searching, sorting
 */
public class ArraysDemo {
    
    // Array creation and initialization
    public int[] createArray(int size) {
        return new int[size];
    }
    
    public int[] initializeArray() {
        int[] arr = {1, 2, 3, 4, 5};
        return arr;
    }
    
    public String[] createStringArray(int size) {
        return new String[size];
    }
    
    // Array traversal
    public void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
    
    public void printArrayEnhanced(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    // Array searching - Linear search
    public int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    // Array searching - Binary search (array must be sorted)
    public int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            }
            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
    
    // Array sorting - Bubble sort
    public void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    
    // Array sorting - Selection sort
    public void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            int temp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = temp;
        }
    }
    
    // Array sorting - Insertion sort
    public void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
    
    // Array manipulation - Find maximum
    public int findMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
    
    // Array manipulation - Find minimum
    public int findMin(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }
    
    // Array manipulation - Reverse array
    public void reverseArray(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
    
    // Array manipulation - Remove duplicates
    public int[] removeDuplicates(int[] arr) {
        if (arr.length <= 1) {
            return arr;
        }
        
        int[] temp = new int[arr.length];
        int j = 0;
        
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] != arr[i + 1]) {
                temp[j++] = arr[i];
            }
        }
        temp[j++] = arr[arr.length - 1];
        
        int[] result = new int[j];
        System.arraycopy(temp, 0, result, 0, j);
        return result;
    }
    
    // Two-dimensional array
    public void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
    
    // Matrix operations - Transpose
    public int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] result = new int[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Spring Boot Configuration

```java
package com.learning.basics.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasicsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BasicsApplication.class, args);
    }
}
```

### 2.2 Configuration Properties

```java
package com.learning.basics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.basics")
public class BasicsConfig {
    
    private int maxArraySize = 1000;
    private int defaultTimeout = 30;
    private boolean enableValidation = true;
    private String defaultCharset = "UTF-8";
    
    // Getters and setters
    public int getMaxArraySize() {
        return maxArraySize;
    }
    
    public void setMaxArraySize(int maxArraySize) {
        this.maxArraySize = maxArraySize;
    }
    
    public int getDefaultTimeout() {
        return defaultTimeout;
    }
    
    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }
    
    public boolean isEnableValidation() {
        return enableValidation;
    }
    
    public void setEnableValidation(boolean enableValidation) {
        this.enableValidation = enableValidation;
    }
    
    public String getDefaultCharset() {
        return defaultCharset;
    }
    
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }
}
```

### 2.3 Service Layer with Validation

```java
package com.learning.basics.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class BasicsService {
    
    public record CalculationResult(
        int sum,
        int product,
        double average,
        int min,
        int max
    ) {}
    
    public CalculationResult calculate(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return new CalculationResult(0, 0, 0, 0, 0);
        }
        
        int sum = 0;
        int product = 1;
        int min = numbers[0];
        int max = numbers[0];
        
        for (int num : numbers) {
            sum += num;
            product *= num;
            if (num < min) min = num;
            if (num > max) max = num;
        }
        
        double average = (double) sum / numbers.length;
        
        return new CalculationResult(sum, product, average, min, max);
    }
    
    public List<Integer> findPrimes(int limit) {
        List<Integer> primes = new ArrayList<>();
        
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        
        return primes;
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
```

### 2.4 REST Controller

```java
package com.learning.basics.controller;

import com.learning.basics.service.BasicsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/basics")
public class BasicsController {
    
    private final BasicsService service;
    
    public BasicsController(BasicsService service) {
        this.service = service;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<BasicsService.CalculationResult> calculate(
            @RequestBody int[] numbers) {
        var result = service.calculate(numbers);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/primes/{limit}")
    public ResponseEntity<List<Integer>> getPrimes(@PathVariable int limit) {
        if (limit < 2 || limit > 10000) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.findPrimes(limit));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
```

### 2.5 Application Properties

```yaml
# application.yml
spring:
  application:
    name: java-basics

server:
  port: 8080

app:
  basics:
    max-array-size: 10000
    default-timeout: 60
    enable-validation: true
    default-charset: UTF-8
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Variable Operations

**Step 1: Understanding Primitive Types**
- Java has 8 primitive types: `byte`, `short`, `int`, `long`, `float`, `double`, `boolean`, `char`
- Each has a specific range and memory footprint

**Step 2: Reference Types**
- Objects and arrays are reference types
- They store memory addresses, not actual values

**Step 3: Type Conversion**
- Widening (implicit): `int` → `long` → `double`
- Narrowing (explicit): `double` → `int` requires casting

**Step 4: Autoboxing**
- Automatic conversion between primitives and their wrapper classes
- Enabled in Java 5

### 3.2 Control Flow

**Step 1: Conditional Statements**
- `if-else` for binary decisions
- `switch` for multi-way branching (modern switch expressions in Java 14+)

**Step 2: Loops**
- `for`: when number of iterations is known
- `while`: when condition is checked before iteration
- `do-while`: when at least one iteration is guaranteed

**Step 3: Branching**
- `break`: exits loop
- `continue`: skips current iteration
- Labels: control nested loop execution

### 3.3 Methods

**Step 1: Method Signature**
- Name + parameter types = method signature
- Return type is not part of signature

**Step 2: Overloading**
- Multiple methods with same name, different parameters
- Compile-time polymorphism

**Step 3: Recursion**
- Method calls itself
- Must have termination condition
- Stack overflow risk for deep recursion

**Step 4: Pass-by-Value**
- Primitives: copy of value passed
- Objects: copy of reference passed (both refer to same object)

### 3.4 Arrays

**Step 1: Array Declaration**
- `int[] arr` or `int arr[]`
- Fixed size after creation

**Step 2: Memory Allocation**
- `new` keyword allocates contiguous memory
- Default values for primitives: 0, 0.0, false

**Step 3: Searching**
- Linear: O(n) - checks each element
- Binary: O(log n) - requires sorted array

**Step 4: Sorting**
- Bubble: O(n²) - simple but inefficient
- Selection: O(n²) - better than bubble
- Arrays.sort() uses TimSort: O(n log n)

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Location |
|---------|---------------|----------|
| **Primitives** | 8 primitive types with default values | VariablesDemo.java |
| **Type Conversion** | Widening, narrowing, autoboxing | VariablesDemo.java |
| **Control Flow** | If-else, switch, loops | ControlFlowDemo.java |
| **Methods** | Overloading, recursion, varargs | MethodsDemo.java |
| **Arrays** | Creation, sorting, searching | ArraysDemo.java |
| **Encapsulation** | Private fields, public methods | VariablesDemo.java |
| **Configuration** | Spring Boot properties | BasicsConfig.java |
| **REST API** | Spring Web controllers | BasicsController.java |
| **Validation** | Input validation in service | BasicsService.java |

---

## Key Takeaways

1. **Variables**: Understand primitives vs references, type conversion rules
2. **Control Flow**: Master all loop types and branching statements
3. **Methods**: Practice overloading and recursion
4. **Arrays**: Know sorting algorithms and their complexity
5. **Production**: Use Spring Boot for enterprise applications

---

## Next Steps

- Practice with more complex recursive problems
- Implement additional sorting algorithms (merge sort, quick sort)
- Explore Java standard library utilities
- Build more complex REST APIs with Spring Boot