# Interview Questions: String Handling

## Company-Specific Focus

### Google
- String immutability: why it is crucial for multithreading, security, and String pool
- String interning: .intern() and its impact on memory footprint
- String deduplication in G1 GC: how G1 finds duplicates

### Microsoft
- String concatenation: Java + vs C# StringBuilder; How the Java compiler handles it
- StringBuilder vs StringBuffer: thread safety (or lack thereof) and the need for it

### Amazon
- StringBuilder for message construction: especially in the processing pipeline
- String.split() allocation: causing many string allocations in the log processing system
- Charset encoding: how to work with strings in the NIO world

### Meta
- Substring: does substring create a new string? The answer in Java 7+
- Using the pattern and Matcher instead of String.matches: performance

### Apple
- String storage in Java 9+: compact strings, one byte per Latin-1 char
- CharSequence interface: what it gives to String, StringBuilder, and StringBuffer

### Oracle
- String in JLS 4.3.3: how String is a unique primitive class
- String constant pool: in the class file at the constant pool
- The modernization of String: compact Strings in Java 9 the coder field

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 242 Valid Anagram | Easy | Amazon, Apple, Google, Microsoft | Character counting using int[26] |
| 125 Valid Palindrome | Easy | Google, Apple | Two-pointer, Character.isLetterOrDigit |
| 3 Longest Substring Without Repeating Characters | Medium | Amazon, Apple, Google | Sliding window, HashMap of char positions |
| 5 Longest Palindromic Substring | Medium | Amazon, Google, Apple | Expand from center vs DP |
| 49 Group Anagrams | Medium | Amazon, Google, Apple | char count key |
| 76 Minimum Window Substring | Hard | Amazon, Google, Apple | Sliding window with frequency tracking |
| 647 Palindromic Substrings | Medium | Amazon, Apple | Center expansion for palindromes |
| 28 Find the Index of First Occurrence in String | Easy | Amazon | KMP or built-in indexOf |
| 459 Repeated Substring Pattern | Easy | Amazon, Microsoft | Mathematical checking of repeating patterns |
| 227 Basic Calculator II | Medium | Google, Amazon | String parsing with operator stack |

## Real Production Scenarios
- **LinkedIn**: String.split() was used on a large file processing path generating millions of arrays per minute; converted to a char-by-char parser to reduce memory
- **Cloudflare**: A log processing system hit OOM because of using + concatenation on very large strings inside a loop; conversion to StringBuilder
- **Uber**: Using .intern() on user email addresses in the session object — caused native OOM in the string table. Later replaced with WeakReference cache

## Interview Patterns & Tips
- **Substring memory leak**: In Java 6 and earlier, substring shared the underlying char[]. In Java 7+, substring creates a new array. This fix solved memory leak but increased allocation.
- **Gotcha**: The + in a loop concatenation creates a new StringBuilder each iteration — O(n^2) allocations
- **Look for reverse/swap/palindrome**: Two pointers and char arrays

## Deep Dive Questions
- **JVM**: What is the internal representation of String from Java 9 onwards?
- **String constant pool**: Where is it managed in JVM - in the heap? Metaspace?
- **Deduplication**: How does the G1 deduplication work?
- **Hashing**: How is String's hashCode() computed? Why is it cached?
- **Security**: Why is String final and how can char[] be erased?