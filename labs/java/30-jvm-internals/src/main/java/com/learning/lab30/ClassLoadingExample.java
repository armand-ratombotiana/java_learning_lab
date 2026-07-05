package com.learning.lab30;

/**
 * Demonstrates class loading, static initialization, and class object inspection.
 */
public class ClassLoadingExample {

    public static void showClassLoading() throws ClassNotFoundException {
        System.out.println("=== Class Loading ===");

        Class<?> clazz1 = Class.forName("com.learning.lab30.DemoClass");
        System.out.println("Loaded: " + clazz1.getName());

        Class<?> clazz2 = DemoClass.class;
        System.out.println("Via .class: " + clazz2.getName());

        DemoClass instance = new DemoClass();
        Class<?> clazz3 = instance.getClass();
        System.out.println("Via getClass(): " + clazz3.getName());

        System.out.println("Same class? " + (clazz1 == clazz2 && clazz2 == clazz3));

        System.out.println("ClassLoader: " + clazz1.getClassLoader());
        System.out.println("Superclass: " + clazz1.getSuperclass().getSimpleName());

        Package pkg = clazz1.getPackage();
        System.out.println("Package: " + pkg.getName());
    }
}

class DemoClass {
    static {
        System.out.println("  DemoClass static initializer running");
    }

    public DemoClass() {
        System.out.println("  DemoClass constructor running");
    }
}
