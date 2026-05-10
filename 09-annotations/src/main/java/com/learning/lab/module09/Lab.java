package com.learning.lab.module09;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class Lab {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 09: Annotations ===");
        builtInAnnotationsDemo();
        customAnnotationDemo();
        metaAnnotationsDemo();
        reflectionDemo();
    }

    static void builtInAnnotationsDemo() {
        System.out.println("\n--- Built-in Annotations ---");
        DeprecatedClass obj = new DeprecatedClass();
        obj.oldMethod();
    }

    static void customAnnotationDemo() throws Exception {
        System.out.println("\n--- Custom Annotations ---");
        processAnnotatedClass(MyClass.class);
    }

    static void metaAnnotationsDemo() {
        System.out.println("\n--- Meta-Annotations ---");
        System.out.println("Retention: " + CustomAnnotation.class.getAnnotation(Retention.class).value());
        System.out.println("Target: " + CustomAnnotation.class.getAnnotation(Target.class).value());
    }

    static void reflectionDemo() {
        System.out.println("\n--- Reflection with Annotations ---");
        for (Method m : MyClass.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(RunThis.class)) {
                System.out.println("Found annotated method: " + m.getName());
            }
        }
    }

    static void processAnnotatedClass(Class<?> clazz) throws Exception {
        if (clazz.isAnnotationPresent(CustomAnnotation.class)) {
            CustomAnnotation annotation = clazz.getAnnotation(CustomAnnotation.class);
            System.out.println("Author: " + annotation.author());
            System.out.println("Version: " + annotation.version());
            System.out.println("Description: " + annotation.description());
        }

        Object instance = clazz.getDeclaredConstructor().newInstance();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Execute.class)) {
                m.invoke(instance);
            }
        }
    }
}

@Deprecated
class DeprecatedClass {
    @Deprecated
    public void oldMethod() {
        System.out.println("This method is deprecated");
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface CustomAnnotation {
    String author() default "Unknown";
    int version() default 1;
    String description() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Execute {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface RunThis {
    String action() default "run";
}

@CustomAnnotation(author = "John Doe", version = 2, description = "Demo class")
class MyClass {
    @Execute("sayHello")
    public void hello() {
        System.out.println("Hello from method");
    }

    @RunThis(action = "test")
    public void testMethod() {
        System.out.println("Test method executed");
    }
}