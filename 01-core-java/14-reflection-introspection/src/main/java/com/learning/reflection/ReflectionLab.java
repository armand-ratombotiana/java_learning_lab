package com.learning.reflection;

import java.lang.reflect.*;

public class ReflectionLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Reflection & Introspection Lab ===\n");

        // 1. Class Inspection
        System.out.println("1. Inspecting a Class:");
        Class<?> clazz = Person.class;
        System.out.println("   Class: " + clazz.getName());
        System.out.println("   Superclass: " + clazz.getSuperclass().getSimpleName());
        System.out.println("   Interfaces: " + java.util.Arrays.toString(clazz.getInterfaces()));
        System.out.println("   Modifiers: " + Modifier.toString(clazz.getModifiers()));

        // 2. Field Introspection
        System.out.println("\n2. Fields:");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("   " + Modifier.toString(f.getModifiers()) + " " +
                f.getType().getSimpleName() + " " + f.getName());
        }

        // 3. Method Introspection
        System.out.println("\n3. Methods:");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("   " + Modifier.toString(m.getModifiers()) + " " +
                m.getReturnType().getSimpleName() + " " + m.getName() + "(" +
                java.util.Arrays.toString(m.getParameterTypes()) + ")");
        }

        // 4. Constructor Introspection
        System.out.println("\n4. Constructors:");
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            System.out.println("   " + c.getName() + "(" +
                java.util.Arrays.toString(c.getParameterTypes()) + ")");
        }

        // 5. Dynamic Instantiation
        System.out.println("\n5. Dynamic Instantiation & Method Invocation:");
        Person p = (Person) clazz.getDeclaredConstructor(String.class, int.class)
            .newInstance("Alice", 30);
        System.out.println("   Created: " + p);

        Method greetMethod = clazz.getMethod("greet", String.class);
        String result = (String) greetMethod.invoke(p, "Bob");
        System.out.println("   " + result);

        // 6. Field Access (including private)
        System.out.println("\n6. Accessing Private Fields:");
        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);
        System.out.println("   Private name before: " + nameField.get(p));
        nameField.set(p, "Charlie");
        System.out.println("   Private name after: " + nameField.get(p));

        // 7. Annotations
        System.out.println("\n7. Annotations:");
        if (clazz.isAnnotationPresent(Deprecated.class)) {
            System.out.println("   Class is deprecated");
        }
        for (java.lang.annotation.Annotation a : clazz.getAnnotations()) {
            System.out.println("   Annotation: " + a.annotationType().getSimpleName());
        }

        // 8. Array Reflection
        System.out.println("\n8. Array Reflection:");
        int[] array = (int[]) Array.newInstance(int.class, 5);
        for (int i = 0; i < 5; i++) Array.setInt(array, i, i * 10);
        System.out.println("   Array: " + java.util.Arrays.toString(array));

        // 9. Proxy Pattern
        System.out.println("\n9. Dynamic Proxy:");
        Service realService = new RealService();
        Service proxy = (Service) Proxy.newProxyInstance(
            Service.class.getClassLoader(),
            new Class[]{Service.class},
            (proxyObj, method, args1) -> {
                System.out.println("   [Proxy] Before " + method.getName());
                Object result1 = method.invoke(realService, args1);
                System.out.println("   [Proxy] After " + method.getName());
                return result1;
            });
        proxy.execute("test");

        System.out.println("\n=== Reflection Lab Complete ===");
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) { this.name = name; this.age = age; }
        public String greet(String other) { return "Hello " + other + ", I'm " + name; }
        public String toString() { return "Person(" + name + ", " + age + ")"; }
    }

    interface Service { void execute(String input); }
    static class RealService implements Service {
        public void execute(String input) { System.out.println("   RealService executing: " + input); }
    }
}