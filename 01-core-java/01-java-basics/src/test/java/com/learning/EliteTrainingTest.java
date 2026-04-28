package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Comprehensive test suite for EliteTraining exercises.
 * Tests all solutions for correctness, edge cases, and performance.
 *
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Elite Training Test Suite")
class EliteTrainingTest {

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: FOUNDATION EXERCISES TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Exercise 1: Reverse String - Basic Cases")
    void testReverseString_BasicCases() {
        assertEquals("olleh", EliteTraining.reverseString("hello"));
        assertEquals("dlroW olleH", EliteTraining.reverseString("Hello World"));
        assertEquals("a", EliteTraining.reverseString("a"));
    }

    @Test
    @DisplayName("Exercise 1: Reverse String - Edge Cases")
    void testReverseString_EdgeCases() {
        assertEquals("", EliteTraining.reverseString(""));
        assertNull(EliteTraining.reverseString(null));
        assertEquals("  ", EliteTraining.reverseString("  "));
    }

    @Test
    @DisplayName("Exercise 2: Is Palindrome - Valid Palindromes")
    void testIsPalindrome_ValidPalindromes() {
        assertTrue(EliteTraining.isPalindrome("racecar"));
        assertTrue(EliteTraining.isPalindrome("madam"));
        assertTrue(EliteTraining.isPalindrome("a"));
        assertTrue(EliteTraining.isPalindrome(""));
    }

    @Test
    @DisplayName("Exercise 2: Is Palindrome - Not Palindromes")
    void testIsPalindrome_NotPalindromes() {
        assertFalse(EliteTraining.isPalindrome("hello"));
        assertFalse(EliteTraining.isPalindrome("world"));
        assertFalse(EliteTraining.isPalindrome("ab"));
    }

    @Test
    @DisplayName("Exercise 2: Is Palindrome - Case Insensitive")
    void testIsPalindrome_CaseInsensitive() {
        assertTrue(EliteTraining.isPalindrome("RaceCar"));
        assertTrue(EliteTraining.isPalindrome("MadAm"));
    }

    @Test
    @DisplayName("Exercise 3: FizzBuzz - Multiples of 3")
    void testFizzBuzz_MultiplesOfThree() {
        assertEquals("Fizz", EliteTraining.fizzBuzz(3));
        assertEquals("Fizz", EliteTraining.fizzBuzz(6));
        assertEquals("Fizz", EliteTraining.fizzBuzz(9));
    }

    @Test
    @DisplayName("Exercise 3: FizzBuzz - Multiples of 5")
    void testFizzBuzz_MultiplesOfFive() {
        assertEquals("Buzz", EliteTraining.fizzBuzz(5));
        assertEquals("Buzz", EliteTraining.fizzBuzz(10));
        assertEquals("Buzz", EliteTraining.fizzBuzz(20));
    }

    @Test
    @DisplayName("Exercise 3: FizzBuzz - Multiples of Both")
    void testFizzBuzz_MultiplesOfBoth() {
        assertEquals("FizzBuzz", EliteTraining.fizzBuzz(15));
        assertEquals("FizzBuzz", EliteTraining.fizzBuzz(30));
        assertEquals("FizzBuzz", EliteTraining.fizzBuzz(45));
    }

    @Test
    @DisplayName("Exercise 3: FizzBuzz - Regular Numbers")
    void testFizzBuzz_RegularNumbers() {
        assertEquals("1", EliteTraining.fizzBuzz(1));
        assertEquals("2", EliteTraining.fizzBuzz(2));
        assertEquals("7", EliteTraining.fizzBuzz(7));
    }

    @Test
    @DisplayName("Exercise 4: Find Max - Normal Array")
    void testFindMax_NormalArray() {
        assertEquals(9, EliteTraining.findMax(new int[]{3, 7, 2, 9, 4, 1, 8}));
        assertEquals(100, EliteTraining.findMax(new int[]{100, 50, 25}));
        assertEquals(5, EliteTraining.findMax(new int[]{5}));
    }

    @Test
    @DisplayName("Exercise 4: Find Max - Negative Numbers")
    void testFindMax_NegativeNumbers() {
        assertEquals(-1, EliteTraining.findMax(new int[]{-5, -3, -1, -10}));
        assertEquals(0, EliteTraining.findMax(new int[]{-5, 0, -3}));
    }

    @Test
    @DisplayName("Exercise 4: Find Max - Edge Cases")
    void testFindMax_EdgeCases() {
        assertThrows(IllegalArgumentException.class, () ->
            EliteTraining.findMax(null));
        assertThrows(IllegalArgumentException.class, () ->
            EliteTraining.findMax(new int[]{}));
    }

    @Test
    @DisplayName("Exercise 5: Count Vowels - Normal Cases")
    void testCountVowels_NormalCases() {
        assertEquals(3, EliteTraining.countVowels("Hello World"));
        assertEquals(5, EliteTraining.countVowels("aeiou"));
        assertEquals(0, EliteTraining.countVowels("bcdfg"));
    }

    @Test
    @DisplayName("Exercise 5: Count Vowels - Edge Cases")
    void testCountVowels_EdgeCases() {
        assertEquals(0, EliteTraining.countVowels(""));
        assertEquals(0, EliteTraining.countVowels(null));
        assertEquals(5, EliteTraining.countVowels("AEIOU"));
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: INTERMEDIATE EXERCISES TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Exercise 6: Two Sum - Valid Pairs")
    void testTwoSum_ValidPairs() {
        int[] result = EliteTraining.twoSum(new int[]{2, 7, 11, 15}, 9);
        assertArrayEquals(new int[]{0, 1}, result);

        result = EliteTraining.twoSum(new int[]{3, 2, 4}, 6);
        assertArrayEquals(new int[]{1, 2}, result);
    }

    @Test
    @DisplayName("Exercise 6: Two Sum - No Solution")
    void testTwoSum_NoSolution() {
        int[] result = EliteTraining.twoSum(new int[]{1, 2, 3}, 10);
        assertArrayEquals(new int[]{-1, -1}, result);
    }

    @Test
    @DisplayName("Exercise 6: Two Sum - Same Element Twice")
    void testTwoSum_SameElementTwice() {
        int[] result = EliteTraining.twoSum(new int[]{3, 3}, 6);
        assertArrayEquals(new int[]{0, 1}, result);
    }

    @Test
    @DisplayName("Exercise 7: Remove Duplicates - Normal Cases")
    void testRemoveDuplicates_NormalCases() {
        int[] nums = {1, 1, 2, 2, 3};
        int newLength = EliteTraining.removeDuplicates(nums);
        assertEquals(3, newLength);
        assertArrayEquals(new int[]{1, 2, 3, 2, 3}, nums);

        nums = new int[]{1, 1, 1, 1};
        newLength = EliteTraining.removeDuplicates(nums);
        assertEquals(1, newLength);
    }

    @Test
    @DisplayName("Exercise 7: Remove Duplicates - Edge Cases")
    void testRemoveDuplicates_EdgeCases() {
        assertEquals(0, EliteTraining.removeDuplicates(null));
        assertEquals(0, EliteTraining.removeDuplicates(new int[]{}));

        int[] nums = {1};
        assertEquals(1, EliteTraining.removeDuplicates(nums));
    }

    @Test
    @DisplayName("Exercise 8: Anagram Check - Valid Anagrams")
    void testAreAnagrams_ValidAnagrams() {
        assertTrue(EliteTraining.areAnagrams("listen", "silent"));
        assertTrue(EliteTraining.areAnagrams("evil", "vile"));
        assertTrue(EliteTraining.areAnagrams("a", "a"));
    }

    @Test
    @DisplayName("Exercise 8: Anagram Check - Not Anagrams")
    void testAreAnagrams_NotAnagrams() {
        assertFalse(EliteTraining.areAnagrams("hello", "world"));
        assertFalse(EliteTraining.areAnagrams("listen", "listening"));
        assertFalse(EliteTraining.areAnagrams("a", "b"));
    }

    @Test
    @DisplayName("Exercise 8: Anagram Check - Case Insensitive")
    void testAreAnagrams_CaseInsensitive() {
        assertTrue(EliteTraining.areAnagrams("Listen", "Silent"));
        assertTrue(EliteTraining.areAnagrams("EVIL", "vile"));
    }

    @Test
    @DisplayName("Exercise 8: Anagram Check - Edge Cases")
    void testAreAnagrams_EdgeCases() {
        assertFalse(EliteTraining.areAnagrams(null, "test"));
        assertFalse(EliteTraining.areAnagrams("test", null));
        assertTrue(EliteTraining.areAnagrams("", ""));
    }

    @Test
    @DisplayName("Exercise 9: Valid Parentheses - Valid Cases")
    void testValidParentheses_ValidCases() {
        assertTrue(EliteTraining.isValidParentheses("()"));
        assertTrue(EliteTraining.isValidParentheses("()[]{}"));
        assertTrue(EliteTraining.isValidParentheses("{[]}"));
        assertTrue(EliteTraining.isValidParentheses("((()))"));
    }

    @Test
    @DisplayName("Exercise 9: Valid Parentheses - Invalid Cases")
    void testValidParentheses_InvalidCases() {
        assertFalse(EliteTraining.isValidParentheses("(]"));
        assertFalse(EliteTraining.isValidParentheses("([)]"));
        assertFalse(EliteTraining.isValidParentheses("((()"));
        assertFalse(EliteTraining.isValidParentheses(")"));
    }

    @Test
    @DisplayName("Exercise 9: Valid Parentheses - Edge Cases")
    void testValidParentheses_EdgeCases() {
        assertFalse(EliteTraining.isValidParentheses(null));
        assertFalse(EliteTraining.isValidParentheses("("));
        assertTrue(EliteTraining.isValidParentheses(""));
    }

    @Test
    @DisplayName("Exercise 10: First Non-Repeating Char - Normal Cases")
    void testFirstNonRepeatingChar_NormalCases() {
        assertEquals('l', EliteTraining.firstNonRepeatingChar("leetcode"));
        assertEquals('c', EliteTraining.firstNonRepeatingChar("aabbcde")); // 'c' is the first non-repeating
        assertEquals('a', EliteTraining.firstNonRepeatingChar("a"));
    }

    @Test
    @DisplayName("Exercise 10: First Non-Repeating Char - All Repeating")
    void testFirstNonRepeatingChar_AllRepeating() {
        assertEquals('\0', EliteTraining.firstNonRepeatingChar("aabbcc"));
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: ADVANCED EXERCISES TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Exercise 11: Longest Substring - Normal Cases")
    void testLongestSubstring_NormalCases() {
        assertEquals(3, EliteTraining.lengthOfLongestSubstring("abcabcbb"));
        assertEquals(1, EliteTraining.lengthOfLongestSubstring("bbbbb"));
        assertEquals(3, EliteTraining.lengthOfLongestSubstring("pwwkew"));
    }

    @Test
    @DisplayName("Exercise 11: Longest Substring - Edge Cases")
    void testLongestSubstring_EdgeCases() {
        assertEquals(0, EliteTraining.lengthOfLongestSubstring(""));
        assertEquals(0, EliteTraining.lengthOfLongestSubstring(null));
        assertEquals(1, EliteTraining.lengthOfLongestSubstring("a"));
        assertEquals(2, EliteTraining.lengthOfLongestSubstring("au"));
    }

    @Test
    @DisplayName("Exercise 11: Longest Substring - No Repeats")
    void testLongestSubstring_NoRepeats() {
        assertEquals(5, EliteTraining.lengthOfLongestSubstring("abcde"));
    }

    @Test
    @DisplayName("Exercise 12: Rotate Array - Normal Cases")
    void testRotateArray_NormalCases() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7};
        EliteTraining.rotateArray(nums, 3);
        assertArrayEquals(new int[]{5, 6, 7, 1, 2, 3, 4}, nums);

        nums = new int[]{1, 2};
        EliteTraining.rotateArray(nums, 1);
        assertArrayEquals(new int[]{2, 1}, nums);
    }

    @Test
    @DisplayName("Exercise 12: Rotate Array - K Greater Than Length")
    void testRotateArray_KGreaterThanLength() {
        int[] nums = {1, 2, 3};
        EliteTraining.rotateArray(nums, 4); // Same as k=1
        assertArrayEquals(new int[]{3, 1, 2}, nums);
    }

    @Test
    @DisplayName("Exercise 12: Rotate Array - Edge Cases")
    void testRotateArray_EdgeCases() {
        int[] nums = {1};
        EliteTraining.rotateArray(nums, 5);
        assertArrayEquals(new int[]{1}, nums);

        EliteTraining.rotateArray(null, 3); // Should not throw
        EliteTraining.rotateArray(new int[]{}, 3); // Should not throw
    }

    @Test
    @DisplayName("Exercise 13: Merge Sorted Arrays - Normal Cases")
    void testMergeSortedArrays_NormalCases() {
        int[] arr1 = {1, 3, 5, 7};
        int[] arr2 = {2, 4, 6, 8};
        int[] result = EliteTraining.mergeSortedArrays(arr1, arr2);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, result);
    }

    @Test
    @DisplayName("Exercise 13: Merge Sorted Arrays - Different Sizes")
    void testMergeSortedArrays_DifferentSizes() {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {4, 5, 6, 7, 8};
        int[] result = EliteTraining.mergeSortedArrays(arr1, arr2);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, result);
    }

    @Test
    @DisplayName("Exercise 13: Merge Sorted Arrays - Empty Arrays")
    void testMergeSortedArrays_EmptyArrays() {
        int[] arr1 = {};
        int[] arr2 = {1, 2, 3};
        int[] result = EliteTraining.mergeSortedArrays(arr1, arr2);
        assertArrayEquals(new int[]{1, 2, 3}, result);

        arr1 = new int[]{4, 5};
        arr2 = new int[]{};
        result = EliteTraining.mergeSortedArrays(arr1, arr2);
        assertArrayEquals(new int[]{4, 5}, result);
    }

    @Test
    @DisplayName("Exercise 13: Merge Sorted Arrays - Duplicate Elements")
    void testMergeSortedArrays_Duplicates() {
        int[] arr1 = {1, 3, 5};
        int[] arr2 = {3, 5, 7};
        int[] result = EliteTraining.mergeSortedArrays(arr1, arr2);
        assertArrayEquals(new int[]{1, 3, 3, 5, 5, 7}, result);
    }

    // ═══════════════════════════════════════════════════════════════
    // PERFORMANCE TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Performance: Two Sum with Large Array")
    void testTwoSum_Performance() {
        int[] largeArray = new int[10000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i;
        }

        long start = System.nanoTime();
        int[] result = EliteTraining.twoSum(largeArray, 19997); // Sum of 9998 + 9999
        long end = System.nanoTime();

        assertArrayEquals(new int[]{9998, 9999}, result);

        // Should complete in less than 50ms for 10k elements
        long durationMs = (end - start) / 1_000_000;
        assertTrue(durationMs < 50, "Performance issue: took " + durationMs + "ms");
    }

    @Test
    @DisplayName("Performance: Longest Substring with Large String")
    void testLongestSubstring_Performance() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append((char)('a' + (i % 26)));
        }
        String largeString = sb.toString();

        long start = System.nanoTime();
        int length = EliteTraining.lengthOfLongestSubstring(largeString);
        long end = System.nanoTime();

        assertEquals(26, length); // All lowercase letters

        // Should complete in less than 50ms
        long durationMs = (end - start) / 1_000_000;
        assertTrue(durationMs < 50, "Performance issue: took " + durationMs + "ms");
    }
}
