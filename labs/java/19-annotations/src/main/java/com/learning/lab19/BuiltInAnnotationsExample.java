package com.learning.lab19;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates built-in annotations: @Override, @Deprecated, @SuppressWarnings, @FunctionalInterface.
 */
public class BuiltInAnnotationsExample {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void showBuiltInAnnotations() {
        System.out.println("=== Built-in Annotations ===");

        Child child = new Child();
        System.out.println(child.message());

        OldClass old = new OldClass();
        old.oldMethod();

        List rawList = new ArrayList();
        rawList.add("unchecked");
        System.out.println("Raw type used: " + rawList);

        Greeter greeter = () -> "Hello from @FunctionalInterface!";
        System.out.println(greeter.greet());
    }
}

class Parent {
    public String message() {
        return "Parent message";
    }
}

class Child extends Parent {
    @Override
    public String message() {
        return "Child overridden message";
    }
}

@Deprecated(since = "2.0", forRemoval = true)
class OldClass {
    @Deprecated
    public void oldMethod() {
        System.out.println("This method is deprecated");
    }
}

@FunctionalInterface
interface Greeter {
    String greet();
}
