package com.javaacademy.lab49.offheap;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Demonstrates VarHandle for atomic field updates and off-heap
 * memory access. VarHandle provides a type-safe, performant alternative
 * to sun.misc.Unsafe for atomic operations on object fields and array elements.
 */
public class VarHandleExample {

    private volatile int counter = 0;
    private static final VarHandle COUNTER_HANDLE;

    static {
        try {
            COUNTER_HANDLE = MethodHandles.lookup()
                .findVarHandle(VarHandleExample.class, "counter", int.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        var example = new VarHandleExample();

        // Atomic increment using VarHandle.getAndAdd
        int prev = (int) COUNTER_HANDLE.getAndAdd(example, 5);
        System.out.println("Previous: " + prev + ", Current: " + example.counter);

        // Atomic compare-and-set
        boolean cas = COUNTER_HANDLE.compareAndSet(example, 5, 10);
        System.out.println("CAS: " + cas + ", Counter: " + example.counter);

        // Volatile read/write
        COUNTER_HANDLE.setVolatile(example, 42);
        int read = (int) COUNTER_HANDLE.getVolatile(example);
        System.out.println("Volatile read: " + read);

        // Array element handle
        VarHandle arrayHandle = MethodHandles.arrayElementVarHandle(int[].class);
        int[] arr = new int[]{1, 2, 3, 4, 5};
        arrayHandle.setVolatile(arr, 2, 99);
        System.out.println("Array[2] = " + arr[2]);

        System.out.println("VarHandle demo complete.");
    }
}
