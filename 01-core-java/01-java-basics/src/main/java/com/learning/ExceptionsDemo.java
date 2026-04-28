package com.learning;

/**
 * Demonstrates Java exception handling including try-catch-finally blocks,
 * exception hierarchies, custom exceptions, and best practices.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ExceptionsDemo {
    
    /**
     * Demonstrates various exception handling scenarios.
     */
    public static void demonstrateExceptions() {
        System.out.println("\n--- EXCEPTION HIERARCHY ---");
        demonstrateExceptionHierarchy();
        
        System.out.println("\n--- TRY-CATCH BLOCKS ---");
        demonstrateTryCatch();
        
        System.out.println("\n--- TRY-CATCH-FINALLY ---");
        demonstrateTryCatchFinally();
        
        System.out.println("\n--- MULTIPLE CATCH BLOCKS ---");
        demonstrateMultipleCatch();
        
        System.out.println("\n--- TRY-WITH-RESOURCES ---");
        demonstrateTryWithResources();
        
        System.out.println("\n--- CUSTOM EXCEPTIONS ---");
        demonstrateCustomExceptions();
        
        System.out.println("\n--- EXCEPTION PROPAGATION ---");
        demonstrateExceptionPropagation();
    }
    
    /**
     * Demonstrates Java exception hierarchy: Throwable > Exception/Error.
     */
    private static void demonstrateExceptionHierarchy() {
        System.out.println("Exception Hierarchy:");
        System.out.println("  Throwable");
        System.out.println("    ├── Error (system errors, not usually caught)");
        System.out.println("    │   ├── OutOfMemoryError");
        System.out.println("    │   └── StackOverflowError");
        System.out.println("    └── Exception (recoverable)");
        System.out.println("        ├── Checked (must handle)");
        System.out.println("        │   ├── IOException");
        System.out.println("        │   └── SQLException");
        System.out.println("        └── Unchecked (RuntimeException)");
        System.out.println("            ├── NullPointerException");
        System.out.println("            ├── ArrayIndexOutOfBoundsException");
        System.out.println("            └── ArithmeticException");
    }
    
    /**
     * Demonstrates basic try-catch block.
     */
    private static void demonstrateTryCatch() {
        try {
            int[] numbers = {1, 2, 3};
            System.out.println("Accessing index 5: " + numbers[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught exception: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
        }
        System.out.println("Program continues after exception handling");
    }
    
    /**
     * Demonstrates try-catch-finally block.
     */
    private static void demonstrateTryCatchFinally() {
        try {
            System.out.println("In try block");
            int result = 10 / 0;  // Will throw ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
        } finally {
            System.out.println("Finally block always executes");
        }
    }
    
    /**
     * Demonstrates handling multiple exceptions with separate catch blocks.
     */
    private static void demonstrateMultipleCatch() {
        try {
            // Simulate different exception scenarios
            int scenario = 1;
            
            switch (scenario) {
                case 1:
                    String str = null;
                    System.out.println(str.length());  // NullPointerException
                    break;
                case 2:
                    int[] arr = {1, 2, 3};
                    System.out.println(arr[10]);  // ArrayIndexOutOfBoundsException
                    break;
                case 3:
                    int num = Integer.parseInt("abc");  // NumberFormatException
                    break;
            }
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: accessing null object");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught ArrayIndexOutOfBoundsException: invalid index");
        } catch (NumberFormatException e) {
            System.out.println("Caught NumberFormatException: invalid number format");
        } catch (Exception e) {
            System.out.println("Caught generic Exception: " + e.getClass().getSimpleName());
        }
    }
    
    /**
     * Demonstrates try-with-resources for automatic resource management.
     */
    private static void demonstrateTryWithResources() {
        // Example: try-with-resources automatically closes the resource
        try (SimpleResource resource = new SimpleResource("Test Resource")) {
            System.out.println("Using resource: " + resource.getName());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println("Resource automatically closed");
    }
    
    /**
     * Demonstrates custom exception creation and usage.
     */
    private static void demonstrateCustomExceptions() {
        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Custom exception caught: " + e.getMessage());
        }
        
        try {
            validateAge(25);
            System.out.println("Age validation successful");
        } catch (InvalidAgeException e) {
            System.out.println("Custom exception: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates exception propagation through method calls.
     */
    private static void demonstrateExceptionPropagation() {
        try {
            methodLevelOne();
        } catch (InvalidAgeException e) {
            System.out.println("Caught at top level: " + e.getMessage());
        }
    }
    
    // ===== HELPER METHODS =====
    
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be 18 or older. Provided: " + age);
        }
    }
    
    public static void methodLevelOne() throws InvalidAgeException {
        methodLevelTwo();
    }
    
    public static void methodLevelTwo() throws InvalidAgeException {
        validateAge(10);  // Throws exception that propagates up
    }
    
    /**
     * Custom exception class for age validation.
     */
    public static class InvalidAgeException extends Exception {
        public InvalidAgeException(String message) {
            super(message);
        }
    }
    
    /**
     * Simple resource class implementing AutoCloseable for try-with-resources.
     */
    public static class SimpleResource implements AutoCloseable {
        private String name;
        
        public SimpleResource(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public void close() throws Exception {
            System.out.println("SimpleResource.close() called - cleaning up " + name);
        }
    }
}
