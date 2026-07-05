package com.learning.lab19;

import java.lang.annotation.*;

/**
 * Demonstrates custom annotations with retention policies.
 */
public class CustomAnnotationExample {

    public static void showCustomAnnotations() {
        System.out.println("=== Custom Annotations ===");

        AnnotatedClass obj = new AnnotatedClass();
        obj.doSomething();

        Class<?> clazz = obj.getClass();
        if (clazz.isAnnotationPresent(AuthorInfo.class)) {
            AuthorInfo info = clazz.getAnnotation(AuthorInfo.class);
            System.out.println("Class annotated with @AuthorInfo:");
            System.out.println("  Author: " + info.author());
            System.out.println("  Version: " + info.version());
            System.out.println("  Reviewed: " + info.reviewed());
        }
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface AuthorInfo {
    String author();
    double version() default 1.0;
    boolean reviewed() default false;
}

@AuthorInfo(author = "Java Academy", version = 2.1, reviewed = true)
class AnnotatedClass {
    @Loggable
    public void doSomething() {
        System.out.println("  Doing something important");
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Loggable {
}
