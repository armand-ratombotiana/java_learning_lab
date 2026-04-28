package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for ControlFlow class.
 * Tests all control flow statements and methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Control Flow Tests")
class ControlFlowTest {
    
    @Test
    @DisplayName("Should demonstrate control flow without errors")
    void testDemonstrateControlFlow() {
        assertThatCode(() -> ControlFlow.demonstrateControlFlow())
            .doesNotThrowAnyException();
    }
    
    @ParameterizedTest
    @CsvSource({
        "5, positive",
        "-5, negative",
        "0, zero",
        "100, positive",
        "-100, negative"
    })
    @DisplayName("Should check number correctly")
    void testCheckNumber(int number, String expected) {
        assertThat(ControlFlow.checkNumber(number)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Should handle maximum integer value")
    void testCheckNumberMaxValue() {
        assertThat(ControlFlow.checkNumber(Integer.MAX_VALUE)).isEqualTo("positive");
    }
    
    @Test
    @DisplayName("Should handle minimum integer value")
    void testCheckNumberMinValue() {
        assertThat(ControlFlow.checkNumber(Integer.MIN_VALUE)).isEqualTo("negative");
    }
    
    @ParameterizedTest
    @CsvSource({
        "1, Monday",
        "2, Tuesday",
        "3, Wednesday",
        "4, Thursday",
        "5, Friday",
        "6, Saturday",
        "7, Sunday"
    })
    @DisplayName("Should return correct day name")
    void testGetDayName(int day, String expected) {
        assertThat(ControlFlow.getDayName(day)).isEqualTo(expected);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 8, -1, 100})
    @DisplayName("Should return 'Invalid day' for invalid day numbers")
    void testGetDayNameInvalid(int day) {
        assertThat(ControlFlow.getDayName(day)).isEqualTo("Invalid day");
    }
    
    @Test
    @DisplayName("Should calculate sum up to n correctly")
    void testSumUpTo() {
        assertThat(ControlFlow.sumUpTo(5)).isEqualTo(15); // 1+2+3+4+5
        assertThat(ControlFlow.sumUpTo(10)).isEqualTo(55); // 1+2+...+10
        assertThat(ControlFlow.sumUpTo(1)).isEqualTo(1);
        assertThat(ControlFlow.sumUpTo(0)).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should handle negative numbers in sumUpTo")
    void testSumUpToNegative() {
        assertThat(ControlFlow.sumUpTo(-5)).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should calculate sum for large numbers")
    void testSumUpToLarge() {
        assertThat(ControlFlow.sumUpTo(100)).isEqualTo(5050);
    }
    
    @Test
    @DisplayName("Should calculate factorial correctly")
    void testFactorial() {
        assertThat(ControlFlow.factorial(0)).isEqualTo(1);
        assertThat(ControlFlow.factorial(1)).isEqualTo(1);
        assertThat(ControlFlow.factorial(5)).isEqualTo(120); // 5! = 120
        assertThat(ControlFlow.factorial(10)).isEqualTo(3628800); // 10!
    }
    
    @Test
    @DisplayName("Should handle factorial of small numbers")
    void testFactorialSmall() {
        assertThat(ControlFlow.factorial(2)).isEqualTo(2);
        assertThat(ControlFlow.factorial(3)).isEqualTo(6);
        assertThat(ControlFlow.factorial(4)).isEqualTo(24);
    }
    
    @Test
    @DisplayName("Should handle factorial of negative numbers")
    void testFactorialNegative() {
        assertThat(ControlFlow.factorial(-1)).isEqualTo(1);
        assertThat(ControlFlow.factorial(-5)).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should calculate factorial for larger numbers")
    void testFactorialLarge() {
        assertThat(ControlFlow.factorial(12)).isEqualTo(479001600L);
        assertThat(ControlFlow.factorial(15)).isEqualTo(1307674368000L);
    }
    
    @Test
    @DisplayName("Should handle edge cases for checkNumber")
    void testCheckNumberEdgeCases() {
        assertThat(ControlFlow.checkNumber(1)).isEqualTo("positive");
        assertThat(ControlFlow.checkNumber(-1)).isEqualTo("negative");
    }
    
    @Test
    @DisplayName("Should handle all valid day numbers")
    void testAllValidDays() {
        for (int i = 1; i <= 7; i++) {
            assertThat(ControlFlow.getDayName(i)).isNotEqualTo("Invalid day");
        }
    }
    
    @Test
    @DisplayName("Should calculate sum correctly for sequential numbers")
    void testSumUpToSequential() {
        // Sum of 1 to n = n(n+1)/2
        for (int n = 1; n <= 10; n++) {
            int expected = n * (n + 1) / 2;
            assertThat(ControlFlow.sumUpTo(n)).isEqualTo(expected);
        }
    }
}