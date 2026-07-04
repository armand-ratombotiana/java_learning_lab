# Flashcards: Tries

---

**Q**: What is a trie?
**A**: An N-ary tree where each node represents a prefix of keys, edges are labeled with characters.

---

**Q**: What is the time complexity of trie operations?
**A**: O(L) where L is the string length — independent of number of keys.

---

**Q**: What does isEndOfWord mean in a trie node?
**A**: A complete word ends at this node.

---

**Q**: What is the advantage of a trie over HashMap for prefix search?
**A**: Tries support O(L) prefix search; HashMap requires O(nL) scan.

---

**Q**: What is a radix tree?
**A**: A compressed trie where single-child nodes are merged into labeled edges.

---

**Q**: What is the memory cost of a trie node with array children (alphabet size 26)?
**A**: ~68 bytes per node (array object + overhead) regardless of actual children.

---

**Q**: When should you use Map-backed children vs array-backed children?
**A**: Map for sparse/sparse alphabets; array for fixed, dense alphabets.

---

**Q**: What is the difference between search and startsWith?
**A**: search checks the terminal flag; startsWith only checks if the prefix exists.

---

**Q**: What is a Patricia trie?
**A**: Practical Algorithm To Retrieve Information Coded In Alphanumeric — a compressed trie.

---

**Q**: What are tries used for in networking?
**A**: IP routing — binary tries for CIDR prefix matching (longest prefix match).
