package com.learning;

/**
 * Demonstrates Java String class including creation, manipulation,
 * comparison, and common String methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class StringsDemo {
    
    /**
     * Demonstrates all String concepts in Java.
     */
    public static void demonstrateStrings() {
        demonstrateStringCreation();
        demonstrateStringMethods();
        demonstrateStringComparison();
        demonstrateStringManipulation();
        demonstrateStringBuilder();
        demonstrateStringFormatting();
    }
    
    /**
     * Demonstrates different ways to create strings.
     */
    private static void demonstrateStringCreation() {
        System.out.println("String Creation:");
        
        // String literal (stored in String pool)
        String str1 = "Hello";
        String str2 = "Hello";
        
        // Using new keyword (creates new object in heap)
        String str3 = new String("Hello");
        String str4 = new String("Hello");
        
        // String pool demonstration
        System.out.println("str1 == str2 (same reference): " + (str1 == str2)); // true
        System.out.println("str3 == str4 (different references): " + (str3 == str4)); // false
        System.out.println("str1.equals(str3) (same content): " + str1.equals(str3)); // true
        
        // From char array
        char[] chars = {'H', 'e', 'l', 'l', 'o'};
        String str5 = new String(chars);
        System.out.println("From char array: " + str5);
        
        // From byte array
        byte[] bytes = {72, 101, 108, 108, 111};
        String str6 = new String(bytes);
        System.out.println("From byte array: " + str6);
    }
    
    /**
     * Demonstrates common String methods.
     */
    private static void demonstrateStringMethods() {
        System.out.println("\nString Methods:");
        
        String str = "Hello World";
        
        // Length
        System.out.println("Length: " + str.length());
        
        // Character at index
        System.out.println("Character at index 0: " + str.charAt(0));
        System.out.println("Character at index 6: " + str.charAt(6));
        
        // Substring
        System.out.println("Substring(0, 5): " + str.substring(0, 5));
        System.out.println("Substring(6): " + str.substring(6));
        
        // Index of
        System.out.println("Index of 'o': " + str.indexOf('o'));
        System.out.println("Last index of 'o': " + str.lastIndexOf('o'));
        System.out.println("Index of 'World': " + str.indexOf("World"));
        
        // Contains
        System.out.println("Contains 'World': " + str.contains("World"));
        System.out.println("Contains 'Java': " + str.contains("Java"));
        
        // Starts with / Ends with
        System.out.println("Starts with 'Hello': " + str.startsWith("Hello"));
        System.out.println("Ends with 'World': " + str.endsWith("World"));
        
        // Empty check
        String empty = "";
        System.out.println("Empty string is empty: " + empty.isEmpty());
        System.out.println("Empty string is blank: " + empty.isBlank());
        
        String blank = "   ";
        System.out.println("Blank string is empty: " + blank.isEmpty());
        System.out.println("Blank string is blank: " + blank.isBlank());
    }
    
    /**
     * Demonstrates string comparison methods.
     */
    private static void demonstrateStringComparison() {
        System.out.println("\nString Comparison:");
        
        String str1 = "Hello";
        String str2 = "Hello";
        String str3 = "hello";
        String str4 = "World";
        
        // equals()
        System.out.println("str1.equals(str2): " + str1.equals(str2));
        System.out.println("str1.equals(str3): " + str1.equals(str3));
        
        // equalsIgnoreCase()
        System.out.println("str1.equalsIgnoreCase(str3): " + str1.equalsIgnoreCase(str3));
        
        // compareTo()
        System.out.println("str1.compareTo(str2): " + str1.compareTo(str2)); // 0 (equal)
        System.out.println("str1.compareTo(str4): " + str1.compareTo(str4)); // negative (str1 < str4)
        System.out.println("str4.compareTo(str1): " + str4.compareTo(str1)); // positive (str4 > str1)
        
        // compareToIgnoreCase()
        System.out.println("str1.compareToIgnoreCase(str3): " + str1.compareToIgnoreCase(str3));
    }
    
    /**
     * Demonstrates string manipulation methods.
     */
    private static void demonstrateStringManipulation() {
        System.out.println("\nString Manipulation:");
        
        String str = "  Hello World  ";
        
        // Trim (removes leading and trailing whitespace)
        System.out.println("Original: '" + str + "'");
        System.out.println("Trimmed: '" + str.trim() + "'");
        System.out.println("Strip: '" + str.strip() + "'"); // Java 11+
        
        // Case conversion
        System.out.println("Upper case: " + str.toUpperCase());
        System.out.println("Lower case: " + str.toLowerCase());
        
        // Replace
        String text = "Hello World";
        System.out.println("Replace 'World' with 'Java': " + text.replace("World", "Java"));
        System.out.println("Replace 'o' with '0': " + text.replace('o', '0'));
        System.out.println("Replace all 'l' with 'L': " + text.replaceAll("l", "L"));
        System.out.println("Replace first 'l' with 'L': " + text.replaceFirst("l", "L"));
        
        // Split
        String csv = "apple,banana,orange,mango";
        String[] fruits = csv.split(",");
        System.out.println("Split by comma:");
        for (String fruit : fruits) {
            System.out.println("  - " + fruit);
        }
        
        // Join (Java 8+)
        String joined = String.join(", ", fruits);
        System.out.println("Joined: " + joined);
        
        // Repeat (Java 11+)
        System.out.println("Repeat 'Ha' 3 times: " + "Ha".repeat(3));
        
        // Concat
        String first = "Hello";
        String second = "World";
        System.out.println("Concat: " + first.concat(" ").concat(second));
    }
    
    /**
     * Demonstrates StringBuilder for efficient string manipulation.
     */
    private static void demonstrateStringBuilder() {
        System.out.println("\nStringBuilder:");
        
        // StringBuilder is mutable and more efficient for string manipulation
        StringBuilder sb = new StringBuilder("Hello");
        
        // Append
        sb.append(" World");
        System.out.println("After append: " + sb);
        
        // Insert
        sb.insert(5, ",");
        System.out.println("After insert: " + sb);
        
        // Delete
        sb.delete(5, 6); // Remove comma
        System.out.println("After delete: " + sb);
        
        // Replace
        sb.replace(0, 5, "Hi");
        System.out.println("After replace: " + sb);
        
        // Reverse
        sb.reverse();
        System.out.println("After reverse: " + sb);
        sb.reverse(); // Reverse back
        
        // Capacity and length
        System.out.println("Length: " + sb.length());
        System.out.println("Capacity: " + sb.capacity());
        
        // Convert to String
        String result = sb.toString();
        System.out.println("Final string: " + result);
        
        // Performance comparison
        System.out.println("\nPerformance Comparison:");
        long startTime, endTime;
        
        // String concatenation (slow)
        startTime = System.nanoTime();
        String str = "";
        for (int i = 0; i < 1000; i++) {
            str += "a";
        }
        endTime = System.nanoTime();
        System.out.println("String concatenation time: " + (endTime - startTime) + " ns");
        
        // StringBuilder (fast)
        startTime = System.nanoTime();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            builder.append("a");
        }
        endTime = System.nanoTime();
        System.out.println("StringBuilder time: " + (endTime - startTime) + " ns");
    }
    
    /**
     * Demonstrates string formatting.
     */
    private static void demonstrateStringFormatting() {
        System.out.println("\nString Formatting:");
        
        // String.format()
        String name = "John";
        int age = 30;
        double salary = 50000.50;
        
        String formatted = String.format("Name: %s, Age: %d, Salary: $%.2f", name, age, salary);
        System.out.println(formatted);
        
        // printf() - prints directly
        System.out.printf("Name: %s, Age: %d, Salary: $%.2f%n", name, age, salary);
        
        // Format specifiers
        System.out.println("\nFormat Specifiers:");
        System.out.printf("Integer: %d%n", 42);
        System.out.printf("Float: %.2f%n", 3.14159);
        System.out.printf("String: %s%n", "Hello");
        System.out.printf("Character: %c%n", 'A');
        System.out.printf("Boolean: %b%n", true);
        System.out.printf("Hexadecimal: %x%n", 255);
        System.out.printf("Scientific: %e%n", 12345.6789);
        
        // Width and alignment
        System.out.println("\nWidth and Alignment:");
        System.out.printf("|%10s|%n", "Right"); // Right-aligned, width 10
        System.out.printf("|%-10s|%n", "Left"); // Left-aligned, width 10
        System.out.printf("|%010d|%n", 42); // Zero-padded, width 10
        
        // Text blocks (Java 15+)
        System.out.println("\nText Blocks:");
        String json = """
                {
                    "name": "John",
                    "age": 30,
                    "city": "New York"
                }
                """;
        System.out.println(json);
    }
    
    /**
     * Checks if a string is a palindrome.
     * 
     * @param str the string to check
     * @return true if palindrome, false otherwise
     */
    public static boolean isPalindrome(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        String cleaned = str.toLowerCase().replaceAll("[^a-z0-9]", "");
        int left = 0;
        int right = cleaned.length() - 1;
        
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    /**
     * Counts the occurrences of a character in a string.
     * 
     * @param str the string to search
     * @param ch the character to count
     * @return the count
     */
    public static int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Reverses a string.
     * 
     * @param str the string to reverse
     * @return the reversed string
     */
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
    
    /**
     * Capitalizes the first letter of each word.
     * 
     * @param str the string to capitalize
     * @return the capitalized string
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}