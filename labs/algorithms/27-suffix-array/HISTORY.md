# History of Suffix Arrays

1973: Peter Weiner introduced the suffix tree, an O(n) data structure for pattern matching. This was the first linear-time string indexing structure, but its memory overhead was significant.

1976: Esko Ukkonen developed an online suffix tree construction algorithm, making suffix trees more practical. However, pointer overhead remained a barrier for large strings.

1983: Udi Manber and Gene Myers introduced the suffix array as a space-efficient alternative to suffix trees. Their initial O(n log n) construction algorithm used prefix-doubling.

1990: The suffix array was independently discovered by Gaston Gonnet and Ricardo Baeza-Yates in the context of text databases.

1992: Simon J. Kasai and colleagues published Kasai's algorithm for O(n) LCP array construction. This made suffix arrays fully practical for simulating suffix tree operations.

2003: The SA-IS (Suffix Array Induced Sorting) algorithm by Ge Nong, Sen Zhang, and Wai Hong Chan achieved O(n) time with a compact implementation.

2009: Yuta Mori introduced the DivSufSort algorithm, achieving practical O(n log n) performance with extremely low constant factors. It became the default in many applications.

2010s: Induced sorting algorithms became the standard for suffix array construction. The Burrows-Wheeler transform and FM-index (Ferragina-Manzini index) built on suffix arrays became the basis of compressed full-text indexing.