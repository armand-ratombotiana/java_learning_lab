# Mental Models

## KMP — "Bookmark After Partial Match"
After partially matching a pattern, you don't start from scratch — you use a bookmark (prefix function) to continue from where the match overlaps.

## Rabin-Karp — "Fingerprint Comparison"
Instead of comparing strings character by character, compute a numeric fingerprint (hash). Compare fingerprints first, verify on match.

## Trie — "Tree of Prefixes"
Like a phone tree menu: each node asks "what's the next character?" and branches accordingly. All strings sharing a prefix share the same path.
