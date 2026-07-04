# Visual Guide to Tries

## Basic Trie

```
Insert "cat", "car", "dog":

        (root)
       /      \
      c        d
      |        |
      a        o
     / \       |
    t   r      g
    *   *      *

* = terminal node (end of word)
```

## Search Path for "car"

```
(root) → c → a → r → isEndOfWord? → true ✓
```

## Search Path for "cap"

```
(root) → c → a → p → p is not a child of 'a' → false ✗
```

## Autocomplete for "ca"

```
(root) → c → a
            / \
           t   r
           *   *

Results found: ["cat", "car"]
```

## Compressed Trie (Radix Tree)

```
Insert "cat", "car", "dog":

        (root)
       /      \
     "ca"     "dog"
     /   \       *
   "t"  "r"
    *    *

Single-child paths are merged into edges with string labels.
```

## Trie with Word Frequencies

```
         (root)
        /      \
       c(0)    d(0)
       |       |
       a(0)    o(0)
      /  \     |
    t(2) r(3)  g(1)
     *    *    *

Each node stores count of words passing through.
Useful for autocomplete ranking.
```

## Prefix Tree Visualization

```
Words: "she", "shell", "he", "hello", "hell"

         (root)
        /      \
      s         h
      |         |
      h         e
      |        / \
      e       l   l*
     / \      |
    l   l*    l
    |   |     |
    l*  *     o*
    *
```
