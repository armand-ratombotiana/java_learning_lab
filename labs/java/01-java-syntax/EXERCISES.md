# Java Syntax — Exercises

## Exercise 1: Fix the Syntax
**Difficulty:** Easy

The following code has 5 syntax errors. Find and fix them:
```java
public class exercise1 {
    public static void main(String[] args)
        int x = 5
        String name = "Alice"
        System.out.println(Name)
    }
}
```

## Exercise 2: Variable Declaration
**Difficulty:** Easy

Declare variables of each primitive type (`byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`) with appropriate values and print them all.

## Exercise 3: Operator Practice
**Difficulty:** Easy

Write a program that takes two integers and prints the result of all arithmetic operators: `+`, `-`, `*`, `/`, `%`, and compound assignments.

## Exercise 4: Naming Convention Fix
**Difficulty:** Easy

Rename the following to follow Java conventions:
```java
public class employee_record {
    private static final int max_size = 100;
    private String EmployeeName;
    public void Process_Data() { }
}
```

## Exercise 5: Temperature Converter
**Difficulty:** Medium

Write a program that converts Celsius to Fahrenheit: `F = C * 9/5 + 32`. Use proper syntax, meaningful variable names, and print the result with explanatory text. Handle both integer and floating-point cases.

## Exercise 6: Comment That Code
**Difficulty:** Medium

Add all three types of Java comments (single-line, multi-line, Javadoc) to this method:
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

## Exercise 7: Number Classifier
**Difficulty:** Medium

Write a program that uses the ternary operator to classify a number as positive, negative, or zero. Print the result. Use `args[0]` input.

## Exercise 8: Literal Format Practice
**Difficulty:** Medium

Create variables using each literal format:
- Decimal, hexadecimal, octal, and binary for the value 50
- Floating-point with scientific notation
- Character with Unicode escape
- String with a text block

Print all values.

## Exercise 9: Mini-DSL Builder
**Difficulty:** Hard

Create a class that demonstrates: package declaration, imports, all comment types, constants, instance fields, constructors, methods with different access modifiers, and a main method that exercises everything. Follow all naming conventions.

## Exercise 10: Syntax Validator
**Difficulty:** Hard

Write a program that reads a `.java` file and reports:
- Number of lines, tokens, and characters
- Number of each type of comment
- Number of keywords found
- Number of statements (count `;`)
- Whether braces are balanced (hint: use a stack)

## Exercise 11: Number Base Converter
**Difficulty:** Hard

Write a program that takes an integer and prints it in decimal, binary (prefix `0b`), octal (prefix `0`), and hexadecimal (prefix `0x`). Use `Integer.toString(int, radix)` and format output properly.

## Exercise 12: Expression Evaluator
**Difficulty:** Hard

Write a program that evaluates a simple arithmetic expression from command-line args (e.g., `java Eval 3 + 4`). Handle `+`, `-`, `*`, `/`, `%` using the ternary operator or switch expression. Handle division by zero and invalid operators gracefully.
