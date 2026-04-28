package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for Operators class.
 * Tests all operator types and calculations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Operators Tests")
class OperatorsTest {
    
    @Test
    @DisplayName("Should demonstrate operators without errors")
    void testDemonstrateOperators() {
        assertThatCode(() -> Operators.demonstrateOperators())
            .doesNotThrowAnyException();
    }
    
    @ParameterizedTest
    @CsvSource({
        "10, 5, '+', 15",
        "10, 5, '-', 5",
        "10, 5, '*', 50",
        "10, 5, '/', 2",
        "10, 3, '%', 1"
    })
    @DisplayName("Should calculate arithmetic operations correctly")
    void testCalculate(double a, double b, char operator, double expected) {
        assertThat(Operators.calculate(a, b, operator)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Should return NaN for division by zero")
    void testDivisionByZero() {
        assertThat(Operators.calculate(10, 0, '/')).isNaN();
    }
    
    @Test
    @DisplayName("Should return NaN for modulus by zero")
    void testModulusByZero() {
        assertThat(Operators.calculate(10, 0, '%')).isNaN();
    }
    
    @Test
    @DisplayName("Should throw exception for invalid operator")
    void testInvalidOperator() {
        assertThatThrownBy(() -> Operators.calculate(10, 5, '&'))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid operator");
    }
    
    @Test
    @DisplayName("Should handle negative numbers in addition")
    void testAdditionWithNegatives() {
        assertThat(Operators.calculate(-10, -5, '+')).isEqualTo(-15);
        assertThat(Operators.calculate(-10, 5, '+')).isEqualTo(-5);
        assertThat(Operators.calculate(10, -5, '+')).isEqualTo(5);
    }
    
    @Test
    @DisplayName("Should handle negative numbers in subtraction")
    void testSubtractionWithNegatives() {
        assertThat(Operators.calculate(-10, -5, '-')).isEqualTo(-5);
        assertThat(Operators.calculate(-10, 5, '-')).isEqualTo(-15);
        assertThat(Operators.calculate(10, -5, '-')).isEqualTo(15);
    }
    
    @Test
    @DisplayName("Should handle negative numbers in multiplication")
    void testMultiplicationWithNegatives() {
        assertThat(Operators.calculate(-10, -5, '*')).isEqualTo(50);
        assertThat(Operators.calculate(-10, 5, '*')).isEqualTo(-50);
        assertThat(Operators.calculate(10, -5, '*')).isEqualTo(-50);
    }
    
    @Test
    @DisplayName("Should handle decimal numbers")
    void testDecimalNumbers() {
        assertThat(Operators.calculate(10.5, 2.5, '+')).isEqualTo(13.0);
        assertThat(Operators.calculate(10.5, 2.5, '-')).isEqualTo(8.0);
        assertThat(Operators.calculate(10.5, 2.0, '*')).isEqualTo(21.0);
        assertThat(Operators.calculate(10.0, 2.5, '/')).isEqualTo(4.0);
    }
    
    @Test
    @DisplayName("Should evaluate logical AND correctly")
    void testLogicalAnd() {
        assertThat(Operators.evaluateLogical(true, true, "AND")).isTrue();
        assertThat(Operators.evaluateLogical(true, false, "AND")).isFalse();
        assertThat(Operators.evaluateLogical(false, true, "AND")).isFalse();
        assertThat(Operators.evaluateLogical(false, false, "AND")).isFalse();
    }
    
    @Test
    @DisplayName("Should evaluate logical OR correctly")
    void testLogicalOr() {
        assertThat(Operators.evaluateLogical(true, true, "OR")).isTrue();
        assertThat(Operators.evaluateLogical(true, false, "OR")).isTrue();
        assertThat(Operators.evaluateLogical(false, true, "OR")).isTrue();
        assertThat(Operators.evaluateLogical(false, false, "OR")).isFalse();
    }
    
    @Test
    @DisplayName("Should evaluate logical NOT correctly")
    void testLogicalNot() {
        assertThat(Operators.evaluateLogical(true, false, "NOT")).isFalse();
        assertThat(Operators.evaluateLogical(false, true, "NOT")).isTrue();
    }
    
    @Test
    @DisplayName("Should handle case-insensitive logical operators")
    void testLogicalOperatorsCaseInsensitive() {
        assertThat(Operators.evaluateLogical(true, true, "and")).isTrue();
        assertThat(Operators.evaluateLogical(true, true, "And")).isTrue();
        assertThat(Operators.evaluateLogical(true, false, "or")).isTrue();
        assertThat(Operators.evaluateLogical(true, false, "not")).isFalse();
    }
    
    @Test
    @DisplayName("Should throw exception for invalid logical operator")
    void testInvalidLogicalOperator() {
        assertThatThrownBy(() -> Operators.evaluateLogical(true, false, "XOR"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid operator");
    }
    
    @Test
    @DisplayName("Should handle zero in calculations")
    void testZeroInCalculations() {
        assertThat(Operators.calculate(0, 5, '+')).isEqualTo(5);
        assertThat(Operators.calculate(5, 0, '+')).isEqualTo(5);
        assertThat(Operators.calculate(0, 5, '*')).isEqualTo(0);
        assertThat(Operators.calculate(5, 0, '*')).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should handle large numbers")
    void testLargeNumbers() {
        assertThat(Operators.calculate(1000000, 2000000, '+')).isEqualTo(3000000);
        assertThat(Operators.calculate(1000000, 500000, '-')).isEqualTo(500000);
        assertThat(Operators.calculate(1000, 1000, '*')).isEqualTo(1000000);
    }
}