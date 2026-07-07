package com.javaacademy.lab49.offheap;

import java.lang.foreign.*;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;

public class ForeignMemoryExample {

    public static void main(String[] args) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(256);

            segment.set(ValueLayout.JAVA_INT, 0, 42);
            segment.set(ValueLayout.JAVA_LONG, 8, 0xCAFEBABE_DEADBEEFL);
            segment.set(ValueLayout.JAVA_DOUBLE, 16, 3.14159265358979);

            int i = segment.get(ValueLayout.JAVA_INT, 0);
            long l = segment.get(ValueLayout.JAVA_LONG, 8);
            double d = segment.get(ValueLayout.JAVA_DOUBLE, 16);

            System.out.printf("Foreign: int=%d long=0x%X double=%.10f%n", i, l, d);

            StructLayout personLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("id"),
                MemoryLayout.paddingLayout(4),
                ValueLayout.JAVA_LONG.withName("timestamp"),
                ValueLayout.JAVA_DOUBLE.withName("score")
            );
            VarHandle idHandle = personLayout.varHandle(PathElement.groupElement("id"));
            VarHandle tsHandle = personLayout.varHandle(PathElement.groupElement("timestamp"));
            VarHandle scoreHandle = personLayout.varHandle(PathElement.groupElement("score"));

            MemorySegment person = arena.allocate(personLayout);
            idHandle.set(person, 0L, 1001);
            tsHandle.set(person, 0L, System.currentTimeMillis());
            scoreHandle.set(person, 0L, 98.5);

            System.out.printf("Person: id=%d ts=%d score=%.1f%n",
                (int) idHandle.get(person, 0L),
                (long) tsHandle.get(person, 0L),
                (double) scoreHandle.get(person, 0L));
        }
    }
}
