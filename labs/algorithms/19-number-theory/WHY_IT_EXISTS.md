# Why Number Theory Algorithms Exist

Number theory has been studied for thousands of years, but algorithmic number theory emerged with the advent of computers. The ancient Euclidean algorithm (c. 300 BC) is the oldest known algorithm still in use. Modern cryptographic systems rely entirely on the computational difficulty of certain number theory problems.

The Sieve of Eratosthenes (c. 240 BC) remained the most efficient prime generation method for over 2000 years. It is still used as a building block in more complex algorithms. The segmented sieve variant handles ranges that exceed memory, enabling prime generation up to 10^12 and beyond.

Miller-Rabin (1980) addressed the practical need for fast primality testing. Prior algorithms like trial division were too slow for the large numbers used in cryptography (1024-4096 bits). Miller-Rabin provides a tradeoff between certainty and speed: each test round halves the probability of error.

The Extended Euclidean algorithm is essential for computing modular inverses, which are required in RSA key generation, elliptic curve cryptography, and the CRT implementation. Without it, asymmetric cryptography as we know it would not be possible.

CRT was discovered by Sun Tzu in the 3rd century AD and later proved by Gauss. In modern computing, CRT enables fast computation by splitting arithmetic modulo a large number N into parallel arithmetic modulo smaller factors. RSA decryption uses CRT for a 4x speedup.

Euler's totient function forms the mathematical foundation of RSA: the encryption and decryption exponents are inverses modulo φ(n). The security of RSA relies on the difficulty of computing φ(n) without knowing the factorization of n.
