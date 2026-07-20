package com.javalab.02;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {
    private MainImplementation util;
    @BeforeEach void setUp() { util = new MainImplementation(); }
    @ParameterizedTest @ValueSource(strings = {"racecar", "level", "radar"})
    @DisplayName("Should detect palindromes") void testPalindrome(String s) { assertTrue(util.isPalindrome(s)); }
    @ParameterizedTest @CsvSource({"0,0", "1,1", "2,1", "3,2", "4,3", "5,5", "6,8"})
    @DisplayName("Fibonacci sequence") void testFibonacci(int n, int expected) { assertEquals(expected, util.fibonacci(n)); }
    @Test @DisplayName("Null is not palindrome") void testNull() { assertFalse(util.isPalindrome(null)); }
}
