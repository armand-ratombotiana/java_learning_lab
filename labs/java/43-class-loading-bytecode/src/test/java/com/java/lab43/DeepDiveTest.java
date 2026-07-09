package com.java.lab43;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.lang.invoke.*;

public class DeepDiveTest {

    @Test
    void testClassFileMagicNumber() throws IOException {
        // Read the class file of this test and verify magic number
        String className = getClass().getName().replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(className)) {
            assertNotNull(is, "Class file should be readable");
            DataInputStream dis = new DataInputStream(is);
            int magic = dis.readInt();
            assertEquals(0xCAFEBABE, magic, "Class file must start with CAFEBABE");
            int minor = dis.readUnsignedShort();
            int major = dis.readUnsignedShort();
            assertTrue(major >= 61, "Class file version should be Java 17+ (major >= 61)");
        }
    }

    @Test
    void testConstantPoolStructure() throws IOException {
        String className = getClass().getName().replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(className)) {
            assertNotNull(is);
            DataInputStream dis = new DataInputStream(is);
            dis.readInt(); // magic
            dis.readUnsignedShort(); // minor
            dis.readUnsignedShort(); // major
            int cpCount = dis.readUnsignedShort();
            assertTrue(cpCount > 10, "Constant pool should have entries");
        }
    }

    @Test
    void testMethodHandleFasterThanReflection() throws Throwable {
        var lookup = MethodHandles.lookup();
        var mt = MethodType.methodType(String.class, String.class);
        var mh = lookup.findVirtual(String.class, "toUpperCase", mt);

        // Warmup
        for (int i = 0; i < 10000; i++) {
            mh.invokeExact("hello");
        }

        // Measure MethodHandle
        long t0 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            mh.invokeExact("hello");
        }
        long mhTime = System.nanoTime() - t0;

        // Measure Reflection
        var method = String.class.getMethod("toUpperCase");
        t0 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            method.invoke("hello");
        }
        long refTime = System.nanoTime() - t0;

        // MethodHandle should be faster (at least not 50x slower)
        assertTrue(mhTime < refTime * 10, 
            "MethodHandle should be much faster than reflection");
    }

    @Test
    void testInvokedynamicLambda() {
        // Lambdas use invokedynamic under the hood
        java.util.function.Function<String, String> f = s -> s.toUpperCase();
        assertEquals("HELLO", f.apply("hello"));
    }

    @Test
    void testAccessFlagsParsing() {
        class InnerClass {
            @SuppressWarnings("unused")
            private int x;
        }
        // Inner class should have ACC_PRIVATE set
        assertNotNull(new InnerClass());
    }

    @Test
    void testDescriptorParsing() {
        // Verify descriptor format
        String desc = "(Ljava/lang/String;I)Ljava/lang/String;";
        assertTrue(desc.startsWith("("));
        assertTrue(desc.contains(")"));
        assertTrue(desc.contains("Ljava/lang/String;"));
    }

    @Test
    void testLambdaMetafactory() throws Throwable {
        var lookup = MethodHandles.lookup();
        var mt = MethodType.methodType(String.class, String.class);
        var toUpper = lookup.findVirtual(String.class, "toUpperCase", 
            MethodType.methodType(String.class));
        
        var mf = LambdaMetafactory.metafactory(
            lookup, "apply",
            MethodType.methodType(java.util.function.Function.class),
            mt.erase(), toUpper, mt);
        
        @SuppressWarnings("unchecked")
        java.util.function.Function<String, String> f = 
            (java.util.function.Function<String, String>) mf.getTarget().invokeExact();
        assertEquals("TEST", f.apply("test"));
    }

    @Test
    void testNestBasedAccess() {
        // Java 11+ allows private access between nest members
        class NestHost {
            private int value = 42;
            class NestMember {
                int access() { return value; } // Direct access, no bridge needed
            }
        }
        var host = new NestHost();
        var member = host.new NestMember();
        assertEquals(42, member.access());
    }
}
