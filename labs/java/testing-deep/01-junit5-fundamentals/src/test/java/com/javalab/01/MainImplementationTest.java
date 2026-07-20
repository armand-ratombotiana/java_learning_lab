package com.javalab.01;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    private MainImplementation calc;
    @BeforeEach void setUp() { calc = new MainImplementation(); }
    @Test @DisplayName("Add should return sum") void testAdd() { assertEquals(5, calc.add(2, 3)); }
    @Test @DisplayName("Divide by zero should throw") void testDivideByZero() { assertThrows(ArithmeticException.class, () -> calc.divide(10, 0)); }
    @Test @DisplayName("isPositive should return true for positive") void testIsPositive() { assertTrue(calc.isPositive(5)); }
    @Test @DisplayName("isPositive should return false for negative") void testIsNegative() { assertFalse(calc.isPositive(-1)); }
}
