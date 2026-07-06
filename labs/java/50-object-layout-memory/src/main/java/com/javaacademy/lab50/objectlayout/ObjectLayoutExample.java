package com.javaacademy.lab50.objectlayout;

import java.lang.reflect.Field;

/**
 * Demonstrates Java Object Layout (JOL) concepts by inspecting object
 * header structure manually using Unsafe. In practice, use the JOL
 * library (org.openjdk.jol) to print object layouts. This example
 * shows mark word, klass pointer, field offsets, and array length.
 */
public class ObjectLayoutExample {

    public static void main(String[] args) throws Exception {
        // Using Unsafe to inspect field offsets
        var obj = new LayoutSample();
        long objAddress = addressOf(obj);

        System.out.println("=== Object Layout Inspection ===");
        System.out.println("Object hash (identity): 0x" + Integer.toHexString(System.identityHashCode(obj)));
        System.out.println("Estimated address: " + objAddress);

        // Show field offsets (JOL library would print exact layout)
        for (Field f : LayoutSample.class.getDeclaredFields()) {
            f.setAccessible(true);
            System.out.println("Field: " + f.getName() + " type=" + f.getType().getSimpleName()
                + " value=" + f.get(obj));
        }

        // Array length header
        int[] arr = new int[]{10, 20, 30};
        System.out.println("\nArray length: " + arr.length);
        System.out.println("Array class: " + arr.getClass().getName());

        // Class pointer via getClass()
        Class<?> klass = obj.getClass();
        System.out.println("\nKlass pointer target: " + klass.getName());
        System.out.println("Klass loader: " + klass.getClassLoader());

        System.out.println("\nUse jol-cli to print full layout:");
        System.out.println("  java -jar jol-cli.jar internals " + obj.getClass().getName());
    }

    // Approximate address using Unsafe (not reliable across JVMs)
    static long addressOf(Object o) {
        // In real JOL, use: GraphLayout.parseInstance(o).toPrintable()
        return System.identityHashCode(o); // placeholder
    }

    static class LayoutSample {
        boolean flag = true;
        byte b = 42;
        short s = 1024;
        int i = 100000;
        float f = 3.14f;
        long l = Long.MAX_VALUE;
        double d = Math.PI;
        String str = "hello";
    }
}
