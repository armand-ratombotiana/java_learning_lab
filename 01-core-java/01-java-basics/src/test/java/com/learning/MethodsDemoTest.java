package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MethodsDemo class.
 * Tests method fundamentals, overloading, varargs, and pass-by-value semantics.
 */
class MethodsDemoTest {
    
    // === METHOD BASICS TESTS ===
    
    @Test
    void testMethodBasicsExecutesWithoutError() {
        assertDoesNotThrow(() -> MethodsDemo.demonstrateMethods());
    }
    
    // === HELPER METHOD TESTS ===
    
    @Test
    void testAddMethodWithTwoIntegers() {
        int result = MethodsDemo.add(5, 3);
        assertEquals(8, result, "Addition of 5 and 3 should be 8");
    }
    
    @Test
    void testAddMethodWithNegativeNumbers() {
        int result = MethodsDemo.add(-5, 3);
        assertEquals(-2, result, "Addition with negative numbers");
    }
    
    @Test
    void testAddMethodWithZero() {
        int result = MethodsDemo.add(0, 5);
        assertEquals(5, result, "Adding zero should return the other number");
    }
    
    @ParameterizedTest
    @CsvSource({
        "5, 3, 8",
        "10, 20, 30",
        "-5, 5, 0",
        "0, 0, 0",
        "100, 200, 300"
    })
    void testAddMethodWithMultipleValues(int a, int b, int expected) {
        assertEquals(expected, MethodsDemo.add(a, b));
    }
    
    // === OVERLOADED METHODS TESTS ===
    
    @Test
    void testAddMethodTakesThreeIntegers() {
        int result = MethodsDemo.add(5, 3, 2);
        assertEquals(10, result, "Addition of three numbers");
    }
    
    @Test
    void testMultiplyMethodOverloading() {
        double intResult = MethodsDemo.multiply(5, 3.0);
        assertEquals(15.0, intResult, 0.01, "Integer * double multiplication");
        
        double doubleResult = MethodsDemo.multiply(2.5, 4.0);
        assertEquals(10.0, doubleResult, 0.01, "Double multiplication");
    }
    
    // === VARARGS TESTS ===
    
    @Test
    void testSumMethodWithVarargs() {
        int result = MethodsDemo.sum(1, 2, 3, 4, 5);
        assertEquals(15, result, "Sum of 1+2+3+4+5");
    }
    
    @Test
    void testSumMethodWithSingleArgument() {
        int result = MethodsDemo.sum(42);
        assertEquals(42, result, "Sum of single number");
    }
    
    @Test
    void testSumMethodWithNoArguments() {
        int result = MethodsDemo.sum();
        assertEquals(0, result, "Sum of no arguments should be 0");
    }
    
    @Test
    void testSumMethodWithLargeNumbers() {
        int result = MethodsDemo.sum(1000, 2000, 3000, 4000);
        assertEquals(10000, result, "Sum of large numbers");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000})
    void testSumMethodWithSingleValues(int value) {
        assertEquals(value, MethodsDemo.sum(value));
    }
    
    // === RETURN TYPE TESTS ===
    
    @Test
    void testIsPrimeMethodReturnsTrueForPrimes() {
        assertTrue(MethodsDemo.isPrime(2), "2 is prime");
        assertTrue(MethodsDemo.isPrime(3), "3 is prime");
        assertTrue(MethodsDemo.isPrime(5), "5 is prime");
        assertTrue(MethodsDemo.isPrime(7), "7 is prime");
        assertTrue(MethodsDemo.isPrime(11), "11 is prime");
    }
    
    @Test
    void testIsPrimeMethodReturnsFalseForNonPrimes() {
        assertFalse(MethodsDemo.isPrime(0), "0 is not prime");
        assertFalse(MethodsDemo.isPrime(1), "1 is not prime");
        assertFalse(MethodsDemo.isPrime(4), "4 is not prime");
        assertFalse(MethodsDemo.isPrime(6), "6 is not prime");
        assertFalse(MethodsDemo.isPrime(8), "8 is not prime");
    }
    
    @Test
    void testIsPrimeMethodReturnsFalseForNegatives() {
        assertFalse(MethodsDemo.isPrime(-5), "Negative numbers are not prime");
        assertFalse(MethodsDemo.isPrime(-1), "Negative numbers are not prime");
    }
    
    // === GET METHOD TESTS ===
    
    @Test
    void testGetPositiveNumberReturnsPositiveValue() {
        int result = MethodsDemo.getPositiveNumber(10);
        assertEquals(10, result, "Should return positive number as-is");
        
        int zeroResult = MethodsDemo.getPositiveNumber(-5);
        assertEquals(0, zeroResult, "Should return 0 for negative");
    }
    
    @Test
    void testGetGreetingReturnsString() {
        String result = MethodsDemo.getGreeting("Alice");
        assertNotNull(result, "Greeting should not be null");
        assertFalse(result.isEmpty(), "Greeting should not be empty");
    }
    
    // === PASS-BY-VALUE TESTS ===
    
    @Test
    void testModifyPrimitiveDoesNotChangeOriginal() {
        int original = 10;
        MethodsDemo.modifyPrimitive(original);
        assertEquals(10, original, "Original primitive should not change");
    }
    
    @Test
    void testModifyArrayChangesOriginalContent() {
        int[] arr = {1, 2, 3};
        MethodsDemo.modifyArray(arr);
        assertEquals(99, arr[1], "Array content at index 1 should be modified to 99");
        assertEquals(1, arr[0], "Array content at index 0 should remain unchanged");
    }
    
    @Test
    void testModifyStringDoesNotChangeOriginal() {
        String original = "hello";
        MethodsDemo.modifyString(original);
        assertEquals("hello", original, "String should not change (immutable)");
    }
    
    @Test
    void testReassignmentInMethodDoesNotAffectOriginal() {
        int[] originalArray = {1, 2, 3};
        int[] arrayBefore = originalArray;
        MethodsDemo.reassignArray(originalArray);
        assertSame(arrayBefore, originalArray, "Original array reference should not change");
    }
    
    // === EDGE CASES AND SPECIAL SCENARIOS ===
    
    @Test
    void testMethodChainingWithMultipleCalls() {
        int result = MethodsDemo.add(MethodsDemo.add(5, 3), (int)MethodsDemo.multiply(2, 4.0));
        assertEquals(16, result, "5+3+2*4 = 16");
    }
    
    @Test
    void testPrintNTimesWithZero() {
        assertDoesNotThrow(() -> MethodsDemo.printNTimes("test", 0));
    }
    
    @Test
    void testPrintNTimesWithNegative() {
        assertDoesNotThrow(() -> MethodsDemo.printNTimes("test", -1));
    }
    
    @Test
    void testOverloadingResolution() {
        // Test that correct overloaded method is called
        assertEquals(8, MethodsDemo.add(5, 3));        // int version
        assertEquals(10, MethodsDemo.add(5, 3, 2));    // three ints version
    }
    
    // === BOUNDARY TESTS ===
    
    @Test
    void testAddWithIntegerMaxValue() {
        // Note: This tests overflow behavior
        int result = MethodsDemo.add(Integer.MAX_VALUE - 5, 3);
        assertEquals(Integer.MAX_VALUE - 2, result);
    }
    
    @Test
    void testSumWithManyArguments() {
        int result = MethodsDemo.sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertEquals(55, result, "Sum of 1 to 10");
    }
    
    @Test
    void testIsPrimeWithLargeNumber() {
        assertTrue(MethodsDemo.isPrime(97), "97 is prime");
        assertFalse(MethodsDemo.isPrime(100), "100 is not prime");
    }
}
