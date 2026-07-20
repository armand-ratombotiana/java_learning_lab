package com.mathlab.vectorcalc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class VectorCalculusTest {

    private static final double EPS = 1e-10;

    @BeforeEach
    void setUp() {}

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(VectorCalculus.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = VectorCalculus.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null input throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> VectorCalculus.compute((double[])null)); }

    @Test @DisplayName("Empty input throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> VectorCalculus.compute(new double[0])); }

    @Test @DisplayName("Non-finite input throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> VectorCalculus.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> VectorCalculus.compute(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> VectorCalculus.compute(Double.NEGATIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Parameterized inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(VectorCalculus.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value source inputs")
    void testValues(double in) { assertTrue(Double.isFinite(VectorCalculus.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() {
        assertTrue(VectorCalculus.approxEquals(1.0, 1.0, EPS));
        assertFalse(VectorCalculus.approxEquals(1.0, 2.0, EPS));
    }

    @Test @DisplayName("Refined computation")
    void testRefined() {
        assertTrue(Double.isFinite(VectorCalculus.computeRefined(1.0, 10)));
    }

    @Test @DisplayName("All variants finite")
    void testVariants() {
        for (VectorCalculus.Variant v : VectorCalculus.Variant.values())
            assertTrue(Double.isFinite(VectorCalculus.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version non-empty")
    void testVersion() { assertFalse(VectorCalculus.getVersion().isEmpty()); }

    @Test @DisplayName("Error estimate")
    void testError() { assertTrue(VectorCalculus.estimateError(1.0) >= 0); }

    @Test @DisplayName("Validate valid input")
    void testValidateValid() { assertDoesNotThrow(() -> VectorCalculus.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Validate invalid input")
    void testValidateInvalid() { assertThrows(IllegalArgumentException.class, () -> VectorCalculus.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional interface")
    void testFunctional() { assertEquals(10.0, VectorCalculus.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Negative iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> VectorCalculus.computeRefined(1.0, -1)); }

    @Test @DisplayName("Zero iterations")
    void testZeroIter() { assertThrows(IllegalArgumentException.class, () -> VectorCalculus.computeRefined(1.0, 0)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(VectorCalculus.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(VectorCalculus.compute(1e-10))); }
}
