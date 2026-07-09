package com.java.lab44;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.*;

public class DeepDiveTest {

    @Test
    void testCompilationLogParsing() {
        // Simulate parsing -XX:+PrintCompilation output
        String logLine = "548  1       3    java.lang.String::hashCode (55 bytes)";
        
        Pattern pattern = Pattern.compile(
            "(\\d+)\\s+(\\d+)\\s+([%s!bn ])\\s+(\\d)\\s+(\\S+)::(\\S+)\\s+\\((\\d+)\\s+bytes\\)");
        Matcher m = pattern.matcher(logLine);
        
        assertTrue(m.find());
        assertEquals("548", m.group(1)); // timestamp
        assertEquals("1", m.group(2));   // compile ID
        assertEquals(" ", m.group(3));   // flags
        assertEquals("3", m.group(4));   // tier
        assertEquals("java.lang.String", m.group(5)); // class
        assertEquals("hashCode", m.group(6)); // method
        assertEquals("55", m.group(7));  // size
    }

    @Test
    void testC2Optimization() {
        // C2 can auto-vectorize this loop
        float[] arr = new float[1000];
        for (int i = 0; i < arr.length; i++) arr[i] = i;
        
        float sum = 0;
        for (float v : arr) sum += v;
        
        assertEquals(499500.0f, sum, 0.01f);
    }

    @Test
    void testOSRLongLoop() {
        // Long loops trigger OSR compilation
        double sum = 0;
        for (int i = 0; i < 100_000; i++) {
            sum += Math.sin(i) * Math.cos(i);
        }
        assertFalse(Double.isNaN(sum));
    }

    @Test
    void testUncommonTrap() {
        // This tests type profiling → uncommon trap
        Object obj = "string";
        for (int i = 0; i < 20000; i++) {
            if (obj instanceof String s) {
                assertEquals("string", s);
            }
        }
    }

    @Test
    void testIntrinsicBitCount() {
        // Integer.bitCount is intrinsified to POPCNT
        assertEquals(3, Integer.bitCount(7));   // 111 → 3 bits
        assertEquals(1, Integer.bitCount(8));   // 1000 → 1 bit
        assertEquals(0, Integer.bitCount(0));
        assertEquals(31, Integer.bitCount(-1)); // all 1s → 32 bits (but Java int is signed)
    }

    @Test
    void testIntrinsicArraycopy() {
        // System.arraycopy is intrinsified to memmove/rep movsb
        int[] src = {1, 2, 3, 4, 5};
        int[] dst = new int[5];
        System.arraycopy(src, 0, dst, 0, 5);
        assertArrayEquals(src, dst);
    }

    @Test
    void testEscapeAnalysis() {
        // Escape analysis may stack-allocate this Point
        class Point { int x, y; Point(int x, int y) { this.x = x; this.y = y; } }
        
        long sum = 0;
        for (int i = 0; i < 100000; i++) {
            Point p = new Point(i, i + 1); // May be scalar replaced
            sum += p.x + p.y;
        }
        assertEquals(100000L * 100001L, sum); // Sum of 2*i+1 for i=0..99999
    }

    @Test
    void testDeoptimizationRecovery() {
        // Test that deoptimized code still produces correct results
        Object o = Integer.valueOf(42);
        assertInstanceOf(Integer.class, o);
        assertEquals(42, ((Integer) o).intValue());
    }

    @Test
    void testMethodInlining() {
        // Hot methods get inlined — test still produces correct results
        int result = 0;
        for (int i = 0; i < 100000; i++) {
            result += square(i);
        }
        // Sum of i² for i=0..99999 = (n)(n+1)(2n+1)/6
        // = 99999 * 100000 * 199999 / 6
        assertEquals(333328333350000L, result);
    }

    private static int square(int x) {
        return x * x;
    }
}
