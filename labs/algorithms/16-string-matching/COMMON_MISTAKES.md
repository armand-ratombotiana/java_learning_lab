# Common Mistakes — String Matching

- **KMP: Off-by-one in prefix function** — Forgetting that pi[j-1] is correct when mismatching at position j
- **KMP: Wrong boundary after match** — Setting j = 0 instead of j = pi[j-1] after finding a complete match
- **Boyer-Moore: Bad char table size** — Only allocating for letters when input may contain extended ASCII
- **Boyer-Moore: Both heuristics needed** — Using only bad character rule gives worst-case O(nm)
- **Rabin-Karp: Modulo negative** — Java's % operator can return negative; always add M before % M
- **Rabin-Karp: Hash overflow** — Not using long for intermediate multiplication before modulo
- **Z-algorithm: Z-array index confusion** — Forgetting to account for the sentinel character offset
- **Aho-Corasick: Missing output links** — Only checking matches at the current node, not following output links
- **Aho-Corasick: Trie memory** — Not using compact storage for large alphabets
- **Generic: Pattern longer than text** — Not checking early and returning empty result
