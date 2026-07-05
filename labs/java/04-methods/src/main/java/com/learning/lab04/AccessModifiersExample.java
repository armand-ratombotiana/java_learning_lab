package com.learning.lab04;

/**
 * Demonstrates access modifiers (public, protected, default, private) and static methods.
 */
public class AccessModifiersExample {

    public static void showAccessModifiers() {
        System.out.println("=== Access Modifiers & Static ===");

        Demo demo = new Demo();
        System.out.println("publicField: " + demo.publicField);
        System.out.println("protectedField: " + demo.protectedField);
        System.out.println("defaultField: " + demo.defaultField);

        System.out.println("staticField: " + Demo.staticField);
        Demo.staticMethod();
    }
}

class Demo {
    public String publicField = "public — accessible everywhere";
    protected String protectedField = "protected — accessible in package + subclasses";
    String defaultField = "default (package-private) — accessible in package";
    @SuppressWarnings("unused")
    private String privateField = "private — accessible only within this class";

    static String staticField = "static — belongs to class, not instance";

    static void staticMethod() {
        System.out.println("staticMethod() called without an instance");
    }
}
