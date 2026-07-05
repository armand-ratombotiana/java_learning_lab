package com.learning.lab06;

/**
 * Demonstrates static fields, static methods, and static initialization blocks.
 */
public class StaticMembersExample {

    public static void showStaticMembers() {
        System.out.println("=== Static Members ===");

        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        System.out.println("Instance count (via class): " + Counter.getInstanceCount());
        System.out.println("c1 ID: " + c1.getId());
        System.out.println("c2 ID: " + c2.getId());
        System.out.println("c3 ID: " + c3.getId());

        System.out.println("Math.PI (static final): " + Math.PI);
        System.out.println("Math.sqrt(16): " + Math.sqrt(16));
    }
}

class Counter {
    private static int instanceCount = 0;
    private static final int MAX_INSTANCES = 100;
    private final int id;

    public Counter() {
        instanceCount++;
        this.id = instanceCount;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public int getId() {
        return id;
    }
}
