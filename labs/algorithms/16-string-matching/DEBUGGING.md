# Debugging — String Matching

## Prefix Function Verification

`java
// Print prefix function for debugging
System.out.println("Prefix: " + Arrays.toString(pi));
`

For pattern "ABABAC", expected pi = [0, 0, 1, 1, 2, 0].

## Search Step Tracing

`java
// Trace each comparison
System.out.println("i=" + i + " j=" + j + " text=" + text.charAt(i) + " pattern=" + pattern.charAt(j));
`

## Rolling Hash Verification

Print hash values for each window and verify that equal substrings produce equal hashes. If not, check base and modulus values.

## Aho-Corasick Trie Visualization

Print the trie with node indices, characters, and failure links to verify automaton correctness.

## Common Test Cases

- Pattern equals text
- Pattern longer than text (empty result)
- Multiple overlapping matches (e.g., "AAA" in "AAAA")
- No match present
- Pattern and text with repeating characters
