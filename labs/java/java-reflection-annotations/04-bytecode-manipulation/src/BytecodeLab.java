package com.java.reflection.annotations.lab04;

/**
 * Bytecode Manipulation Lab
 *
 * This file demonstrates the conceptual structure.
 * ASM and ByteBuddy require external dependencies;
 * run with the classpath configured for net.bytebuddy:byte-buddy:1.14+.
 *
 * The examples are commented — uncomment and add the dependency to experiment.
 */
public class BytecodeLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Bytecode Manipulation Lab ===");
        System.out.println("ASM / ByteBuddy examples require dependencies on the classpath.");
        System.out.println();
        System.out.println("ByteBuddy example (uncomment to run):");
        System.out.println("  Class<?> type = new ByteBuddy()");
        System.out.println("      .subclass(Object.class)");
        System.out.println("      .method(ElementMatchers.named(\"toString\"))");
        System.out.println("      .intercept(FixedValue.value(\"Generated!\"))");
        System.out.println("      .make()");
        System.out.println("      .load(getClass().getClassLoader())");
        System.out.println("      .getLoaded();");
        System.out.println("  Object obj = type.getDeclaredConstructor().newInstance();");
        System.out.println("  System.out.println(obj); // Generated!");
    }
}
