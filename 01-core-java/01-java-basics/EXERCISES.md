# Exercises: Java Basics

<div align="center">

![Module](https://img.shields.io/badge/Module-01-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-30-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**30 comprehensive exercises for Java Basics module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-10)](#easy-exercises-1-10)
2. [Medium Exercises (11-20)](#medium-exercises-11-20)
3. [Hard Exercises (21-25)](#hard-exercises-21-25)
4. [Interview Exercises (26-30)](#interview-exercises-26-30)
5. [Solutions Summary](#solutions-summary)

---

## 🟢 Easy Exercises (1-10)

### Exercise 1: Hello World
**Difficulty:** Easy  
**Time:** 5 minutes  
**Topics:** Basic syntax, main method

**Problem:**
Write a Java program that prints "Hello, World!" to the console.

**Starter Code:**
```java
public class HelloWorld {
    public static void main(String[] args) {
        // TODO: Print "Hello, World!"
    }
}
```

**Solution:**
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**Variations:**
- Print multiple lines
- Use System.out.print() instead of println()
- Print with different formatting

---

### Exercise 2: Variables and Data Types
**Difficulty:** Easy  
**Time:** 10 minutes  
**Topics:** Variables, primitive types, type casting

**Problem:**
Create a program that declares variables of different primitive types (int, double, boolean, char, String) and prints their values.

**Starter Code:**
```java
public class DataTypes {
    public static void main(String[] args) {
        // TODO: Declare variables of different types
        // TODO: Print their values
    }
}
```

**Solution:**
```java
public class DataTypes {
    public static void main(String[] args) {
        int age = 25;
        double salary = 50000.50;
        boolean isActive = true;
        char grade = 'A';
        String name = "John";
        
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Salary: " + salary);
        System.out.println("Active: " + isActive);
        System.out.println("Grade: " + grade);
    }
}
```

**Variations:**
- Use different initial values
- Perform type casting
- Use final variables

---

### Exercise 3: Arithmetic Operations
**Difficulty:** Easy  
**Time:** 10 minutes  
**Topics:** Operators, arithmetic operations

**Problem:**
Write a program that performs basic arithmetic operations (addition, subtraction, multiplication, division, modulus) on two numbers.

**Starter Code:**
```java
public class Arithmetic {
    public static void main(String[] args) {
        int a = 20;
        int b = 5;
        
        // TODO: Perform arithmetic operations
        // TODO: Print results
    }
}
```

**Solution:**
```java
public class Arithmetic {
    public static void main(String[] args) {
        int a = 20;
        int b = 5;
        
        System.out.println("Addition: " + (a + b));
        System.out.println("Subtraction: " + (a - b));
        System.out.println("Multiplication: " + (a * b));
        System.out.println("Division: " + (a / b));
        System.out.println("Modulus: " + (a % b));
    }
}
```

**Variations:**
- Use double values
- Handle division by zero
- Use compound assignment operators

---

### Exercise 4: String Operations
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** String manipulation, string methods

**Problem:**
Write a program that performs various string operations: concatenation, length, substring, uppercase, lowercase, and contains.

**Starter Code:**
```java
public class StringOperations {
    public static void main(String[] args) {
        String str1 = "Hello";
        String str2 = "World";
        
        // TODO: Perform string operations
        // TODO: Print results
    }
}
```

**Solution:**
```java
public class StringOperations {
    public static void main(String[] args) {
        String str1 = "Hello";
        String str2 = "World";
        
        System.out.println("Concatenation: " + str1 + " " + str2);
        System.out.println("Length: " + str1.length());
        System.out.println("Substring: " + str1.substring(0, 3));
        System.out.println("Uppercase: " + str1.toUpperCase());
        System.out.println("Lowercase: " + str1.toLowerCase());
        System.out.println("Contains 'ell': " + str1.contains("ell"));
    }
}
```

**Variations:**
- Use StringBuilder for concatenation
- Find index of substring
- Replace characters
- Split strings

---

### Exercise 5: If-Else Statements
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Conditional statements, if-else

**Problem:**
Write a program that checks if a number is positive, negative, or zero.

**Starter Code:**
```java
public class IfElse {
    public static void main(String[] args) {
        int number = 10;
        
        // TODO: Check if positive, negative, or zero
        // TODO: Print result
    }
}
```

**Solution:**
```java
public class IfElse {
    public static void main(String[] args) {
        int number = 10;
        
        if (number > 0) {
            System.out.println("Positive");
        } else if (number < 0) {
            System.out.println("Negative");
        } else {
            System.out.println("Zero");
        }
    }
}
```

**Variations:**
- Check if even or odd
- Check if in range
- Use ternary operator

---

### Exercise 6: Switch Statement
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Switch statement, case statements

**Problem:**
Write a program that uses a switch statement to print the day of the week based on a number (1-7).

**Starter Code:**
```java
public class SwitchDay {
    public static void main(String[] args) {
        int day = 3;
        
        // TODO: Use switch to print day name
    }
}
```

**Solution:**
```java
public class SwitchDay {
    public static void main(String[] args) {
        int day = 3;
        
        switch (day) {
            case 1:
                System.out.println("Monday");
                break;
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");
                break;
            case 4:
                System.out.println("Thursday");
                break;
            case 5:
                System.out.println("Friday");
                break;
            case 6:
                System.out.println("Saturday");
                break;
            case 7:
                System.out.println("Sunday");
                break;
            default:
                System.out.println("Invalid day");
        }
    }
}
```

**Variations:**
- Use enhanced switch (Java 14+)
- Add multiple cases per label
- Use switch with strings

---

### Exercise 7: For Loop
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Loops, for loop

**Problem:**
Write a program that prints numbers from 1 to 10 using a for loop.

**Starter Code:**
```java
public class ForLoop {
    public static void main(String[] args) {
        // TODO: Print numbers 1 to 10
    }
}
```

**Solution:**
```java
public class ForLoop {
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println(i);
        }
    }
}
```

**Variations:**
- Print in reverse order
- Print only even numbers
- Print with custom increment
- Use nested loops

---

### Exercise 8: While Loop
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Loops, while loop

**Problem:**
Write a program that prints numbers from 1 to 10 using a while loop.

**Starter Code:**
```java
public class WhileLoop {
    public static void main(String[] args) {
        // TODO: Print numbers 1 to 10 using while loop
    }
}
```

**Solution:**
```java
public class WhileLoop {
    public static void main(String[] args) {
        int i = 1;
        while (i <= 10) {
            System.out.println(i);
            i++;
        }
    }
}
```

**Variations:**
- Use do-while loop
- Print in reverse
- Use break statement
- Use continue statement

---

### Exercise 9: Arrays
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Arrays, array operations

**Problem:**
Write a program that creates an array of integers, prints all elements, and calculates the sum.

**Starter Code:**
```java
public class Arrays {
    public static void main(String[] args) {
        int[] numbers = {10, 20, 30, 40, 50};
        
        // TODO: Print all elements
        // TODO: Calculate and print sum
    }
}
```

**Solution:**
```java
public class Arrays {
    public static void main(String[] args) {
        int[] numbers = {10, 20, 30, 40, 50};
        
        System.out.println("Elements:");
        for (int num : numbers) {
            System.out.println(num);
        }
        
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        System.out.println("Sum: " + sum);
    }
}
```

**Variations:**
- Find maximum/minimum
- Calculate average
- Reverse array
- Search for element

---

### Exercise 10: Methods
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Methods, method parameters, return values

**Problem:**
Write a program with methods to add two numbers, multiply two numbers, and check if a number is even.

**Starter Code:**
```java
public class Methods {
    // TODO: Create add method
    // TODO: Create multiply method
    // TODO: Create isEven method
    
    public static void main(String[] args) {
        // TODO: Call methods and print results
    }
}
```

**Solution:**
```java
public class Methods {
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    public static boolean isEven(int num) {
        return num % 2 == 0;
    }
    
    public static void main(String[] args) {
        System.out.println("Add: " + add(5, 3));
        System.out.println("Multiply: " + multiply(5, 3));
        System.out.println("Is 4 even: " + isEven(4));
    }
}
```

**Variations:**
- Use varargs
- Use method overloading
- Use default parameters
- Return multiple values

---

## 🟡 Medium Exercises (11-20)

### Exercise 11: Method Overloading
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Method overloading, polymorphism

**Problem:**
Create a Calculator class with overloaded add methods for int, double, and three parameters.

**Solution:**
```java
public class Calculator {
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static double add(double a, double b) {
        return a + b;
    }
    
    public static int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public static void main(String[] args) {
        System.out.println("Int add: " + add(5, 3));
        System.out.println("Double add: " + add(5.5, 3.2));
        System.out.println("Three int add: " + add(5, 3, 2));
    }
}
```

---

### Exercise 12: Varargs
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Varargs, variable arguments

**Problem:**
Create a method that accepts variable number of arguments and returns their sum.

**Solution:**
```java
public class VarargSum {
    public static int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    public static void main(String[] args) {
        System.out.println("Sum of 1,2,3: " + sum(1, 2, 3));
        System.out.println("Sum of 1,2,3,4,5: " + sum(1, 2, 3, 4, 5));
    }
}
```

---

### Exercise 13: String Immutability
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** String immutability, StringBuilder

**Problem:**
Demonstrate string immutability and show how StringBuilder is more efficient for string concatenation.

**Solution:**
```java
public class StringImmutability {
    public static void main(String[] args) {
        // String immutability
        String str = "Hello";
        str = str + " World";
        System.out.println(str);
        
        // StringBuilder for efficiency
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        System.out.println("StringBuilder length: " + sb.length());
    }
}
```

---

### Exercise 14: Type Casting
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Type casting, widening, narrowing

**Problem:**
Demonstrate widening and narrowing type casting with examples.

**Solution:**
```java
public class TypeCasting {
    public static void main(String[] args) {
        // Widening (automatic)
        int intValue = 100;
        double doubleValue = intValue;
        System.out.println("Widening: " + doubleValue);
        
        // Narrowing (explicit)
        double doubleValue2 = 100.5;
        int intValue2 = (int) doubleValue2;
        System.out.println("Narrowing: " + intValue2);
    }
}
```

---

### Exercise 15: 2D Arrays
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** 2D arrays, nested loops

**Problem:**
Create a 2D array (matrix) and print it in a formatted way.

**Solution:**
```java
public class Matrix {
    public static void main(String[] args) {
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
```

---

### Exercise 16: Fibonacci Sequence
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Loops, recursion basics

**Problem:**
Write a program to print the first 10 Fibonacci numbers.

**Solution:**
```java
public class Fibonacci {
    public static void main(String[] args) {
        int a = 0, b = 1;
        System.out.print(a + " " + b);
        
        for (int i = 2; i < 10; i++) {
            int c = a + b;
            System.out.print(" " + c);
            a = b;
            b = c;
        }
    }
}
```

---

### Exercise 17: Prime Number Checker
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Loops, conditionals, methods

**Problem:**
Write a method to check if a number is prime.

**Solution:**
```java
public class PrimeChecker {
    public static boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num <= 3) return true;
        if (num % 2 == 0 || num % 3 == 0) return false;
        
        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        System.out.println("Is 17 prime: " + isPrime(17));
        System.out.println("Is 20 prime: " + isPrime(20));
    }
}
```

---

### Exercise 18: Palindrome Checker
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** String operations, loops

**Problem:**
Write a method to check if a string is a palindrome.

**Solution:**
```java
public class PalindromeChecker {
    public static boolean isPalindrome(String str) {
        String cleaned = str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        int left = 0, right = cleaned.length() - 1;
        
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    public static void main(String[] args) {
        System.out.println("Is 'racecar' palindrome: " + isPalindrome("racecar"));
        System.out.println("Is 'hello' palindrome: " + isPalindrome("hello"));
    }
}
```

---

### Exercise 19: Factorial Calculator
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Loops, recursion

**Problem:**
Write both iterative and recursive methods to calculate factorial.

**Solution:**
```java
public class Factorial {
    public static int factorialIterative(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    public static int factorialRecursive(int n) {
        if (n <= 1) return 1;
        return n * factorialRecursive(n - 1);
    }
    
    public static void main(String[] args) {
        System.out.println("5! (iterative): " + factorialIterative(5));
        System.out.println("5! (recursive): " + factorialRecursive(5));
    }
}
```

---

### Exercise 20: Array Sorting
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Arrays, sorting, algorithms

**Problem:**
Implement bubble sort and use built-in sort to sort an array.

**Solution:**
```java
import java.util.Arrays;

public class ArraySorting {
    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        
        bubbleSort(arr);
        System.out.println("Bubble sort: " + Arrays.toString(arr));
        
        int[] arr2 = {64, 34, 25, 12, 22, 11, 90};
        Arrays.sort(arr2);
        System.out.println("Built-in sort: " + Arrays.toString(arr2));
    }
}
```

---

## 🔴 Hard Exercises (21-25)

### Exercise 21: Number Guessing Game
**Difficulty:** Hard  
**Time:** 30 minutes  
**Topics:** Loops, conditionals, user input

**Problem:**
Create a number guessing game where the user has to guess a random number.

**Solution:**
```java
import java.util.Scanner;
import java.util.Random;

public class GuessingGame {
    public static void main(String[] args) {
        Random random = new Random();
        int secretNumber = random.nextInt(100) + 1;
        int guess = 0;
        int attempts = 0;
        
        Scanner scanner = new Scanner(System.in);
        
        while (guess != secretNumber) {
            System.out.print("Guess a number (1-100): ");
            guess = scanner.nextInt();
            attempts++;
            
            if (guess < secretNumber) {
                System.out.println("Too low!");
            } else if (guess > secretNumber) {
                System.out.println("Too high!");
            } else {
                System.out.println("Correct! You guessed in " + attempts + " attempts.");
            }
        }
        
        scanner.close();
    }
}
```

---

### Exercise 22: Matrix Operations
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** 2D arrays, nested loops, algorithms

**Problem:**
Implement matrix addition and multiplication.

**Solution:**
```java
public class MatrixOperations {
    public static int[][] addMatrices(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    
    public static int[][] multiplyMatrices(int[][] a, int[][] b) {
        int[][] result = new int[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < b.length; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
    
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        
        System.out.println("Addition:");
        printMatrix(addMatrices(a, b));
        
        System.out.println("Multiplication:");
        printMatrix(multiplyMatrices(a, b));
    }
}
```

---

### Exercise 23: String Manipulation
**Difficulty:** Hard  
**Time:** 30 minutes  
**Topics:** String operations, algorithms

**Problem:**
Implement methods to reverse a string, count character frequency, and find longest word.

**Solution:**
```java
public class StringManipulation {
    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
    
    public static void countCharFrequency(String str) {
        java.util.Map<Character, Integer> frequency = new java.util.HashMap<>();
        for (char c : str.toCharArray()) {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }
        System.out.println(frequency);
    }
    
    public static String findLongestWord(String str) {
        String[] words = str.split(" ");
        String longest = "";
        for (String word : words) {
            if (word.length() > longest.length()) {
                longest = word;
            }
        }
        return longest;
    }
    
    public static void main(String[] args) {
        String text = "Hello World Java Programming";
        
        System.out.println("Reversed: " + reverseString("Hello"));
        System.out.print("Frequency: ");
        countCharFrequency("hello");
        System.out.println("Longest word: " + findLongestWord(text));
    }
}
```

---

### Exercise 24: Number System Conversion
**Difficulty:** Hard  
**Time:** 30 minutes  
**Topics:** Number systems, algorithms

**Problem:**
Convert numbers between decimal, binary, octal, and hexadecimal.

**Solution:**
```java
public class NumberConversion {
    public static String decimalToBinary(int num) {
        return Integer.toBinaryString(num);
    }
    
    public static String decimalToOctal(int num) {
        return Integer.toOctalString(num);
    }
    
    public static String decimalToHex(int num) {
        return Integer.toHexString(num);
    }
    
    public static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }
    
    public static void main(String[] args) {
        int num = 255;
        
        System.out.println("Decimal: " + num);
        System.out.println("Binary: " + decimalToBinary(num));
        System.out.println("Octal: " + decimalToOctal(num));
        System.out.println("Hex: " + decimalToHex(num));
        System.out.println("Binary to Decimal: " + binaryToDecimal("11111111"));
    }
}
```

---

### Exercise 25: Anagram Checker
**Difficulty:** Hard  
**Time:** 30 minutes  
**Topics:** String operations, sorting, algorithms

**Problem:**
Check if two strings are anagrams of each other.

**Solution:**
```java
import java.util.Arrays;

public class AnagramChecker {
    public static boolean areAnagrams(String str1, String str2) {
        String s1 = str1.replaceAll(" ", "").toLowerCase();
        String s2 = str2.replaceAll(" ", "").toLowerCase();
        
        if (s1.length() != s2.length()) {
            return false;
        }
        
        char[] arr1 = s1.toCharArray();
        char[] arr2 = s2.toCharArray();
        
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        
        return Arrays.equals(arr1, arr2);
    }
    
    public static void main(String[] args) {
        System.out.println("'listen' and 'silent': " + areAnagrams("listen", "silent"));
        System.out.println("'hello' and 'world': " + areAnagrams("hello", "world"));
    }
}
```

---

## 🎯 Interview Exercises (26-30)

### Exercise 26: FizzBuzz
**Difficulty:** Interview  
**Time:** 15 minutes  
**Topics:** Loops, conditionals

**Problem:**
Print numbers 1 to 100, but for multiples of 3 print "Fizz", for multiples of 5 print "Buzz", and for multiples of both print "FizzBuzz".

**Solution:**
```java
public class FizzBuzz {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            if (i % 15 == 0) {
                System.out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");
            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);
            }
        }
    }
}
```

---

### Exercise 27: Two Sum Problem
**Difficulty:** Interview  
**Time:** 20 minutes  
**Topics:** Arrays, algorithms, hash maps

**Problem:**
Given an array of integers and a target sum, find two numbers that add up to the target.

**Solution:**
```java
import java.util.HashMap;
import java.util.Map;

public class TwoSum {
    public static int[] findTwoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        
        return new int[]{};
    }
    
    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = findTwoSum(nums, target);
        System.out.println("Indices: " + result[0] + ", " + result[1]);
    }
}
```

---

### Exercise 28: Reverse Integer
**Difficulty:** Interview  
**Time:** 20 minutes  
**Topics:** Math, string operations

**Problem:**
Reverse an integer without using string conversion.

**Solution:**
```java
public class ReverseInteger {
    public static int reverse(int num) {
        int reversed = 0;
        int original = num;
        
        while (num != 0) {
            int digit = num % 10;
            reversed = reversed * 10 + digit;
            num /= 10;
        }
        
        return reversed;
    }
    
    public static void main(String[] args) {
        System.out.println("Reverse of 12345: " + reverse(12345));
        System.out.println("Reverse of -123: " + reverse(-123));
    }
}
```

---

### Exercise 29: Longest Substring Without Repeating Characters
**Difficulty:** Interview  
**Time:** 25 minutes  
**Topics:** String operations, sliding window

**Problem:**
Find the length of the longest substring without repeating characters.

**Solution:**
```java
import java.util.HashMap;
import java.util.Map;

public class LongestSubstring {
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> charIndex = new HashMap<>();
        int maxLength = 0;
        int start = 0;
        
        for (int end = 0; end < s.length(); end++) {
            char c = s.charAt(end);
            
            if (charIndex.containsKey(c)) {
                start = Math.max(start, charIndex.get(c) + 1);
            }
            
            charIndex.put(c, end);
            maxLength = Math.max(maxLength, end - start + 1);
        }
        
        return maxLength;
    }
    
    public static void main(String[] args) {
        System.out.println("Length for 'abcabcbb': " + lengthOfLongestSubstring("abcabcbb"));
        System.out.println("Length for 'bbbbb': " + lengthOfLongestSubstring("bbbbb"));
        System.out.println("Length for 'pwwkew': " + lengthOfLongestSubstring("pwwkew"));
    }
}
```

---

### Exercise 30: Median of Two Sorted Arrays
**Difficulty:** Interview  
**Time:** 30 minutes  
**Topics:** Arrays, algorithms, binary search

**Problem:**
Find the median of two sorted arrays.

**Solution:**
```java
public class MedianSortedArrays {
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }
        
        int m = nums1.length;
        int n = nums2.length;
        int low = 0, high = m;
        
        while (low <= high) {
            int cut1 = (low + high) / 2;
            int cut2 = (m + n + 1) / 2 - cut1;
            
            int left1 = cut1 == 0 ? Integer.MIN_VALUE : nums1[cut1 - 1];
            int left2 = cut2 == 0 ? Integer.MIN_VALUE : nums2[cut2 - 1];
            int right1 = cut1 == m ? Integer.MAX_VALUE : nums1[cut1];
            int right2 = cut2 == n ? Integer.MAX_VALUE : nums2[cut2];
            
            if (left1 <= right2 && left2 <= right1) {
                if ((m + n) % 2 == 0) {
                    return (Math.max(left1, left2) + Math.min(right1, right2)) / 2.0;
                } else {
                    return Math.max(left1, left2);
                }
            } else if (left1 > right2) {
                high = cut1 - 1;
            } else {
                low = cut1 + 1;
            }
        }
        
        return -1;
    }
    
    public static void main(String[] args) {
        int[] nums1 = {1, 3};
        int[] nums2 = {2};
        System.out.println("Median: " + findMedianSortedArrays(nums1, nums2));
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Hello World | Easy | 5 min | Basic syntax |
| 2 | Variables and Data Types | Easy | 10 min | Variables, types |
| 3 | Arithmetic Operations | Easy | 10 min | Operators |
| 4 | String Operations | Easy | 15 min | Strings |
| 5 | If-Else Statements | Easy | 15 min | Conditionals |
| 6 | Switch Statement | Easy | 15 min | Switch |
| 7 | For Loop | Easy | 15 min | Loops |
| 8 | While Loop | Easy | 15 min | Loops |
| 9 | Arrays | Easy | 15 min | Arrays |
| 10 | Methods | Easy | 20 min | Methods |
| 11 | Method Overloading | Medium | 20 min | Overloading |
| 12 | Varargs | Medium | 20 min | Varargs |
| 13 | String Immutability | Medium | 20 min | Strings |
| 14 | Type Casting | Medium | 20 min | Casting |
| 15 | 2D Arrays | Medium | 25 min | Arrays |
| 16 | Fibonacci Sequence | Medium | 25 min | Loops |
| 17 | Prime Number Checker | Medium | 25 min | Algorithms |
| 18 | Palindrome Checker | Medium | 25 min | Strings |
| 19 | Factorial Calculator | Medium | 20 min | Recursion |
| 20 | Array Sorting | Medium | 25 min | Sorting |
| 21 | Number Guessing Game | Hard | 30 min | Game logic |
| 22 | Matrix Operations | Hard | 35 min | 2D arrays |
| 23 | String Manipulation | Hard | 30 min | Strings |
| 24 | Number System Conversion | Hard | 30 min | Number systems |
| 25 | Anagram Checker | Hard | 30 min | Algorithms |
| 26 | FizzBuzz | Interview | 15 min | Logic |
| 27 | Two Sum Problem | Interview | 20 min | Hash maps |
| 28 | Reverse Integer | Interview | 20 min | Math |
| 29 | Longest Substring | Interview | 25 min | Sliding window |
| 30 | Median of Two Arrays | Interview | 30 min | Binary search |

---

## 🎓 How to Use These Exercises

### For Learning
1. Read the problem statement
2. Try to solve it yourself first
3. Check the solution
4. Understand the approach
5. Try variations

### For Practice
1. Solve without looking at solution
2. Test your code
3. Compare with provided solution
4. Optimize if possible
5. Move to next exercise

### For Interviews
1. Practice under time pressure
2. Explain your approach
3. Write clean code
4. Test edge cases
5. Optimize for time/space

---

<div align="center">

## Exercises: Java Basics

**30 Comprehensive Exercises**

**Easy (10) | Medium (10) | Hard (5) | Interview (5)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>