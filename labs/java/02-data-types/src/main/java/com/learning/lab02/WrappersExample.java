package com.learning.lab02;

/**
 * Demonstrates wrapper classes (Integer, Double, Boolean, etc.) and their utility methods.
 */
public class WrappersExample {

    public static void showWrappers() {
        System.out.println("=== Wrapper Classes ===");

        Integer intObj = Integer.valueOf(42);
        Double doubleObj = Double.valueOf(3.14);
        Boolean boolObj = Boolean.valueOf(true);
        Character charObj = Character.valueOf('J');

        System.out.println("Integer object: " + intObj);
        System.out.println("Double object: " + doubleObj);
        System.out.println("Boolean object: " + boolObj);
        System.out.println("Character object: " + charObj);

        int parsedInt = Integer.parseInt("123");
        double parsedDouble = Double.parseDouble("45.67");
        System.out.println("Parsed int: " + parsedInt);
        System.out.println("Parsed double: " + parsedDouble);

        System.out.println("Max of 10 and 20: " + Integer.max(10, 20));
        System.out.println("Binary of 42: " + Integer.toBinaryString(42));
        System.out.println("Hex of 255: " + Integer.toHexString(255));
        System.out.println("Is digit '5': " + Character.isDigit('5'));
        System.out.println("Is letter 'J': " + Character.isLetter('J'));
    }
}
