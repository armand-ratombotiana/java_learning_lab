package collections;

/**
 * LeetCode 208 - Implement Trie (Prefix Tree)
 * 
 * A trie (pronounced "try") or prefix tree is a tree data structure used to efficiently
 * store and retrieve keys in a dataset of strings.
 * 
 * Approach: Array-based children (26 letters) or HashMap-based for generic characters
 * 
 * Time: O(m) per operation where m is key length
 * Space: O(total characters stored)
 */
public class ImplementTrie {

    static class Trie {
        private static class Node {
            Node[] children = new Node[26];
            boolean isEnd;
        }

        private final Node root = new Node();

        public void insert(String word) {
            Node cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new Node();
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        public boolean search(String word) {
            Node cur = root;
            for (char c : word.toCharArray()) {
                cur = cur.children[c - 'a'];
                if (cur == null) return false;
            }
            return cur.isEnd;
        }

        public boolean startsWith(String prefix) {
            Node cur = root;
            for (char c : prefix.toCharArray()) {
                cur = cur.children[c - 'a'];
                if (cur == null) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        assert trie.search("apple") : "search apple";
        assert !trie.search("app") : "search app (not full word)";
        assert trie.startsWith("app") : "prefix app";
        trie.insert("app");
        assert trie.search("app") : "search app after insert";
        assert !trie.search("ap") : "ap is not a word";
        assert trie.startsWith("a") : "prefix a";
        assert trie.startsWith("appl") : "prefix appl";
        System.out.println("All Trie tests passed.");
    }
}