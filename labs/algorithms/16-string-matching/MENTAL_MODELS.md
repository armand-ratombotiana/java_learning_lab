# Mental Models for String Matching

## KMP — "The Bookmark"

Imagine reading a book where you need to find a specific phrase. When you reach a word that doesn't match, instead of starting over from the next page, you use a bookmark to jump back to the last place where the text could possibly restart the phrase. The prefix function tells you exactly where that bookmark goes, so you never reread text you've already matched.

## Boyer-Moore — "The Reverse Inspector"

Picture a detective examining a line of evidence from right to left. When a piece doesn't fit, the detective looks at what the mismatched piece is and how it relates to the evidence pattern. If the mismatched piece never appears in the pattern, the detective can skip the entire segment. This is the bad character rule. If a suffix matched but the preceding character didn't, the detective looks for another occurrence of that suffix that might align with a different preceding character. This is the good suffix rule.

## Rabin-Karp — "The Fingerprint Scanner"

Think of scanning a crowd for a specific person. Instead of comparing every single detail for each person, you compute a quick fingerprint for each person. If the fingerprint matches, you then do a detailed check. The rolling hash lets you efficiently compute the fingerprint for the next window by only adjusting for the character entering and leaving.

## Aho-Corasick — "The Multi-Pattern Automaton"

Imagine a librarian who needs to find any book from a list of banned titles on shelves. Instead of searching for each title separately, the librarian builds a decision tree that can spot any banned title by reading each shelf left to right once. When a partial match is found, the librarian follows shortcuts (failure links) to continue searching without starting over.
