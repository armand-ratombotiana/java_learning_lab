package com.learning;

/**
 * Demonstrates all Java primitive and reference data types.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class DataTypes {
    
    /**
     * Demonstrates all Java data types with examples.
     */
    public static void demonstrateDataTypes() {
        demonstratePrimitiveTypes();
        demonstrateReferenceTypes();
        demonstrateTypeConversion();
        demonstrateWrapperClasses();
    }
    
    /**
     * Demonstrates all 8 primitive data types in Java.
     */
    private static void demonstratePrimitiveTypes() {
        System.out.println("Primitive Data Types:");
        
        // byte: 8-bit signed integer (-128 to 127)
        byte byteVar = 127;
        System.out.println("byte: " + byteVar + " (Range: -128 to 127)");
        
        // short: 16-bit signed integer (-32,768 to 32,767)
        short shortVar = 32000;
        System.out.println("short: " + shortVar + " (Range: -32,768 to 32,767)");
        
        // int: 32-bit signed integer
        int intVar = 2147483647;
        System.out.println("int: " + intVar + " (Range: -2^31 to 2^31-1)");
        
        // long: 64-bit signed integer
        long longVar = 9223372036854775807L;
        System.out.println("long: " + longVar + " (Range: -2^63 to 2^63-1)");
        
        // float: 32-bit floating point
        float floatVar = 3.14159f;
        System.out.println("float: " + floatVar + " (32-bit floating point)");
        
        // double: 64-bit floating point
        double doubleVar = 3.141592653589793;
        System.out.println("double: " + doubleVar + " (64-bit floating point)");
        
        // char: 16-bit Unicode character
        char charVar = 'A';
        System.out.println("char: " + charVar + " (Unicode: " + (int) charVar + ")");
        
        // boolean: true or false
        boolean booleanVar = true;
        System.out.println("boolean: " + booleanVar + " (true or false)");
    }
    
    /**
     * Demonstrates reference data types.
     */
    private static void demonstrateReferenceTypes() {
        System.out.println("\nReference Data Types:");
        
        // String
        String stringVar = "Hello, Java!";
        System.out.println("String: " + stringVar);
        
        // Array
        int[] arrayVar = {1, 2, 3, 4, 5};
        System.out.println("Array: " + java.util.Arrays.toString(arrayVar));
        
        // Object
        Object objectVar = new Object();
        System.out.println("Object: " + objectVar.getClass().getName());
    }
    
    /**
     * Demonstrates type conversion (casting).
     */
    private static void demonstrateTypeConversion() {
        System.out.println("\nType Conversion:");
        
        // Implicit conversion (widening)
        int intValue = 100;
        long longValue = intValue; // int to long
        double doubleValue = longValue; // long to double
        System.out.println("Implicit: int(" + intValue + ") -> long(" + longValue + ") -> double(" + doubleValue + ")");
        
        // Explicit conversion (narrowing)
        double doubleVal = 100.99;
        int intVal = (int) doubleVal; // double to int (loses decimal)
        System.out.println("Explicit: double(" + doubleVal + ") -> int(" + intVal + ")");
        
        // String to primitive
        String numberString = "123";
        int parsedInt = Integer.parseInt(numberString);
        System.out.println("String to int: \"" + numberString + "\" -> " + parsedInt);
        
        // Primitive to String
        int number = 456;
        String numberStr = String.valueOf(number);
        System.out.println("int to String: " + number + " -> \"" + numberStr + "\"");
    }
    
    /**
     * Demonstrates wrapper classes for primitive types.
     */
    private static void demonstrateWrapperClasses() {
        System.out.println("\nWrapper Classes:");
        
        // Autoboxing: primitive to wrapper
        Integer integerObj = 100; // int to Integer
        Double doubleObj = 3.14; // double to Double
        Boolean booleanObj = true; // boolean to Boolean
        
        System.out.println("Autoboxing: int -> Integer: " + integerObj);
        System.out.println("Autoboxing: double -> Double: " + doubleObj);
        System.out.println("Autoboxing: boolean -> Boolean: " + booleanObj);
        
        // Unboxing: wrapper to primitive
        int intPrimitive = integerObj; // Integer to int
        double doublePrimitive = doubleObj; // Double to double
        boolean booleanPrimitive = booleanObj; // Boolean to boolean
        
        System.out.println("Unboxing: Integer -> int: " + intPrimitive);
        System.out.println("Unboxing: Double -> double: " + doublePrimitive);
        System.out.println("Unboxing: Boolean -> boolean: " + booleanPrimitive);
        
        // Wrapper class utility methods
        System.out.println("Integer.MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Double.MAX_VALUE: " + Double.MAX_VALUE);
        System.out.println("Double.MIN_VALUE: " + Double.MIN_VALUE);
    }
    
    /**
     * Gets the size of a primitive type in bytes.
     * 
     * @param type the primitive type name
     * @return the size in bytes
     */
    public static int getSizeInBytes(String type) {
        return switch (type.toLowerCase()) {
            case "byte" -> 1;
            case "short", "char" -> 2;
            case "int", "float" -> 4;
            case "long", "double" -> 8;
            case "boolean" -> 1; // JVM dependent, typically 1 byte
            default -> -1;
        };
    }
    
    /**
     * Checks if a value is within the range of a data type.
     * 
     * @param value the value to check
     * @param type the data type
     * @return true if within range, false otherwise
     */
    public static boolean isWithinRange(long value, String type) {
        return switch (type.toLowerCase()) {
            case "byte" -> value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE;
            case "short" -> value >= Short.MIN_VALUE && value <= Short.MAX_VALUE;
            case "int" -> value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
            case "long" -> true; // long can hold any long value
            default -> false;
        };
    }
}