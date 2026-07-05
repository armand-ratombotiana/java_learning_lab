package com.learning.lab02;

/**
 * Demonstrates all 8 primitive types in Java: byte, short, int, long, float, double, char, boolean.
 */
public class PrimitivesExample {

    public static void showPrimitives() {
        System.out.println("=== Primitive Types ===");

        byte minByte = Byte.MIN_VALUE;
        byte maxByte = Byte.MAX_VALUE;
        System.out.println("byte range: " + minByte + " to " + maxByte);

        short minShort = Short.MIN_VALUE;
        short maxShort = Short.MAX_VALUE;
        System.out.println("short range: " + minShort + " to " + maxShort);

        int minInt = Integer.MIN_VALUE;
        int maxInt = Integer.MAX_VALUE;
        System.out.println("int range: " + minInt + " to " + maxInt);

        long minLong = Long.MIN_VALUE;
        long maxLong = Long.MAX_VALUE;
        System.out.println("long range: " + minLong + " to " + maxLong);

        float pi = 3.141592653589793f;
        System.out.println("float pi (precision limited): " + pi);

        double precisePi = 3.141592653589793;
        System.out.println("double pi (more precision): " + precisePi);

        char letter = 'J';
        char unicode = '\u004A';
        System.out.println("char letter: " + letter + ", unicode: " + unicode);

        boolean javaIsFun = true;
        System.out.println("boolean: " + javaIsFun);

        System.out.println("Size: byte=8b, short=16b, int=32b, long=64b, float=32b, double=64b, char=16b, boolean=~1b");
    }
}
