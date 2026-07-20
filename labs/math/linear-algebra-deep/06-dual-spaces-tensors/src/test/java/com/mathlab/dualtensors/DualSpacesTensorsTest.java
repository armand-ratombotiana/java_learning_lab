package com.mathlab.dualtensors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class DualSpacesTensorsTest {
    private static final double EPS = 1e-10;

    @Test @DisplayName("Basic computation")
    void testBasic() { assertTrue(Double.isFinite(DualSpacesTensors.compute(1.0))); }

    @Test @DisplayName("Array computation")
    void testArray() {
        double[] r = DualSpacesTensors.compute(new double[]{0,1,2,3});
        assertEquals(4, r.length);
        for (double v : r) assertTrue(Double.isFinite(v));
    }

    @Test @DisplayName("Null throws")
    void testNull() { assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.compute((double[])null)); }

    @Test @DisplayName("Empty throws")
    void testEmpty() { assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.compute(new double[0])); }

    @Test @DisplayName("Non-finite throws")
    void testNonFinite() {
        assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.compute(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.compute(Double.POSITIVE_INFINITY));
    }

    @ParameterizedTest @CsvSource({"0,0","1,1","-1,-1","2.5,2.5"})
    @DisplayName("Param inputs")
    void testParams(double in, double exp) { assertTrue(Double.isFinite(DualSpacesTensors.compute(in))); }

    @ParameterizedTest @ValueSource(doubles = {0.5, 1.5, 10, 100, 1000})
    @DisplayName("Value inputs")
    void testValues(double in) { assertTrue(Double.isFinite(DualSpacesTensors.compute(in))); }

    @Test @DisplayName("Approx equals")
    void testApprox() { assertTrue(DualSpacesTensors.approxEquals(1,1,EPS)); assertFalse(DualSpacesTensors.approxEquals(1,2,EPS)); }

    @Test @DisplayName("Refined")
    void testRefined() { assertTrue(Double.isFinite(DualSpacesTensors.computeRefined(1.0,10))); }

    @Test @DisplayName("Variants")
    void testVariants() {
        for (DualSpacesTensors.Variant v : DualSpacesTensors.Variant.values())
            assertTrue(Double.isFinite(DualSpacesTensors.computeVariant(1.0, v)));
    }

    @Test @DisplayName("Version")
    void testVersion() { assertFalse(DualSpacesTensors.getVersion().isEmpty()); }

    @Test @DisplayName("Error")
    void testError() { assertTrue(DualSpacesTensors.estimateError(1.0) >= 0); }

    @Test @DisplayName("Valid input")
    void testValid() { assertDoesNotThrow(() -> DualSpacesTensors.validateInput(new double[]{1,2,3})); }

    @Test @DisplayName("Invalid input")
    void testInvalid() { assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.validateInput(new double[]{Double.NaN})); }

    @Test @DisplayName("Functional")
    void testFunctional() { assertEquals(10.0, DualSpacesTensors.computeFunctional(x -> x*2, 5.0), EPS); }

    @Test @DisplayName("Neg iterations")
    void testNegIter() { assertThrows(IllegalArgumentException.class, () -> DualSpacesTensors.computeRefined(1.0,-1)); }

    @Test @DisplayName("Large input")
    void testLarge() { assertTrue(Double.isFinite(DualSpacesTensors.compute(1e10))); }

    @Test @DisplayName("Small input")
    void testSmall() { assertTrue(Double.isFinite(DualSpacesTensors.compute(1e-10))); }
}
