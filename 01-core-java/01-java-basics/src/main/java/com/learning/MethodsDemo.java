package com.learning;

/**
 * Demonstrates Java method concepts including parameters, return types, varargs,
 * and method overloading. This class covers fundamental and advanced method patterns.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class MethodsDemo {
    
    /**
     * Demonstrates various aspects of Java methods.
     */
    public static void demonstrateMethods() {
        System.out.println("\n--- METHOD FUNDAMENTALS ---");
        demonstrateMethodBasics();
        
        System.out.println("\n--- PARAMETER PASSING ---");
        demonstrateParameterPassing();
        
        System.out.println("\n--- METHOD OVERLOADING ---");
        demonstrateMethodOverloading();
        
        System.out.println("\n--- VARARGS (VARIABLE ARGUMENTS) ---");
        demonstrateVarargs();
        
        System.out.println("\n--- RETURN TYPES ---");
        demonstrateReturnTypes();
        
        System.out.println("\n--- PASS BY VALUE ---");
        demonstratePassByValue();
    }
    
    /**
     * Demonstrates basic method structure and calling.
     */
    private static void demonstrateMethodBasics() {
        // Method with no parameters or return value
        printWelcomeMessage();
        
        // Method with parameters and return value
        int result = add(5, 10);
        System.out.println("5 + 10 = " + result);
        
        // Method with parameters but no return value
        printNTimes("Hello", 3);
    }
    
    /**
     * Demonstrates parameter passing: primitive vs reference types.
     */
    private static void demonstrateParameterPassing() {
        // Primitive type - pass by value
        int num = 100;
        System.out.println("Before method: num = " + num);
        modifyPrimitive(num);
        System.out.println("After method: num = " + num);  // Still 100
        
        // Reference type - pass by reference value
        int[] array = {1, 2, 3};
        System.out.println("Before method: array[1] = " + array[1]);
        modifyArray(array);
        System.out.println("After method: array[1] = " + array[1]);  // Changed to 99
        
        // String - immutable reference type
        String str = "Original";
        System.out.println("Before method: str = " + str);
        modifyString(str);
        System.out.println("After method: str = " + str);  // Still "Original"
    }
    
    /**
     * Demonstrates method overloading - same method name, different parameters.
     */
    private static void demonstrateMethodOverloading() {
        // Overloading by number of parameters
        System.out.println("add(5, 10) = " + add(5, 10));
        System.out.println("add(5, 10, 15) = " + add(5, 10, 15));
        
        // Overloading by parameter type
        System.out.println("multiply(5, 3.5) = " + multiply(5, 3.5));
        System.out.println("multiply(5.5, 3.5) = " + multiply(5.5, 3.5));
        
        // Overloading by parameter order
        System.out.println("printValues(5, \"Number\") = ");
        printValues(5, "Number");
        System.out.println("printValues(\"Value:\", 42) = ");
        printValues("Value:", 42);
    }
    
    /**
     * Demonstrates varargs (variable number of arguments).
     */
    private static void demonstrateVarargs() {
        // Varargs allows flexible number of arguments
        System.out.println("sum(10, 20, 30) = " + sum(10, 20, 30));
        System.out.println("sum(5) = " + sum(5));
        System.out.println("sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) = " + sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        
        // Varargs with mixed parameters
        System.out.println("concatenate(\"Hello\", \"Java\", \"Varargs\") = " + 
                         concatenate("Hello", "Java", "Varargs"));
    }
    
    /**
     * Demonstrates different return types and return statements.
     */
    private static void demonstrateReturnTypes() {
        // Return primitive type
        int number = getPositiveNumber(5);
        System.out.println("Positive number: " + number);
        
        // Return reference type
        String message = getGreeting("Alice");
        System.out.println("Greeting: " + message);
        
        // Return boolean
        boolean isPrime = isPrime(17);
        System.out.println("Is 17 prime? " + isPrime);
        
        // Early return statement
        displayMessage(0);
        displayMessage(5);
    }
    
    /**
     * Demonstrates pass by value for primitives.
     */
    private static void demonstratePassByValue() {
        int value = 42;
        System.out.println("Original value: " + value);
        
        // This modifies a copy, not the original
        incrementValue(value);
        System.out.println("After incrementValue: " + value);
        System.out.println("(Note: value is unchanged because primitives are passed by value)");
    }
    
    // ===== HELPER METHODS =====
    
    public static void printWelcomeMessage() {
        System.out.println("Welcome to Methods Demo!");
    }
    
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public static double multiply(int a, double b) {
        return a * b;
    }
    
    public static double multiply(double a, double b) {
        return a * b;
    }
    
    public static void printNTimes(String message, int times) {
        for (int i = 0; i < times; i++) {
            System.out.println(message + " (" + (i + 1) + ")");
        }
    }
    
    public static void modifyPrimitive(int num) {
        num = num * 2;  // Only modifies the copy
    }
    
    public static void modifyArray(int[] array) {
        array[1] = 99;  // Modifies the actual array
    }
    
    public static void modifyString(String str) {
        str = "Modified";  // Only modifies the local reference
    }
    
    public static int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    public static String concatenate(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static void printValues(int num, String text) {
        System.out.println("  int: " + num + ", String: " + text);
    }
    
    public static void printValues(String text, int num) {
        System.out.println("  String: " + text + ", int: " + num);
    }
    
    public static int getPositiveNumber(int num) {
        return num > 0 ? num : 0;
    }
    
    public static String getGreeting(String name) {
        return "Hello, " + name + "!";
    }
    
    public static boolean isPrime(int num) {
        if (num < 2) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;
        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) return false;
        }
        return true;
    }
    
    public static void displayMessage(int count) {
        if (count == 0) {
            System.out.println("Early return: count is zero");
            return;
        }
        System.out.println("Count is: " + count);
    }
    
    public static void incrementValue(int value) {
        value++;  // Only increments the copy
    }
    
    public static void reassignArray(int[] array) {
        array = new int[10];  // Only modifies the local reference
    }
}
