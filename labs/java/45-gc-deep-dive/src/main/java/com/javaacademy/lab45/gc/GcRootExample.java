package com.javaacademy.lab45.gc;

import java.util.*;

/**
 * Forces GC roots: stack references, static fields, JNI global references.
 * Demonstrates what the GC treats as root objects during tracing.
 */
public class GcRootExample {

    // Static field = GC root
    private static GcRootExample staticRoot;

    // Instance fields are reachable via this (stack root)
    private final Map<String, Object> cache = new HashMap<>();
    private final List<byte[]> data = new ArrayList<>();

    /**
     * Stack root: the 'local' variable references the object on the heap.
     */
    public static void stackReference() {
        @SuppressWarnings("unused")
        GcRootExample local = new GcRootExample();
        // local is a stack root — reachable while this method executes
        local.cache.put("key", "value");
        System.out.println("Stack root active: local.cache = " + local.cache.get("key"));
        // When method returns, local is no longer a root → object becomes GC eligible
    }

    public static void staticReference() {
        staticRoot = new GcRootExample();
        staticRoot.cache.put("static-key", "static-value");
        System.out.println("Static root active: staticRoot.cache = " + staticRoot.cache.get("static-key"));
    }

    public static void main(String[] args) {
        System.out.println("=== GC Root Examples ===\n");

        stackReference();

        staticReference();

        // Keep a hard reference (also a stack root here in main)
        List<GcRootExample> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new GcRootExample());
        }
        System.out.println("List (stack root) holds " + list.size() + " objects");

        // Suggest GC — roots still alive, nothing collected
        System.gc();
        System.out.println("After GC (roots still active): list size = " + list.size());

        // Clear references
        list.clear();
        staticRoot = null;

        // Now objects are eligible for GC
        System.gc();
        System.out.println("After clearing roots: objects eligible for collection");
    }
}
