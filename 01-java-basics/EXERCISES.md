# Java Basics - Exercises

## Exercise Set 1: Variables and Data Types

### Exercise 1.1: Variable Declaration
Create a program that declares variables of all primitive types and prints their default and initialized values.

```java
public class Exercise1_1 {
    public static void main(String[] args) {
        // Declare variables of each primitive type
        // Initialize them with appropriate values
        // Print each variable with its type
        
        // Your code here
    }
}
```

**Solution:**
```java
public class Exercise1_1 {
    public static void main(String[] args) {
        byte b = 127;
        short s = 32000;
        int i = 2147483647;
        long l = 9223372036854775807L;
        float f = 3.14f;
        double d = 3.14159265359;
        char c = 'A';
        boolean bool = true;
        
        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double: " + d);
        System.out.println("char: " + c);
        System.out.println("boolean: " + bool);
    }
}
```

### Exercise 1.2: Type Conversion
Write a program that demonstrates implicit and explicit type conversions between int, double, and float.

### Exercise 1.3: String Operations
Create a program that performs various String operations:
- Concatenation
- Length calculation
- Substring extraction
- Character access
- Case conversion

---

## Exercise Set 2: Control Flow

### Exercise 2.1: Grade Calculator
Write a program that takes a numeric score and outputs the corresponding letter grade.

| Score Range | Grade |
|-------------|-------|
| 90-100 | A |
| 80-89 | B |
| 70-79 | C |
| 60-69 | D |
| 0-59 | F |

```java
public class GradeCalculator {
    public static char calculateGrade(int score) {
        // Your implementation
        return 'F';
    }
    
    public static void main(String[] args) {
        int[] scores = {95, 82, 73, 65, 45, 100};
        for (int score : scores) {
            System.out.println("Score " + score + " -> Grade " + calculateGrade(score));
        }
    }
}
```

**Solution:**
```java
public class GradeCalculator {
    public static char calculateGrade(int score) {
        if (score >= 90 && score <= 100) {
            return 'A';
        } else if (score >= 80) {
            return 'B';
        } else if (score >= 70) {
            return 'C';
        } else if (score >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }
}
```

### Exercise 2.2: FizzBuzz
Print numbers from 1 to 100, but:
- For multiples of 3, print "Fizz"
- For multiples of 5, print "Buzz"
- For multiples of both, print "FizzBuzz"

### Exercise 2.3: Number Patterns
Create the following patterns using nested loops:

Pattern 1:
```
*
**
***
****
*****
```

Pattern 2:
```
*****
****
***
**
*
```

Pattern 3:
```
    *
   **
  ***
 ****
*****
```

### Exercise 2.4: Prime Numbers
Write a program that:
1. Finds all prime numbers between 2 and 100
2. Calculates if a user-provided number is prime

```java
public class PrimeChecker {
    public static boolean isPrime(int n) {
        // Your implementation
        return false;
    }
    
    public static void printPrimes(int limit) {
        // Your implementation
    }
}
```

---

## Exercise Set 3: Methods

### Exercise 3.1: Calculator
Create a simple calculator with methods for:
- add(int a, int b)
- subtract(int a, int b)
- multiply(int a, int b)
- divide(int a, int b) - handle division by zero
- modulus(int a, int b)

```java
public class Calculator {
    public static int add(int a, int b) { return a + b; }
    public static int subtract(int a, int b) { return a - b; }
    public static int multiply(int a, int b) { return a * b; }
    public static double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return (double) a / b;
    }
    public static int modulus(int a, int b) { return a % b; }
}
```

### Exercise 3.2: Recursive Methods
Implement the following recursively:
1. Calculate factorial of a number
2. Calculate the nth Fibonacci number
3. Calculate the sum of digits of a number
4. Reverse a string

```java
public class RecursiveExercises {
    
    public static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
    
    public static int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    public static int sumOfDigits(int n) {
        if (n == 0) return 0;
        return (n % 10) + sumOfDigits(n / 10);
    }
    
    public static String reverseString(String s) {
        if (s.isEmpty()) return s;
        return s.charAt(s.length() - 1) + reverseString(s.substring(0, s.length() - 1));
    }
}
```

### Exercise 3.3: Method Overloading
Create a class with overloaded methods for calculating area:
- area(double radius) - circle
- area(double length, double width) - rectangle
- area(int side) - square

---

## Exercise Set 4: Arrays

### Exercise 4.1: Array Operations
Write methods for:
1. Find the maximum and minimum values
2. Calculate the average
3. Find the second largest element
4. Check if array is sorted

```java
public class ArrayOperations {
    
    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int num : arr) {
            if (num > max) max = num;
        }
        return max;
    }
    
    public static double findAverage(int[] arr) {
        int sum = 0;
        for (int num : arr) sum += num;
        return (double) sum / arr.length;
    }
    
    public static int findSecondLargest(int[] arr) {
        int max = findMax(arr);
        int secondMax = Integer.MIN_VALUE;
        for (int num : arr) {
            if (num != max && num > secondMax) {
                secondMax = num;
            }
        }
        return secondMax;
    }
    
    public static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }
}
```

### Exercise 4.2: Array Sorting
Implement the following sorting algorithms:
1. Bubble Sort
2. Selection Sort
3. Insertion Sort

### Exercise 4.3: Two Sum Problem
Given an array and a target, find two numbers that add up to the target. Return their indices.

```java
public class TwoSum {
    public static int[] findTwoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }
}
```

### Exercise 4.4: Matrix Operations
Create a program that:
1. Adds two matrices
2. Multiplies two matrices
3. Finds the transpose of a matrix
4. Finds the sum of diagonal elements

---

## Exercise Set 5: Basic OOP

### Exercise 5.1: Bank Account Class
Create a BankAccount class with:
- accountNumber, holderName, balance
- Constructor
- deposit() and withdraw() methods
- getBalance() method
- withdraw() should not allow negative balance

```java
public class BankAccount {
    private String accountNumber;
    private String holderName;
    private double balance;
    
    public BankAccount(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
    
    @Override
    public String toString() {
        return String.format("Account[%s] %s: $%.2f", accountNumber, holderName, balance);
    }
}
```

### Exercise 5.2: Student Class
Create a Student class with:
- name, id, grades[]
- addGrade(double grade)
- getAverage()
- getHighest()
- getLowest()
- displayInfo()

### Exercise 5.3: Book Library
Create a simple library system with:
- Book class (title, author, isbn, available)
- Library class (books[], addBook, removeBook, findBook, displayBooks)

### Exercise 5.4: Inheritance Challenge
Extend the BankAccount class:
- SavingsAccount with interest rate
- CheckingAccount with overdraft limit

```java
public class SavingsAccount extends BankAccount {
    private double interestRate;
    
    public SavingsAccount(String accountNumber, String holderName, 
                          double initialBalance, double interestRate) {
        super(accountNumber, holderName, initialBalance);
        this.interestRate = interestRate;
    }
    
    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
    }
}

public class CheckingAccount extends BankAccount {
    private double overdraftLimit;
    private double overdraftUsed;
    
    public CheckingAccount(String accountNumber, String holderName,
                          double initialBalance, double overdraftLimit) {
        super(accountNumber, holderName, initialBalance);
        this.overdraftLimit = overdraftLimit;
        this.overdraftUsed = 0;
    }
    
    @Override
    public boolean withdraw(double amount) {
        if (amount > getBalance() + (overdraftLimit - overdraftUsed)) {
            return false;
        }
        if (amount > getBalance()) {
            overdraftUsed += (amount - getBalance());
            return super.withdraw(getBalance());
        }
        return super.withdraw(amount);
    }
}
```

---

## Challenge Problems

### Challenge 1: Palindrome Checker
Write a method that checks if a string is a palindrome (reads same forward and backward).

### Challenge 2: Array Rotation
Given an array and a number k, rotate the array to the right by k steps.

### Challenge 3: Anagram Checker
Write a method to check if two strings are anagrams of each other.

### Challenge 4: Rock-Paper-Scissors Game
Create a console-based game with:
- Player vs Computer
- Score tracking
- Multiple rounds

### Challenge 5: Temperature Converter
Create a comprehensive converter supporting:
- Celsius to Fahrenheit, Kelvin
- Fahrenheit to Celsius, Kelvin
- Kelvin to Celsius, Fahrenheit

---

## Practice Projects

### Project 1: Number Guessing Game
- Computer generates random number (1-100)
- User guesses with hints ("too high", "too low")
- Track number of attempts

### Project 2: ATM Simulator
- Multiple accounts
- Deposit, withdrawal, transfer
- Transaction history

### Project 3: Tic-Tac-Toe
- Two-player mode
- Win detection
- Board display
