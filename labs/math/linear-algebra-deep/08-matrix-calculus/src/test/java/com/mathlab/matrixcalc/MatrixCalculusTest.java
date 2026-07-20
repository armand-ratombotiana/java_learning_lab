package com.mathlab.matrixcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MatrixCalculusTest {
    private static final double EPS = 1e-10;

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(MatrixCalculus.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = MatrixCalculus.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.compute((double[])null)); }

    @Test @DisplayName("Empty throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.compute(new double[0])); }

    @Test @DisplayName("Non-finite throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.compute(Double.POSITIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Param inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(MatrixCalculus.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value inputs")
    void testValues(double in) { assertTrue(Double.isFinite(MatrixCalculus.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() { assertTrue(MatrixCalculus.approxEquals(1,1,EPS)); assertFalse(MatrixCalculus.approxEquals(1,2,EPS)); }

    @Test @DisplayName("Refined")
    void testRefined() { assertTrue(Double.isFinite(MatrixCalculus.computeRefined(1.0,10))); }

    @Test @DisplayName("Variants")
    void testVariants() {
        for (MatrixCalculus.Variant v : MatrixCalculus.Variant.values())
            assertTrue(Double.isFinite(MatrixCalculus.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version")
    void testVersion() { assertFalse(MatrixCalculus.getVersion().isEmpty()); }

    @Test @DisplayName("Error")
    void testError() { assertTrue(MatrixCalculus.estimateError(1.0) >= 0); }

    @Test @DisplayName("Valid input")
    void testValid() { assertDoesNotThrow(() -> MatrixCalculus.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Invalid input")
    void testInvalid() { assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional")
    void testFunctional() { assertEquals(10.0, MatrixCalculus.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Neg iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> MatrixCalculus.computeRefined(1.0,-1)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(MatrixCalculus.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(MatrixCalculus.compute(1e-10))); }
}
