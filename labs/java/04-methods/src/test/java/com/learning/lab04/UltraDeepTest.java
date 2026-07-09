package com.learning.lab04;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.invoke.*;

/**
 * Ultra-deep tests for Methods lab.
 * Explores method handles, invokedynamic, vtable dispatch.
 */
class UltraDeepTest {

    @Test
    void methodHandleInvocation() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType mt = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle mh = lookup.findStatic(Integer.class, "sum", mt);
        int result = (int) mh.invokeExact(10, 20);
        assertEquals(30, result);
    }

    @Test
    void bridgeMethodCovariantReturn() {
        Parent p = new Child();
        Number val = p.getValue();
        assertInstanceOf(Integer.class, val);
    }

    static class Parent {
        Number getValue() { return 1.0; }
    }
    static class Child extends Parent {
        @Override
        Integer getValue() { return 42; }
    }
}
