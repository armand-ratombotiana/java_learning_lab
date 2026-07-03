# Java Syntax — Code Deep Dive

## Example 1: Complete Program Annotated

```java
package com.example.syntax;           // 1. Package declaration — organizes classes

import java.util.Scanner;             // 2. Import — brings in Scanner from java.util

/**
 * 3. Javadoc comment — generates documentation
 */
public class SyntaxDemo {             // 4. Class declaration — PascalCase

    // 5. Constant declaration — static final, UPPER_SNAKE_CASE
    private static final int MAX_ATTEMPTS = 3;

    // 6. Instance field — camelCase
    private String userName;

    // 7. Constructor — same name as class, no return type
    public SyntaxDemo(String userName) {
        this.userName = userName;     // 8. 'this' keyword — disambiguates field from param
    }

    // 9. Method declaration — camelCase, explicit return type
    public String greet(String greeting) {
        return greeting + ", " + userName + "!";  // 10. String concatenation
    }

    // 11. main method — entry point
    public static void main(String[] args) {      // 12. Varargs: String... args also valid
        SyntaxDemo demo = new SyntaxDemo("Alice"); // 13. Object creation with 'new'

        // 14. Local variable with type inference (Java 10+)
        var message = "Welcome";                  // 15. 'var' — compiler infers String

        // 16. Control flow with if-else
        if (args.length > 0) {                    // 17. Parameter access
            System.out.println(demo.greet(args[0]));
        } else {
            System.out.println(demo.greet(message));
        }

        // 18. Loop
        for (int i = 0; i < MAX_ATTEMPTS; i++) {  // 19. For loop with constant
            System.out.println("Attempt " + (i + 1));
        }

        // 20. Enhanced for-loop
        String[] names = {"Bob", "Carol"};        // 21. Array initializer
        for (String name : names) {               // 22. For-each syntax
            System.out.println(new SyntaxDemo(name).greet("Hi"));
        }
    }
}
```

## Example 2: Operator Showcase

```java
public class Operators {
    public static void main(String[] args) {
        // Arithmetic operators
        int sum = 10 + 5;        // 15 — addition
        int diff = 10 - 5;       // 5  — subtraction
        int product = 10 * 5;    // 50 — multiplication
        int quotient = 10 / 5;   // 2  — division
        int remainder = 10 % 3;  // 1  — modulo
        int neg = -10;           // -10 — unary minus

        // Increment/decrement
        int x = 5;
        int y = x++;            // y=5, x=6 (post-increment)
        int z = ++x;            // z=7, x=7 (pre-increment)

        // Relational operators
        boolean eq = (5 == 5);  // true  — equal to
        boolean ne = (5 != 3);  // true  — not equal
        boolean lt = (3 < 5);   // true  — less than
        boolean gt = (5 > 3);   // true  — greater than
        boolean le = (5 <= 5);  // true  — less than or equal
        boolean ge = (5 >= 5);  // true  — greater than or equal

        // Logical operators
        boolean and = true && false;   // false — logical AND (short-circuit)
        boolean or = true || false;    // true  — logical OR (short-circuit)
        boolean not = !true;           // false — logical NOT
        boolean xor = true ^ false;    // true  — logical XOR

        // Bitwise operators
        int a = 0b1100;      // 12
        int b = 0b1010;      // 10
        int bitAnd = a & b;  // 0b1000 = 8
        int bitOr  = a | b;  // 0b1110 = 14
        int bitXor = a ^ b;  // 0b0110 = 6
        int bitNot = ~a;     // 0b...0011 (inverted 32 bits)

        // Shift operators
        int leftShift  = 8 << 2;   // 32 = 8 * 2^2
        int rightShift = 8 >> 2;   // 2  = 8 / 2^2
        int unsignedRightShift = -8 >>> 2; // fills with zeros

        // Ternary operator
        int max = (10 > 5) ? 10 : 5;   // 10

        // Assignment operators
        int value = 10;    // simple assignment
        value += 5;        // value = 15 — compound assignment
        value -= 3;        // value = 12
        value *= 2;        // value = 24
        value /= 4;        // value = 6
        value %= 4;        // value = 2
    }
}
```

## Example 3: Type Conversion and Casting

```java
public class TypeConversion {
    public static void main(String[] args) {
        // Widening (implicit) — safe, no data loss
        int i = 100;
        long l = i;          // int → long
        float f = i;         // int → float
        double d = i;        // int → double

        // Narrowing (explicit cast) — may lose data
        long big = 1_000_000_000_000L;
        int small = (int) big;    // Data loss! Compiler requires cast
        System.out.println(small); // Different value due to truncation

        // Expression type promotion
        byte b1 = 10;
        byte b2 = 20;
        // byte sum = b1 + b2;   // Compilation error: int cannot be converted to byte
        int sum = b1 + b2;        // b1 and b2 promoted to int for addition

        // String concatenation
        String s = "Value: " + 42;     // "Value: 42" — int converted to String
        String s2 = 42 + " is the answer"; // "42 is the answer"
    }
}
```

## Example 4: All Comment Types

```java
// Single-line comment — explains the next line
int x = 10;  // Inline comment after a statement

/*
 * Multi-line comment.
 * Can span several lines.
 * Useful for temporarily disabling blocks.
 */
int y = 20;

/**
 * Javadoc comment — used by javadoc tool.
 * @param name the person to greet
 * @return a greeting string
 * @see java.lang.System
 */
public String greet(String name) {
    return "Hello, " + name;
}
```

## Example 5: Literal Formats

```java
public class Literals {
    public static void main(String[] args) {
        // Integer literals
        int dec = 42;              // decimal
        int hex = 0x2A;            // hexadecimal
        int oct = 052;             // octal (prefix 0)
        int bin = 0b101010;        // binary (prefix 0b)
        int und = 1_000_000;       // with underscores for readability

        // Floating-point literals
        double d1 = 3.14;          // standard
        double d2 = 3.14d;         // with d suffix
        float f1 = 3.14f;          // with f suffix
        double d3 = 1.5e10;        // scientific notation

        // Character literals
        char c1 = 'A';             // single character
        char c2 = '\n';            // escape sequence (newline)
        char c3 = '\u0041';        // Unicode escape (A)
        char c4 = 65;              // integer literal (A)

        // String literal
        String s = "Hello, World!";

        // Boolean and null
        boolean b = true;
        Object o = null;

        // Text block (Java 15+)
        String json = """
                {
                    "name": "Alice",
                    "age": 30
                }
                """;
    }
}
```
