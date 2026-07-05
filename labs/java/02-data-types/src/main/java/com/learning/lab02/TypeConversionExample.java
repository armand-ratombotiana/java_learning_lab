package com.learning.lab02;

/**
 * Demonstrates implicit widening, explicit narrowing, autoboxing/unboxing, and type conversion.
 */
public class TypeConversionExample {

    public static void showConversions() {
        System.out.println("=== Type Conversion & Autoboxing ===");

        int intVal = 100;
        long widening = intVal;
        double widening2 = intVal;
        System.out.println("Widening int -> long: " + widening);
        System.out.println("Widening int -> double: " + widening2);

        double d = 99.99;
        int narrowing = (int) d;
        System.out.println("Narrowing double -> int: " + narrowing + " (loss of precision)");

        long big = 1_000_000_000_000L;
        int overflow = (int) big;
        System.out.println("Narrowing long -> int (overflow): " + overflow);

        Integer wrapper = 42;
        int unboxed = wrapper;
        System.out.println("Autoboxed Integer: " + wrapper + ", Unboxed: " + unboxed);

        int primitive = Integer.valueOf(100);
        System.out.println("Auto-unboxing in assignment: " + primitive);

        String strNum = "256";
        int converted = Integer.parseInt(strNum);
        System.out.println("String to int: " + converted);

        String strBool = "true";
        boolean bool = Boolean.parseBoolean(strBool);
        System.out.println("String to boolean: " + bool);
    }
}
