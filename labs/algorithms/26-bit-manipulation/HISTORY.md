# History of Bit Manipulation

1946: ENIAC, the first general-purpose electronic computer, used bit-level operations in its arithmetic units. Bit manipulation was implicit in hardware design before being formalized as algorithmic technique.

1960s: The C programming language introduced bitwise operators as first-class language features. This made bit manipulation accessible to software developers. The PDP-11 instruction set included powerful bit manipulation instructions.

1977: Henry S. Warren Jr. began collecting bit manipulation tricks at MIT, later published as "Hacker's Delight" (2002). This book became the definitive reference for bit-level algorithms.

1984: Brian Kernighan published his algorithm for counting set bits (population count). The algorithm iteratively clears the lowest set bit: x = x &amp; (x - 1).

1993: Gray code was patented by Frank Gray (Bell Labs) for pulse code communication. It found new applications in Karnaugh maps, error correction, and genetic algorithms.

2000s: Modern CPUs added hardware instructions for population count (POPCNT), leading zeros (LZCNT), and trailing zeros (TZCNT). These made certain bit manipulation algorithms even faster.

2008: SSE4.2 introduced POPCNT instruction in x86 processors. Bit manipulation became a first-class hardware operation rather than a software trick.

2010s: Bit manipulation remained essential in cryptography (SHA, AES), compression (zstd, lz4), and database systems (bitmap indexes in Oracle, PostgreSQL).