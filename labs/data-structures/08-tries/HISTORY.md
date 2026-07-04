# History: Tries

## Early Tries (1959–1960s)

- **1959**: **René de la Briandais** proposed a tree structure for searching dictionaries — the first description of what would become the trie. He used linked lists for children.
- **1960**: **Edward Fredkin** coined the term "trie" from the word "retrieval" (pronounced "try"). He advocated for it as a fast search structure.

## The Trie Name

Fredkin intended the pronunciation to be "tree" (like the middle of "retrieval"), but "try" became common to distinguish it from other trees. Both pronunciations are accepted.

## Patrica Trie (1968)

- **1968**: **Donald R. Morrison** invented the **Patricia trie** (Practical Algorithm To Retrieve Information Coded In Alphanumeric). It compresses single-child nodes, making it memory-efficient.
- **1973**: **Gernot Gwehenberger** independently described a similar compressed trie.

## Applications (1970s–1990s)

- **1970s**: Tries used for natural language processing and dictionary storage
- **1980s**: Suffix trees (tries of all suffixes) developed for string matching
- **1993**: **Binary tries** (CIDR) used in Internet routing for IP prefix matching
- **1990s**: **Aho-Corasick** algorithm (1975) gained popularity for multiple pattern matching using a trie with failure links

## Modern Era (2000s–present)

- **2001**: **Ternary search trees** (Bentley & Sedgewick) as a space-efficient alternative
- **2005**: **Double-array trie** — efficient array-based trie implementation used in Japanese tokenizers (MeCab, Kuromoji)
- **2010s**: **Radix trees** used in Linux kernel (page cache), Redis, and LevelDB
- **Java**: No standard library trie; Apache Commons, Guava, or custom implementations are used
