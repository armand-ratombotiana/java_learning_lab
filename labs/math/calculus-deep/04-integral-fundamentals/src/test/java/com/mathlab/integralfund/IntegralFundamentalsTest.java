package com.mathlab.integralfund;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class IntegralFundamentalsTest {

    private static final double EPS = 1e-10;

    @BeforeEach
    void setUp() {}

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(IntegralFundamentals.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = IntegralFundamentals.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null input throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.compute((double[])null)); }

    @Test @DisplayName("Empty input throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.compute(new double[0])); }

    @Test @DisplayName("Non-finite input throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.compute(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.compute(Double.NEGATIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Parameterized inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(IntegralFundamentals.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value source inputs")
    void testValues(double in) { assertTrue(Double.isFinite(IntegralFundamentals.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() {
        assertTrue(IntegralFundamentals.approxEquals(1.0, 1.0, EPS));
        assertFalse(IntegralFundamentals.approxEquals(1.0, 2.0, EPS));
    }

    @Test @DisplayName("Refined computation")
    void testRefined() {
        assertTrue(Double.isFinite(IntegralFundamentals.computeRefined(1.0, 10)));
    }

    @Test @DisplayName("All variants finite")
    void testVariants() {
        for (IntegralFundamentals.Variant v : IntegralFundamentals.Variant.values())
            assertTrue(Double.isFinite(IntegralFundamentals.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version non-empty")
    void testVersion() { assertFalse(IntegralFundamentals.getVersion().isEmpty()); }

    @Test @DisplayName("Error estimate")
    void testError() { assertTrue(IntegralFundamentals.estimateError(1.0) >= 0); }

    @Test @DisplayName("Validate valid input")
    void testValidateValid() { assertDoesNotThrow(() -> IntegralFundamentals.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Validate invalid input")
    void testValidateInvalid() { assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional interface")
    void testFunctional() { assertEquals(10.0, IntegralFundamentals.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Negative iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.computeRefined(1.0, -1)); }

    @Test @DisplayName("Zero iterations")
    void testZeroIter() { assertThrows(IllegalArgumentException.class, () -> IntegralFundamentals.computeRefined(1.0, 0)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(IntegralFundamentals.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(IntegralFundamentals.compute(1e-10))); }
}
