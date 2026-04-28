package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for StringsDemo class.
 * Tests all string operations and utility methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Strings Demo Tests")
class StringsDemoTest {
    
    @Test
    @DisplayName("Should demonstrate strings without errors")
    void testDemonstrateStrings() {
        assertThatCode(() -> StringsDemo.demonstrateStrings())
            .doesNotThrowAnyException();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"racecar", "madam", "A man a plan a canal Panama", "Was it a car or a cat I saw"})
    @DisplayName("Should identify palindromes correctly")
    void testIsPalindrome(String input) {
        assertThat(StringsDemo.isPalindrome(input)).isTrue();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"hello", "world", "java", "programming"})
    @DisplayName("Should identify non-palindromes correctly")
    void testIsNotPalindrome(String input) {
        assertThat(StringsDemo.isPalindrome(input)).isFalse();
    }
    
    @Test
    @DisplayName("Should return false for null string in palindrome check")
    void testIsPalindromeNull() {
        assertThat(StringsDemo.isPalindrome(null)).isFalse();
    }
    
    @Test
    @DisplayName("Should return false for empty string in palindrome check")
    void testIsPalindromeEmpty() {
        assertThat(StringsDemo.isPalindrome("")).isFalse();
    }
    
    @Test
    @DisplayName("Should handle single character palindrome")
    void testIsPalindromeSingleChar() {
        assertThat(StringsDemo.isPalindrome("a")).isTrue();
    }
    
    @ParameterizedTest
    @CsvSource({
        "hello, l, 2",
        "programming, m, 2",
        "java, a, 2",
        "test, t, 2",
        "hello, o, 1"
    })
    @DisplayName("Should count character occurrences correctly")
    void testCountOccurrences(String str, char ch, int expected) {
        assertThat(StringsDemo.countOccurrences(str, ch)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Should return zero for character not in string")
    void testCountOccurrencesNotFound() {
        assertThat(StringsDemo.countOccurrences("hello", 'z')).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should count all occurrences in string with all same characters")
    void testCountOccurrencesAllSame() {
        assertThat(StringsDemo.countOccurrences("aaaa", 'a')).isEqualTo(4);
    }
    
    @Test
    @DisplayName("Should handle case-sensitive counting")
    void testCountOccurrencesCaseSensitive() {
        assertThat(StringsDemo.countOccurrences("Hello", 'h')).isEqualTo(0);
        assertThat(StringsDemo.countOccurrences("Hello", 'H')).isEqualTo(1);
    }
    
    @ParameterizedTest
    @CsvSource({
        "hello, olleh",
        "java, avaj",
        "programming, gnimmargorp",
        "a, a",
        "'', ''"
    })
    @DisplayName("Should reverse strings correctly")
    void testReverse(String input, String expected) {
        assertThat(StringsDemo.reverse(input)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Should reverse string with spaces")
    void testReverseWithSpaces() {
        assertThat(StringsDemo.reverse("hello world")).isEqualTo("dlrow olleh");
    }
    
    @Test
    @DisplayName("Should reverse string with special characters")
    void testReverseWithSpecialChars() {
        assertThat(StringsDemo.reverse("hello!@#")).isEqualTo("#@!olleh");
    }
    
    @ParameterizedTest
    @CsvSource({
        "hello world, Hello World",
        "HELLO WORLD, Hello World",
        "hELLO wORLD, Hello World",
        "java programming, Java Programming"
    })
    @DisplayName("Should capitalize words correctly")
    void testCapitalizeWords(String input, String expected) {
        assertThat(StringsDemo.capitalizeWords(input)).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Should handle null string in capitalize")
    void testCapitalizeWordsNull() {
        assertThat(StringsDemo.capitalizeWords(null)).isNull();
    }
    
    @Test
    @DisplayName("Should handle empty string in capitalize")
    void testCapitalizeWordsEmpty() {
        assertThat(StringsDemo.capitalizeWords("")).isEmpty();
    }
    
    @Test
    @DisplayName("Should handle single word capitalize")
    void testCapitalizeWordsSingle() {
        assertThat(StringsDemo.capitalizeWords("hello")).isEqualTo("Hello");
    }
    
    @Test
    @DisplayName("Should handle multiple spaces in capitalize")
    void testCapitalizeWordsMultipleSpaces() {
        assertThat(StringsDemo.capitalizeWords("hello  world")).isEqualTo("Hello World");
    }
    
    @Test
    @DisplayName("Should handle leading and trailing spaces in capitalize")
    void testCapitalizeWordsLeadingTrailing() {
        assertThat(StringsDemo.capitalizeWords("  hello world  ")).isEqualTo("Hello World");
    }
    
    @Test
    @DisplayName("Should handle palindrome with numbers")
    void testIsPalindromeWithNumbers() {
        assertThat(StringsDemo.isPalindrome("12321")).isTrue();
        assertThat(StringsDemo.isPalindrome("12345")).isFalse();
    }
    
    @Test
    @DisplayName("Should handle palindrome with mixed case")
    void testIsPalindromeMixedCase() {
        assertThat(StringsDemo.isPalindrome("RaceCar")).isTrue();
        assertThat(StringsDemo.isPalindrome("Madam")).isTrue();
    }
    
    @Test
    @DisplayName("Should count occurrences in empty string")
    void testCountOccurrencesEmpty() {
        assertThat(StringsDemo.countOccurrences("", 'a')).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should reverse empty string")
    void testReverseEmpty() {
        assertThat(StringsDemo.reverse("")).isEmpty();
    }
    
    @Test
    @DisplayName("Should handle capitalize with single character words")
    void testCapitalizeWordsSingleChar() {
        assertThat(StringsDemo.capitalizeWords("a b c")).isEqualTo("A B C");
    }
    
    @Test
    @DisplayName("Should handle very long strings")
    void testLongStrings() {
        String longString = "a".repeat(1000);
        assertThat(StringsDemo.reverse(longString)).hasSize(1000);
        assertThat(StringsDemo.countOccurrences(longString, 'a')).isEqualTo(1000);
    }
    
    @Test
    @DisplayName("Should handle strings with only spaces")
    void testStringsWithOnlySpaces() {
        assertThat(StringsDemo.capitalizeWords("   ")).isEmpty();
    }
    
    @Test
    @DisplayName("Should handle palindrome with punctuation")
    void testIsPalindromeWithPunctuation() {
        assertThat(StringsDemo.isPalindrome("A man, a plan, a canal: Panama")).isTrue();
    }
}