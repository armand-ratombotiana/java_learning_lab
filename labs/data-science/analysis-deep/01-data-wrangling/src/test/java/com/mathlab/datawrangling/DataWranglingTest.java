package com.mathlab.datawrangling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class DataWranglingTest {
    private static final double EPS = 1e-10;

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(DataWrangling.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = DataWrangling.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> DataWrangling.compute((double[])null)); }

    @Test @DisplayName("Empty throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> DataWrangling.compute(new double[0])); }

    @Test @DisplayName("Non-finite throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> DataWrangling.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> DataWrangling.compute(Double.POSITIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Param inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(DataWrangling.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value inputs")
    void testValues(double in) { assertTrue(Double.isFinite(DataWrangling.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() { assertTrue(DataWrangling.approxEquals(1,1,EPS)); assertFalse(DataWrangling.approxEquals(1,2,EPS)); }

    @Test @DisplayName("Refined")
    void testRefined() { assertTrue(Double.isFinite(DataWrangling.computeRefined(1.0,10))); }

    @Test @DisplayName("Variants")
    void testVariants() {
        for (DataWrangling.Variant v : DataWrangling.Variant.values())
            assertTrue(Double.isFinite(DataWrangling.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version")
    void testVersion() { assertFalse(DataWrangling.getVersion().isEmpty()); }

    @Test @DisplayName("Error")
    void testError() { assertTrue(DataWrangling.estimateError(1.0) >= 0); }

    @Test @DisplayName("Valid input")
    void testValid() { assertDoesNotThrow(() -> DataWrangling.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Invalid input")
    void testInvalid() { assertThrows(IllegalArgumentException.class, () -> DataWrangling.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional")
    void testFunctional() { assertEquals(10.0, DataWrangling.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Neg iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> DataWrangling.computeRefined(1.0,-1)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(DataWrangling.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(DataWrangling.compute(1e-10))); }
}
