package com.mathlab.multivariatestats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MultivariateStatisticsTest {
    private static final double EPS = 1e-10;

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(MultivariateStatistics.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = MultivariateStatistics.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.compute((double[])null)); }

    @Test @DisplayName("Empty throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.compute(new double[0])); }

    @Test @DisplayName("Non-finite throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.compute(Double.POSITIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Param inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(MultivariateStatistics.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value inputs")
    void testValues(double in) { assertTrue(Double.isFinite(MultivariateStatistics.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() { assertTrue(MultivariateStatistics.approxEquals(1,1,EPS)); assertFalse(MultivariateStatistics.approxEquals(1,2,EPS)); }

    @Test @DisplayName("Refined")
    void testRefined() { assertTrue(Double.isFinite(MultivariateStatistics.computeRefined(1.0,10))); }

    @Test @DisplayName("Variants")
    void testVariants() {
        for (MultivariateStatistics.Variant v : MultivariateStatistics.Variant.values())
            assertTrue(Double.isFinite(MultivariateStatistics.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version")
    void testVersion() { assertFalse(MultivariateStatistics.getVersion().isEmpty()); }

    @Test @DisplayName("Error")
    void testError() { assertTrue(MultivariateStatistics.estimateError(1.0) >= 0); }

    @Test @DisplayName("Valid input")
    void testValid() { assertDoesNotThrow(() -> MultivariateStatistics.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Invalid input")
    void testInvalid() { assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional")
    void testFunctional() { assertEquals(10.0, MultivariateStatistics.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Neg iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> MultivariateStatistics.computeRefined(1.0,-1)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(MultivariateStatistics.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(MultivariateStatistics.compute(1e-10))); }
}
