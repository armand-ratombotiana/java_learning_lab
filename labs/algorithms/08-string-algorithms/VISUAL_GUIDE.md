# Visual Guide — String Algorithms

## KMP Matching
`
Text:    A B A B A B A C
Pattern: A B A B A C

Step 1: A B A B A B A C
         A B A B A C    ← mismatch at C vs B, q=5→π[4]=3
         ↑ q=5
Step 2: A B A B A B A C
             A B A B A C  ← continue from q=3
             ↑ q=3
Step 3: match found!
`

## Trie Structure
`
        root
       / |  \
      a  b   c
     /   |    \
    t    a     a
   /     |      \
  e      t      t
        / \     /
       s   ?   ?
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — String Algorithms

## Trie Implementation
`java
class Trie {
    static class Node {
        Node[] children = new Node[26];
        boolean isEnd;
    }
    private Node root = new Node();

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null)
                node.children[idx] = new Node();
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return true;
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — String Algorithms

## KMP
1. Compute prefix function for pattern
2. Initialize matched count q = 0
3. For each character in text:
4.   While q > 0 and pattern[q] ≠ text[i], q = π[q-1]
5.   If pattern[q] == text[i], q++
6.   If q == m, pattern found at i-m+1

## Rabin-Karp
1. Compute h = Bᵐ⁻¹ mod prime
2. Compute pattern hash and first text window hash
3. For each window in text:
4.   If hashes match, verify character by character
5.   Roll hash to next window

## Trie
1. Root node has 26 children (for lowercase a-z)
2. Insert: walk/create nodes for each character, mark last as end
3. Search: walk nodes, check if last node is end
4. Prefix: walk nodes, return true if walk succeeds
