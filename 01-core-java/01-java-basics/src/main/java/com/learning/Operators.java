package com.learning;

/**
 * Demonstrates all Java operators including arithmetic, relational, logical,
 * bitwise, assignment, and special operators.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Operators {
    
    /**
     * Demonstrates all types of operators in Java.
     */
    public static void demonstrateOperators() {
        demonstrateArithmeticOperators();
        demonstrateRelationalOperators();
        demonstrateLogicalOperators();
        demonstrateBitwiseOperators();
        demonstrateAssignmentOperators();
        demonstrateSpecialOperators();
    }
    
    /**
     * Demonstrates arithmetic operators (+, -, *, /, %, ++, --).
     */
    private static void demonstrateArithmeticOperators() {
        System.out.println("Arithmetic Operators:");
        
        int a = 10, b = 3;
        
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("Addition (a + b): " + (a + b));
        System.out.println("Subtraction (a - b): " + (a - b));
        System.out.println("Multiplication (a * b): " + (a * b));
        System.out.println("Division (a / b): " + (a / b));
        System.out.println("Modulus (a % b): " + (a % b));
        
        // Increment and Decrement
        int x = 5;
        System.out.println("x = " + x);
        System.out.println("Post-increment (x++): " + (x++)); // prints 5, then x becomes 6
        System.out.println("After post-increment, x = " + x);
        System.out.println("Pre-increment (++x): " + (++x)); // x becomes 7, then prints 7
        
        int y = 5;
        System.out.println("y = " + y);
        System.out.println("Post-decrement (y--): " + (y--)); // prints 5, then y becomes 4
        System.out.println("After post-decrement, y = " + y);
        System.out.println("Pre-decrement (--y): " + (--y)); // y becomes 3, then prints 3
    }
    
    /**
     * Demonstrates relational operators (==, !=, >, <, >=, <=).
     */
    private static void demonstrateRelationalOperators() {
        System.out.println("\nRelational Operators:");
        
        int a = 10, b = 20;
        
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("Equal to (a == b): " + (a == b));
        System.out.println("Not equal to (a != b): " + (a != b));
        System.out.println("Greater than (a > b): " + (a > b));
        System.out.println("Less than (a < b): " + (a < b));
        System.out.println("Greater than or equal to (a >= b): " + (a >= b));
        System.out.println("Less than or equal to (a <= b): " + (a <= b));
    }
    
    /**
     * Demonstrates logical operators (&&, ||, !).
     */
    private static void demonstrateLogicalOperators() {
        System.out.println("\nLogical Operators:");
        
        boolean x = true, y = false;
        
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("Logical AND (x && y): " + (x && y));
        System.out.println("Logical OR (x || y): " + (x || y));
        System.out.println("Logical NOT (!x): " + (!x));
        System.out.println("Logical NOT (!y): " + (!y));
        
        // Short-circuit evaluation
        int a = 10, b = 0;
        System.out.println("\nShort-circuit evaluation:");
        System.out.println("(a > 5) && (b > 0): " + ((a > 5) && (b > 0))); // false
        System.out.println("(a > 5) || (b > 0): " + ((a > 5) || (b > 0))); // true
    }
    
    /**
     * Demonstrates bitwise operators (&, |, ^, ~, <<, >>, >>>).
     */
    private static void demonstrateBitwiseOperators() {
        System.out.println("\nBitwise Operators:");
        
        int a = 5;  // 0101 in binary
        int b = 3;  // 0011 in binary
        
        System.out.println("a = " + a + " (binary: " + Integer.toBinaryString(a) + ")");
        System.out.println("b = " + b + " (binary: " + Integer.toBinaryString(b) + ")");
        
        System.out.println("Bitwise AND (a & b): " + (a & b) + " (binary: " + Integer.toBinaryString(a & b) + ")");
        System.out.println("Bitwise OR (a | b): " + (a | b) + " (binary: " + Integer.toBinaryString(a | b) + ")");
        System.out.println("Bitwise XOR (a ^ b): " + (a ^ b) + " (binary: " + Integer.toBinaryString(a ^ b) + ")");
        System.out.println("Bitwise NOT (~a): " + (~a) + " (binary: " + Integer.toBinaryString(~a) + ")");
        
        // Shift operators
        System.out.println("Left shift (a << 1): " + (a << 1) + " (binary: " + Integer.toBinaryString(a << 1) + ")");
        System.out.println("Right shift (a >> 1): " + (a >> 1) + " (binary: " + Integer.toBinaryString(a >> 1) + ")");
        System.out.println("Unsigned right shift (a >>> 1): " + (a >>> 1) + " (binary: " + Integer.toBinaryString(a >>> 1) + ")");
    }
    
    /**
     * Demonstrates assignment operators (=, +=, -=, *=, /=, %=, &=, |=, ^=, <<=, >>=, >>>=).
     */
    private static void demonstrateAssignmentOperators() {
        System.out.println("\nAssignment Operators:");
        
        int x = 10;
        System.out.println("Initial x = " + x);
        
        x += 5; // x = x + 5
        System.out.println("After x += 5: " + x);
        
        x -= 3; // x = x - 3
        System.out.println("After x -= 3: " + x);
        
        x *= 2; // x = x * 2
        System.out.println("After x *= 2: " + x);
        
        x /= 4; // x = x / 4
        System.out.println("After x /= 4: " + x);
        
        x %= 3; // x = x % 3
        System.out.println("After x %= 3: " + x);
    }
    
    /**
     * Demonstrates special operators (ternary, instanceof).
     */
    private static void demonstrateSpecialOperators() {
        System.out.println("\nSpecial Operators:");
        
        // Ternary operator
        int a = 10, b = 20;
        int max = (a > b) ? a : b;
        System.out.println("Ternary operator (a > b ? a : b): " + max);
        
        // instanceof operator
        String str = "Hello";
        System.out.println("str instanceof String: " + (str instanceof String));
        System.out.println("str instanceof Object: " + (str instanceof Object));
    }
    
    /**
     * Calculates using arithmetic operators.
     * 
     * @param a first operand
     * @param b second operand
     * @param operator the operator (+, -, *, /, %)
     * @return the result
     */
    public static double calculate(double a, double b, char operator) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> b != 0 ? a / b : Double.NaN;
            case '%' -> b != 0 ? a % b : Double.NaN;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
    
    /**
     * Evaluates a logical expression.
     * 
     * @param a first boolean
     * @param b second boolean
     * @param operator the operator (AND, OR, NOT)
     * @return the result
     */
    public static boolean evaluateLogical(boolean a, boolean b, String operator) {
        return switch (operator.toUpperCase()) {
            case "AND" -> a && b;
            case "OR" -> a || b;
            case "NOT" -> !a;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}