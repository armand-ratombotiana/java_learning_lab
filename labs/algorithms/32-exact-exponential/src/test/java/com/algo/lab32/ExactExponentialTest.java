package com.algo.lab32;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExactExponentialTest {

    @Test
    void testMeetInTheMiddle() {
        assertTrue(MeetInTheMiddle.subsetSum(new int[]{3, 5, 7, 2, 9}, 10));
        assertFalse(MeetInTheMiddle.subsetSum(new int[]{3, 5, 7, 2, 9}, 30));
    }

    @Test
    void testMeetInTheMiddleEmpty() {
        assertFalse(MeetInTheMiddle.subsetSum(new int[]{}, 5));
    }

    @Test
    void testFastSubsetConvolution() {
        int n = 3;
        int size = 1 << n;
        double[] a = new double[size];
        double[] b = new double[size];
        for (int i = 0; i < size; i++) {
            a[i] = 1;
            b[i] = 1;
        }
        double[] c = FastSubsetConvolution.convolve(a, b, n);
        for (int mask = 0; mask < size; mask++) {
            assertEquals(Math.pow(2, Integer.bitCount(mask)), c[mask], 1e-9);
        }
    }

    @Test
    void testZetaMoebius() {
        int n = 3;
        double[] f = new double[1 << n];
        for (int i = 0; i < f.length; i++) f[i] = i + 1;
        double[] F = FastSubsetConvolution.zetaTransform(f, n);
        double[] back = FastSubsetConvolution.moebiusTransform(F, n);
        assertArrayEquals(f, back, 1e-9);
    }
}
