package com.javaacademy.lab44.jit;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class JitCompilationTest {

    @Test
    void testJitCompilationResult() {
        double result = JitCompilationDemo.compute(100_000);
        assertTrue(result > 0);
    }

    @Test
    void testInlining() {
        int r = 0;
        for (int i = 0; i < 10_000; i++) {
            r += InliningDemo.add(i, i + 1);
        }
        assertTrue(r > 0);
    }

    @Test
    void testEscapeAnalysisNoEscape() {
        int r = 0;
        for (int i = 0; i < 10_000; i++) {
            r += EscapeAnalysisDemo.noEscape();
        }
        assertEquals(300_000, r);
    }

    @Test
    void testIntrinsicArraycopy() {
        int[] src = {1, 2, 3};
        int[] dst = new int[3];
        System.arraycopy(src, 0, dst, 0, 3);
        assertArrayEquals(src, dst);
    }

    @Test
    void testDeoptimization() {
        DeoptimizationTrigger.main(new String[]{});
    }
}
