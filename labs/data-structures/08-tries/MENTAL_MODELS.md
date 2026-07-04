# Mental Models for Tries

## The Dictionary Tree

A dictionary is organized like a trie of words. The "A" section has words starting with A. Under "AP" are "apple", "apply", "apartment". The prefix "app" is shared. To find all words starting with "app", you go to the APP section and list everything below it.

## The Filing System

A physical filing system where:
- Each drawer is a character
- You start at the first character drawer, open it
- Inside are sub-drawers for the second character
- Inside that, sub-drawers for the third character
- A red marker on a drawer means "this is a complete word"

To find "cat": open C → open A → open T → check for red marker.

## The Phone Tree

An automated phone system: "Press 1 for Sales, 2 for Support..."
Each digit you press goes deeper into the tree. The path of digits is the prefix. Each extension is a complete "word" in the tree.

## The Evolutionary Tree

Species classifications: Kingdom → Phylum → Class → Order → Family → Genus → Species. Each level shares the prefix of the levels above. "Homo sapiens" and "Homo neanderthalensis" share the prefix "Homo".

## Array vs Map Children

```
Array (26 slots):
┌──┬──┬──┬──┬──┬──┬──┬──┬──┐
│a │b │c │d │e │f │...│y │z │
└──┴──┴──┴──┴──┴──┴──┴──┴──┘
     ↑
     pointer to "b" node

Map (sparse):
{b: Node, e: Node, ...}
Only stores present children.
```

Array: faster access (O(1) index), more memory
Map: slower (hash lookup), less memory for sparse trees
