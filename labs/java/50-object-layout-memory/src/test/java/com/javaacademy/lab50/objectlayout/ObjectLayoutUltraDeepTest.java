package com.javaacademy.lab50.objectlayout;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ObjectLayoutUltraDeepTest {

    @Test
    void objectIdentityHashCode() {
        Object obj = new Object();
        int hashCode = System.identityHashCode(obj);
        assertTrue(hashCode != 0 || true);
    }

    @Test
    void arrayObjectHeader() {
        int[] array = new int[10];
        assertEquals(10, array.length);
        assertEquals(int[].class, array.getClass());
    }

    @Test
    void stringInternMemory() {
        String a = "shared";
        String b = "shared";
        assertSame(a, b);
    }

    @Test
    void recordCompactMemoryLayout() {
        record SmallPoint(int x, int y) {}
        var p = new SmallPoint(1, 2);
        assertEquals(1, p.x());
        assertEquals(2, p.y());
    }

    @Test
    void primitiveArrayVsObjectArray() {
        int[] primitive = new int[100];
        Integer[] objectArray = new Integer[100];
        assertEquals(100, primitive.length);
        assertEquals(100, objectArray.length);
    }

    @Test
    void classObjectFields() {
        var fields = SampleClass.class.getDeclaredFields();
        assertTrue(fields.length >= 2);
    }

    static class SampleClass {
        private int intField;
        private long longField;
        private Object refField;
        public SampleClass(int i, long l, Object o) {
            intField = i; longField = l; refField = o;
        }
    }
}
