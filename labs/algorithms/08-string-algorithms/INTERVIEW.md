# Interview Questions: String Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 3 Longest Substring Without Repeating Characters | Medium | Google, Meta, Amazon, Microsoft | Sliding window |
| LC 5 Longest Palindromic Substring | Medium | Google, Meta, Amazon, Microsoft | Expand around center |
| LC 424 Longest Repeating Character Replacement | Medium | Google, Meta | Sliding window |
| LC 76 Minimum Window Substring | Hard | Google, Meta, Amazon | Sliding window |
| LC 242 Valid Anagram | Easy | Google, Meta, Amazon, Microsoft | Frequency count |
| LC 647 Palindromic Substrings | Medium | Google, Meta, Amazon | Expand around center |
| LC 125 Valid Palindrome | Easy | Meta, Amazon, Apple | Two pointers |

## NeetCode Reference
- LC 3 Longest Substring (NeetCode 150)
- LC 5 Longest Palindromic Substring (NeetCode 150)
- LC 424 Longest Repeating Character (NeetCode 150)
- LC 76 Minimum Window Substring (NeetCode 150)
- LC 242 Valid Anagram (NeetCode 150)
- LC 647 Palindromic Substrings (NeetCode 150)

## Company-Specific Questions
### Google
- String algorithms are a core focus; expect complex string transformations
- Longest Substring and Minimum Window Substring are Google signature problems
- Variations involving character sets (ASCII vs Unicode) and case sensitivity

### Microsoft
- Valid Anagram and Longest Palindromic Substring are Microsoft favorites
- How would you implement string comparison in a word processor?
- Design a spell-checker using string algorithms

### Meta
- Sliding window problems (LC 3, LC 76, LC 424) are heavily tested at Meta
- Focus on O(n) time and O(1) space solutions
- Real-world framing: "How would you detect plagiarism in user posts?"

### Amazon
- Product search string matching with prefix/suffix
- Valid Anagram for detecting duplicate product listings
- Longest substring without repeats for session tracking

### Apple
- Memory-efficient string processing on limited devices
- Valid Palindrome with consideration for Unicode normalization
- String search in large text files on iOS (memory-mapped files)

### Oracle
- How does Oracle handle string comparison with different character sets (UTF-8 vs UTF-16)?
- Design a pattern matching algorithm for database TEXT columns
- Explain Oracle's LIKE operator implementation and index usage

## Real Production Scenarios
- Scenario 1: Search autocomplete - using a trie with character frequency to suggest completions for Google Search with sub-100ms latency
- Scenario 2: Plagiarism detection - applying Rabin-Karp rolling hash to fingerprint documents and find overlapping text segments in a content platform
- Scenario 3: Log parsing - debugging a misbehaving log parser that fails on multi-line log entries with special Unicode characters in production

## Interview Tips
- Sliding window problems follow a consistent pattern: expand right, shrink left when condition violated
- For palindrome problems, the expand-around-center approach is O(n^2); Manacher is O(n) but complex
- Know when to use frequency arrays (int[26] for lowercase letters) vs HashMap for character counts
- Common edge cases: empty strings, single character, all same characters, Unicode/special chars

## Java-Specific Considerations
- `String.charAt()` is O(1); `String.toCharArray()` creates a copy
- `String.substring()` is O(n) in Java 6, O(1) in Java 7+ (creates new char[] but shares no reference)
- `StringBuilder` for string construction in loops; avoid `String + String` concatenation
- `int[256]` for extended ASCII; `HashMap<Character, Integer>` for Unicode
- Pitfall: `==` compares references, not values; use `.equals()` for string comparison
- Pitfall: using `StringBuffer` instead of `StringBuilder` (StringBuffer is synchronized, slower)
