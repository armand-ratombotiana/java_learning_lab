package com.algo.lab08;

import java.util.Arrays;

public class StringExample {
    public static void main(String[] args) {
        System.out.println("=== String Algorithms Demo ===\n");

        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";

        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);

        System.out.println("\nKMP matches: " + StringAlgorithms.kmpSearch(text, pattern));
        System.out.println("Rabin-Karp matches: " + StringAlgorithms.rabinKarp(text, pattern));
        System.out.println("Z-algorithm matches: " + StringAlgorithms.zSearch(text, pattern));

        System.out.println("\nZ-array of \"aaabaaab\": " +
            Arrays.toString(StringAlgorithms.zAlgorithm("aaabaaab")));

        String s = "babad";
        System.out.println("Longest palindromic substring of \"" + s + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s) + "\"");

        String s2 = "cbbd";
        System.out.println("Longest palindromic substring of \"" + s2 + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s2) + "\"");

        String s3 = "forgeeksskeegfor";
        System.out.println("Longest palindromic substring of \"" + s3 + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s3) + "\"");
    }
}