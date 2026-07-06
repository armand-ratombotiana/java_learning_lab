package com.javaacademy.lab49.offheap;

import java.lang.foreign.*;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

/**
 * Demonstrates the Foreign Memory API (Panama) introduced as incubator
 * in Java 14 and standardized in Java 21. MemorySegment and Arena provide
 * safe, managed access to off-heap memory without Unsafe.
 */
public class ForeignMemoryExample {

    public static void main(String[] args) {
        // Allocate off-heap memory with arena
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(256);

            // Write primitive values
            segment.set(ValueLayout.JAVA_INT, 0, 42);
            segment.set(ValueLayout.JAVA_LONG, 8, 0xCAFEBABE_DEADBEEFL);
            segment.set(ValueLayout.JAVA_DOUBLE, 16, 3.14159265358979);

            // Read back
            int i = segment.get(ValueLayout.JAVA_INT, 0);
            long l = segment.get(ValueLayout.JAVA_LONG, 8);
            double d = segment.get(ValueLayout.JAVA_DOUBLE, 16);

            System.out.printf("Foreign: int=%d long=0x%X double=%.10f%n", i, l, d);

            // Structured layout (struct-like)
            StructLayout personLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("id"),
                ValueLayout.JAVA_LONG.withName("timestamp"),
                MemoryLayout.paddingLayout(4),
                ValueLayout.JAVA_DOUBLE.withName("score")
            );
            VarHandle idHandle = personLayout.varHandle(PathElement.groupElement("id"));
            VarHandle tsHandle = personLayout.varHandle(PathElement.groupElement("timestamp"));
            VarHandle scoreHandle = personLayout.varHandle(PathElement.groupElement("score"));

            MemorySegment person = arena.allocate(personLayout);
            idHandle.set(person, 1001L);
            tsHandle.set(person, System.currentTimeMillis());
            scoreHandle.set(person, 98.5);

            System.out.printf("Person: id=%d ts=%d score=%.1f%n",
                (int) idHandle.get(person),
                (long) tsHandle.get(person),
                (double) scoreHandle.get(person));
        }
    }
}
