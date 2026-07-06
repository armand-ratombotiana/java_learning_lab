# History of String Matching Algorithms

1970: James Morris and Vaughan Pratt independently discovered a linear-time string matching algorithm. They published their results at the 1970 Symposium on Switching and Automata Theory.

1977: Donald Knuth, James Morris, and Vaughan Pratt published the full KMP algorithm in the SIAM Journal on Computing, providing a rigorous analysis and establishing linear-time worst-case string matching.

1977: Robert S. Boyer and J. Strother Moore published their algorithm in Communications of the ACM, introducing the right-to-left scan with bad character and good suffix heuristics. Their algorithm became the fastest in practice for English text.

1975: Alfred V. Aho and Margaret J. Corasick published their multi-pattern matching algorithm in the Communications of the ACM, generalizing KMP to a trie-based automaton.

1984: Dan Gusfield published the Z-algorithm, providing an alternative linear-time approach with simpler implementation than KMP.

1987: Richard M. Karp and Michael O. Rabin introduced randomized string matching with rolling hashes at the ACM Conference on Principles of Programming Languages.

1990s: Commentz-Walter and Wu-Manber algorithms combined ideas from Boyer-Moore and Aho-Corasick for efficient multi-pattern matching.

2000s: The rise of bioinformatics drove new algorithms for approximate string matching and alignment. BLAST and FASTA became essential tools for genomic sequence matching.

2006: Bit-parallel algorithms (Shift-Or, BNDM) leveraged SIMD instructions for fast string matching on modern hardware.

2010s: Automata-based approaches using finite automata and suffix automata achieved new theoretical and practical results.

2019: Modern regex engines (RE2, Rust regex) combine multiple string matching strategies with automata compilation for linear-time regular expression matching.
