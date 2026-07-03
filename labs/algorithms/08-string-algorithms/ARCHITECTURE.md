# Architecture — String Algorithms

## Java Standard Library
- String.indexOf() — naive with optimizations (Boyer-Moore in modern JDK)
- String.matches() — regex-based pattern matching
- java.util.regex — NFA/DFA-based regex engine
- HashMap/HashSet — hash-based string operations

## Real-World Systems
- grep: Boyer-Moore algorithm
- Google Search: Index-based (inverted index)
- Spell Checkers: Trie + edit distance
- DNA Search: Burrows-Wheeler transform + FM-index
- Auto-complete: Trie with frequency ranking
- Plagiarism Detection: Rabin-Karp (fingerprinting documents)
