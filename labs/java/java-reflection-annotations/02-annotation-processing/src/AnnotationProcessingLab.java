package com.java.reflection.annotations.lab02;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Loggable {
    String level() default "INFO";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Timed {}

class Calculator {

    @Loggable(level = "DEBUG")
    public int add(int a, int b) {
        return a + b;
    }

    @Loggable
    @Timed
    public int multiply(int a, int b) {
        return a * b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }
}

public class AnnotationProcessingLab {

    public static void main(String[] args) throws Exception {
        Calculator calc = new Calculator();

        for (Method m : Calculator.class.getDeclaredMethods()) {
            System.out.println("Method: " + m.getName());

            if (m.isAnnotationPresent(Loggable.class)) {
                Loggable log = m.getAnnotation(Loggable.class);
                System.out.println("  @Loggable(level=" + log.level() + ")");
            }

            if (m.isAnnotationPresent(Timed.class)) {
                System.out.println("  @Timed");
            }

            if (m.getParameterCount() == 2 && m.getReturnType() == int.class) {
                Object result = m.invoke(calc, 5, 3);
                System.out.println("  Result: " + result);
            }

            System.out.println();
        }
    }
}
