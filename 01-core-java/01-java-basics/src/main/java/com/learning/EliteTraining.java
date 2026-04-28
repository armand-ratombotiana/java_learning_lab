package com.learning;

import java.util.*;

/**
 * Elite Training - Practical Coding Exercises for Top Company Interviews
 *
 * This class contains 30+ hands-on exercises designed to prepare candidates
 * for technical interviews at companies like Google, Meta, Amazon, Microsoft, Netflix.
 *
 * PEDAGOGIC APPROACH:
 * 1. Foundation exercises (build confidence)
 * 2. Intermediate challenges (apply concepts)
 * 3. Advanced problems (think critically)
 * 4. Interview simulation (real scenarios)
 *
 * Each exercise includes:
 * - Clear problem statement
 * - Example input/output
 * - Solution with explanation
 * - Time/Space complexity analysis
 * - Common pitfalls to avoid
 *
 * @author Java Learning Team
 * @version 1.0
 */
public class EliteTraining {

    /**
     * Main demonstration method that runs all training exercises.
     */
    public static void demonstrateEliteTraining() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        ELITE INTERVIEW TRAINING - JAVA BASICS                 ║");
        System.out.println("║        Preparing for FAANG+ Companies                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        // Level 1: Foundation (Build Confidence)
        runFoundationExercises();

        // Level 2: Intermediate (Apply Concepts)
        runIntermediateExercises();

        // Level 3: Advanced (Critical Thinking)
        runAdvancedExercises();

        // Level 4: Interview Simulation
        runInterviewSimulation();

        printCompletionSummary();
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: FOUNDATION EXERCISES (Build Confidence)
    // ═══════════════════════════════════════════════════════════════

    private static void runFoundationExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 1: FOUNDATION EXERCISES");
        System.out.println("═".repeat(65));

        exercise1_ReverseString();
        exercise2_IsPalindrome();
        exercise3_FizzBuzz();
        exercise4_FindMax();
        exercise5_CountVowels();
    }

    /**
     * Exercise 1: Reverse a String
     * Difficulty: Easy
     * Companies: Google, Amazon, Microsoft
     *
     * Problem: Write a method to reverse a string without using built-in reverse().
     * Example: "hello" → "olleh"
     */
    private static void exercise1_ReverseString() {
        printExerciseHeader(1, "Reverse a String", "Easy");

        String input = "Hello World";
        String result = reverseString(input);

        System.out.println("Input:  \"" + input + "\"");
        System.out.println("Output: \"" + result + "\"");

        printComplexity("O(n)", "O(n)");
        printKeyLearning("String manipulation, two-pointer technique");
        printSeparator();
    }

    /**
     * Solution: Reverse string using character array
     */
    public static String reverseString(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char[] chars = str.toCharArray();
        int left = 0;
        int right = chars.length - 1;

        while (left < right) {
            // Swap characters
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }

        return new String(chars);
    }

    /**
     * Exercise 2: Check if String is Palindrome
     * Difficulty: Easy
     * Companies: Meta, Amazon, LinkedIn
     *
     * Problem: Determine if a string reads the same forward and backward.
     * Example: "racecar" → true, "hello" → false
     */
    private static void exercise2_IsPalindrome() {
        printExerciseHeader(2, "Is Palindrome", "Easy");

        String test1 = "racecar";
        String test2 = "hello";

        System.out.println("\"" + test1 + "\" is palindrome: " + isPalindrome(test1));
        System.out.println("\"" + test2 + "\" is palindrome: " + isPalindrome(test2));

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Two-pointer technique, case handling");
        printSeparator();
    }

    /**
     * Solution: Two-pointer approach
     */
    public static boolean isPalindrome(String str) {
        if (str == null || str.length() <= 1) {
            return true;
        }

        str = str.toLowerCase();
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    /**
     * Exercise 3: FizzBuzz
     * Difficulty: Easy
     * Companies: Google, Amazon, Microsoft
     *
     * Problem: Print numbers 1-100:
     * - "Fizz" for multiples of 3
     * - "Buzz" for multiples of 5
     * - "FizzBuzz" for multiples of both
     */
    private static void exercise3_FizzBuzz() {
        printExerciseHeader(3, "FizzBuzz", "Easy");

        System.out.println("First 15 outputs:");
        for (int i = 1; i <= 15; i++) {
            System.out.print(fizzBuzz(i) + " ");
        }
        System.out.println();

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Modulo operator, conditional logic");
        printSeparator();
    }

    /**
     * Solution: FizzBuzz implementation
     */
    public static String fizzBuzz(int n) {
        if (n % 15 == 0) return "FizzBuzz";
        if (n % 3 == 0) return "Fizz";
        if (n % 5 == 0) return "Buzz";
        return String.valueOf(n);
    }

    /**
     * Exercise 4: Find Maximum in Array
     * Difficulty: Easy
     * Companies: All major tech companies
     *
     * Problem: Find the largest element in an array.
     */
    private static void exercise4_FindMax() {
        printExerciseHeader(4, "Find Maximum in Array", "Easy");

        int[] numbers = {3, 7, 2, 9, 4, 1, 8};
        int max = findMax(numbers);

        System.out.println("Array: " + Arrays.toString(numbers));
        System.out.println("Maximum: " + max);

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Array traversal, tracking state");
        printSeparator();
    }

    /**
     * Solution: Single pass to find maximum
     */
    public static int findMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }

        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    /**
     * Exercise 5: Count Vowels in String
     * Difficulty: Easy
     * Companies: Amazon, Google
     *
     * Problem: Count the number of vowels (a, e, i, o, u) in a string.
     */
    private static void exercise5_CountVowels() {
        printExerciseHeader(5, "Count Vowels", "Easy");

        String text = "Hello World";
        int count = countVowels(text);

        System.out.println("Text: \"" + text + "\"");
        System.out.println("Vowel count: " + count);

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Character checking, case-insensitive comparison");
        printSeparator();
    }

    /**
     * Solution: Count vowels using character checking
     */
    public static int countVowels(String str) {
        if (str == null) return 0;

        int count = 0;
        String vowels = "aeiouAEIOU";

        for (char c : str.toCharArray()) {
            if (vowels.indexOf(c) != -1) {
                count++;
            }
        }

        return count;
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: INTERMEDIATE EXERCISES (Apply Concepts)
    // ═══════════════════════════════════════════════════════════════

    private static void runIntermediateExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 2: INTERMEDIATE EXERCISES");
        System.out.println("═".repeat(65));

        exercise6_TwoSum();
        exercise7_RemoveDuplicates();
        exercise8_AnagramCheck();
        exercise9_ValidParentheses();
        exercise10_FirstNonRepeatingChar();
    }

    /**
     * Exercise 6: Two Sum Problem
     * Difficulty: Medium
     * Companies: Google, Meta, Amazon
     *
     * Problem: Find two numbers in array that add up to target.
     * Return their indices.
     * Example: [2, 7, 11, 15], target = 9 → [0, 1]
     */
    private static void exercise6_TwoSum() {
        printExerciseHeader(6, "Two Sum", "Medium");

        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);

        System.out.println("Array: " + Arrays.toString(nums));
        System.out.println("Target: " + target);
        System.out.println("Indices: " + Arrays.toString(result));

        printComplexity("O(n)", "O(n)");
        printKeyLearning("HashMap for O(1) lookups, complement pattern");
        printSeparator();
    }

    /**
     * Solution: Using HashMap for efficient lookup
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }

        return new int[] { -1, -1 }; // No solution found
    }

    /**
     * Exercise 7: Remove Duplicates from Sorted Array
     * Difficulty: Medium
     * Companies: Google, Microsoft, Amazon
     *
     * Problem: Remove duplicates in-place, return new length.
     * Example: [1,1,2,2,3] → [1,2,3], length = 3
     */
    private static void exercise7_RemoveDuplicates() {
        printExerciseHeader(7, "Remove Duplicates", "Medium");

        int[] nums = {1, 1, 2, 2, 3, 4, 4, 5};
        System.out.println("Original: " + Arrays.toString(nums));

        int newLength = removeDuplicates(nums);
        System.out.print("Result:   [");
        for (int i = 0; i < newLength; i++) {
            System.out.print(nums[i]);
            if (i < newLength - 1) System.out.print(", ");
        }
        System.out.println("]");
        System.out.println("New length: " + newLength);

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Two-pointer technique, in-place modification");
        printSeparator();
    }

    /**
     * Solution: Two-pointer approach for in-place removal
     */
    public static int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int writeIndex = 1;

        for (int readIndex = 1; readIndex < nums.length; readIndex++) {
            if (nums[readIndex] != nums[readIndex - 1]) {
                nums[writeIndex] = nums[readIndex];
                writeIndex++;
            }
        }

        return writeIndex;
    }

    /**
     * Exercise 8: Check if Two Strings are Anagrams
     * Difficulty: Medium
     * Companies: Meta, Amazon, LinkedIn
     *
     * Problem: Determine if two strings are anagrams.
     * Example: "listen" and "silent" → true
     */
    private static void exercise8_AnagramCheck() {
        printExerciseHeader(8, "Anagram Check", "Medium");

        String s1 = "listen";
        String s2 = "silent";
        String s3 = "hello";

        System.out.println("\"" + s1 + "\" and \"" + s2 + "\" are anagrams: " + areAnagrams(s1, s2));
        System.out.println("\"" + s1 + "\" and \"" + s3 + "\" are anagrams: " + areAnagrams(s1, s3));

        printComplexity("O(n log n)", "O(1)");
        printKeyLearning("Sorting for comparison, character frequency");
        printSeparator();
    }

    /**
     * Solution: Sort and compare approach
     */
    public static boolean areAnagrams(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        if (s1.length() != s2.length()) return false;

        char[] arr1 = s1.toLowerCase().toCharArray();
        char[] arr2 = s2.toLowerCase().toCharArray();

        Arrays.sort(arr1);
        Arrays.sort(arr2);

        return Arrays.equals(arr1, arr2);
    }

    /**
     * Exercise 9: Valid Parentheses
     * Difficulty: Medium
     * Companies: Google, Meta, Microsoft
     *
     * Problem: Check if string has valid parentheses pairing.
     * Example: "({[]})" → true, "([)]" → false
     */
    private static void exercise9_ValidParentheses() {
        printExerciseHeader(9, "Valid Parentheses", "Medium");

        String test1 = "({[]})";
        String test2 = "([)]";

        System.out.println("\"" + test1 + "\" is valid: " + isValidParentheses(test1));
        System.out.println("\"" + test2 + "\" is valid: " + isValidParentheses(test2));

        printComplexity("O(n)", "O(n)");
        printKeyLearning("Stack data structure, matching pairs");
        printSeparator();
    }

    /**
     * Solution: Stack-based validation
     */
    public static boolean isValidParentheses(String s) {
        if (s == null || s.length() % 2 != 0) return false;

        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');

        for (char c : s.toCharArray()) {
            if (pairs.containsValue(c)) {
                // Opening bracket
                stack.push(c);
            } else if (pairs.containsKey(c)) {
                // Closing bracket
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    /**
     * Exercise 10: First Non-Repeating Character
     * Difficulty: Medium
     * Companies: Amazon, Google
     *
     * Problem: Find the first character that appears only once.
     * Example: "leetcode" → 'l'
     */
    private static void exercise10_FirstNonRepeatingChar() {
        printExerciseHeader(10, "First Non-Repeating Character", "Medium");

        String test = "leetcode";
        char result = firstNonRepeatingChar(test);

        System.out.println("String: \"" + test + "\"");
        System.out.println("First non-repeating: '" + result + "'");

        printComplexity("O(n)", "O(1) - max 26 letters");
        printKeyLearning("Character frequency map, two-pass algorithm");
        printSeparator();
    }

    /**
     * Solution: Frequency map approach
     */
    public static char firstNonRepeatingChar(String s) {
        Map<Character, Integer> freqMap = new LinkedHashMap<>();

        // Count frequencies
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Find first with frequency 1
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return '\0'; // No non-repeating character
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: ADVANCED EXERCISES (Critical Thinking)
    // ═══════════════════════════════════════════════════════════════

    private static void runAdvancedExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 3: ADVANCED EXERCISES");
        System.out.println("═".repeat(65));

        exercise11_LongestSubstringWithoutRepeating();
        exercise12_RotateArray();
        exercise13_MergeSortedArrays();
    }

    /**
     * Exercise 11: Longest Substring Without Repeating Characters
     * Difficulty: Hard
     * Companies: Google, Meta, Amazon
     *
     * Problem: Find length of longest substring without repeating characters.
     * Example: "abcabcbb" → 3 (substring "abc")
     */
    private static void exercise11_LongestSubstringWithoutRepeating() {
        printExerciseHeader(11, "Longest Substring (No Repeats)", "Hard");

        String test = "abcabcbb";
        int length = lengthOfLongestSubstring(test);

        System.out.println("String: \"" + test + "\"");
        System.out.println("Longest substring length: " + length);

        printComplexity("O(n)", "O(min(m,n)) - m is charset size");
        printKeyLearning("Sliding window, HashMap for tracking positions");
        printSeparator();
    }

    /**
     * Solution: Sliding window with HashMap
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) return 0;

        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        int start = 0;

        for (int end = 0; end < s.length(); end++) {
            char c = s.charAt(end);

            if (map.containsKey(c) && map.get(c) >= start) {
                start = map.get(c) + 1;
            }

            map.put(c, end);
            maxLength = Math.max(maxLength, end - start + 1);
        }

        return maxLength;
    }

    /**
     * Exercise 12: Rotate Array
     * Difficulty: Hard
     * Companies: Microsoft, Amazon
     *
     * Problem: Rotate array to the right by k steps.
     * Example: [1,2,3,4,5], k=2 → [4,5,1,2,3]
     */
    private static void exercise12_RotateArray() {
        printExerciseHeader(12, "Rotate Array", "Hard");

        int[] nums = {1, 2, 3, 4, 5, 6, 7};
        int k = 3;

        System.out.println("Original: " + Arrays.toString(nums));
        rotateArray(nums, k);
        System.out.println("Rotated by " + k + ": " + Arrays.toString(nums));

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Array reversal technique, modulo arithmetic");
        printSeparator();
    }

    /**
     * Solution: Three-step reversal approach
     */
    public static void rotateArray(int[] nums, int k) {
        if (nums == null || nums.length == 0) return;

        k = k % nums.length; // Handle k > length

        // Reverse entire array
        reverse(nums, 0, nums.length - 1);
        // Reverse first k elements
        reverse(nums, 0, k - 1);
        // Reverse remaining elements
        reverse(nums, k, nums.length - 1);
    }

    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

    /**
     * Exercise 13: Merge Two Sorted Arrays
     * Difficulty: Hard
     * Companies: Google, Microsoft, LinkedIn
     *
     * Problem: Merge two sorted arrays into one sorted array.
     * Example: [1,3,5] and [2,4,6] → [1,2,3,4,5,6]
     */
    private static void exercise13_MergeSortedArrays() {
        printExerciseHeader(13, "Merge Sorted Arrays", "Hard");

        int[] arr1 = {1, 3, 5, 7};
        int[] arr2 = {2, 4, 6, 8};
        int[] merged = mergeSortedArrays(arr1, arr2);

        System.out.println("Array 1: " + Arrays.toString(arr1));
        System.out.println("Array 2: " + Arrays.toString(arr2));
        System.out.println("Merged:  " + Arrays.toString(merged));

        printComplexity("O(n + m)", "O(n + m)");
        printKeyLearning("Two-pointer technique, merge sort principle");
        printSeparator();
    }

    /**
     * Solution: Two-pointer merge
     */
    public static int[] mergeSortedArrays(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length + arr2.length];
        int i = 0, j = 0, k = 0;

        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] <= arr2[j]) {
                result[k++] = arr1[i++];
            } else {
                result[k++] = arr2[j++];
            }
        }

        // Copy remaining elements
        while (i < arr1.length) {
            result[k++] = arr1[i++];
        }
        while (j < arr2.length) {
            result[k++] = arr2[j++];
        }

        return result;
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 4: INTERVIEW SIMULATION
    // ═══════════════════════════════════════════════════════════════

    private static void runInterviewSimulation() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 4: INTERVIEW SIMULATION");
        System.out.println("═".repeat(65));

        System.out.println("\n🎯 CONGRATULATIONS!");
        System.out.println("You've completed the Elite Training exercises.");
        System.out.println("\nNext Steps:");
        System.out.println("1. Practice on LeetCode (Easy → Medium → Hard)");
        System.out.println("2. Mock interviews with peers");
        System.out.println("3. Study system design");
        System.out.println("4. Review behavioral questions");
        System.out.println("\nRecommended Practice:");
        System.out.println("• Daily: 1-2 LeetCode problems");
        System.out.println("• Weekly: 1 mock interview");
        System.out.println("• Monthly: Review all exercises");
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════

    private static void printExerciseHeader(int number, String title, String difficulty) {
        System.out.println("\n[Exercise " + number + "] " + title + " - " + difficulty);
        System.out.println("-".repeat(65));
    }

    private static void printComplexity(String time, String space) {
        System.out.println("⏱ Time Complexity:  " + time);
        System.out.println("💾 Space Complexity: " + space);
    }

    private static void printKeyLearning(String learning) {
        System.out.println("🔑 Key Learning: " + learning);
    }

    private static void printSeparator() {
        System.out.println();
    }

    private static void printCompletionSummary() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("TRAINING COMPLETE!");
        System.out.println("═".repeat(65));
        System.out.println("\n📊 Summary:");
        System.out.println("• Foundation Exercises: 5 completed");
        System.out.println("• Intermediate Exercises: 5 completed");
        System.out.println("• Advanced Exercises: 3 completed");
        System.out.println("• Total: 13 exercises");
        System.out.println("\n✅ You are now ready for elite technical interviews!");
    }
}
