# Security — String Matching

## Hash Collision Attacks

Rabin-Karp with a fixed modulus is vulnerable to algorithmic complexity attacks. An attacker can craft input that causes many hash collisions, degrading performance to O(nm). Use randomized modulus or rolling hash with two moduli.

## Pattern Injection

If patterns come from user input, very long patterns can cause excessive memory allocation. Validate pattern length before building data structures.

## ReDoS (Regex Denial of Service)

While these algorithms are safe, regex engines using backtracking can be catastrophically slow on crafted input. Prefer automaton-based approaches for untrusted input.

## Timing Side Channels

Constant-time string comparison may be needed when matching against sensitive tokens or passwords. Standard string matching algorithms are not constant-time.

## Memory Exhaustion

Aho-Corasick automaton size grows with total pattern length. For many patterns from untrusted sources, this can exhaust memory.
