# Exercises: Number Theory

## Theoretical Exercises

### Problem 1: Fundamental Theorem of Arithmetic
Prove that every integer n > 1 can be written uniquely as a product of primes.

### Problem 2: Infinite Primes
Prove that there are infinitely many prime numbers using Euclid's method.

### Problem 3: GCD Properties
Show that gcd(a, b) = gcd(a, b - a) for integers a, b. Generalize to gcd(a, b) = gcd(a, b mod a).

### Problem 4: Modular Arithmetic
Prove that if a ≡ b (mod m) and c ≡ d (mod m), then:
a) a + c ≡ b + d (mod m)
b) ac ≡ bd (mod m)
c) aⁿ ≡ bⁿ (mod m) for n ≥ 1

### Problem 5: Chinese Remainder Theorem
Find all integers x satisfying:
x ≡ 2 (mod 3), x ≡ 3 (mod 5), x ≡ 2 (mod 7)

### Problem 6: Euler's Theorem
Show that for any integer a coprime to n, a^φ(n) ≡ 1 (mod n). Use this to explain Fermat's Little Theorem as a special case.

### Problem 7: RSA Security
Explain why RSA security depends on the difficulty of factoring. What happens if p and q are chosen too close together?

### Problem 8: Primality Testing
Compare the time complexity of trial division, Miller-Rabin, and AKS primality tests. For what size inputs is each preferred?

## Programming Exercises

### Exercise 1: GCD and LCM
Implement gcd and lcm using the Euclidean algorithm. Test with various inputs including edge cases.

### Exercise 2: Extended Euclidean Algorithm
Implement extendedEuclid(a, b) that returns [gcd, x, y] where ax + by = gcd(a, b).

### Exercise 3: Modular Exponentiation
Implement fast modular exponentiation using the square-and-multiply algorithm.

### Exercise 4: Modular Inverse
Compute the modular inverse of a modulo m using the extended Euclidean algorithm. Handle the case where the inverse does not exist.

### Exercise 5: Chinese Remainder Solver
Implement a solver for systems of simultaneous congruences. Test with the system from Problem 5.

### Exercise 6: RSA Implementation
Generate RSA keys of varying bit lengths (512, 1024, 2048). Encrypt and decrypt messages. Verify that RSA is commutative (encrypt then decrypt = decrypt then encrypt).

### Exercise 7: Prime Factorization
Implement Pollard's rho factorization algorithm. Test it on products of two primes.

### Exercise 8: Miller-Rabin Test
Implement the Miller-Rabin primality test. Compare its performance against trial division for large numbers.

### Exercise 9: Carmichael Numbers
Find Carmichael numbers and verify that they pass Fermat's test but are composite.

### Exercise 10: Performance Benchmark
Benchmark your implementations against Java's built-in BigInteger methods. Create a table comparing execution times.

## Mini-Project: RSA Chat
Implement a simple encrypted chat system using RSA. Generate key pairs, exchange public keys, and send encrypted messages between two parties.

## Real-World Project: Secure File Encryption
Build a file encryption tool that uses RSA for key exchange (asymmetric) and AES for bulk encryption (symmetric). The tool should:
1. Generate RSA key pairs for sender and receiver
2. Use RSA to encrypt an AES session key
3. Encrypt the file content with AES
4. Decrypt the file using the corresponding private key
5. Support file signing for authenticity verification
