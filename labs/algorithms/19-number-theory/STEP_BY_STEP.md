# Step-by-Step — Number Theory Implementation

## Sieve of Eratosthenes Steps

1. Create boolean array isPrime[0..n] initialized to true
2. Set isPrime[0] = isPrime[1] = false
3. For i from 2 to sqrt(n):
   a. If isPrime[i] is true:
      - Mark all multiples of i from i*i to n as false
4. Collect all i where isPrime[i] is true

## Miller-Rabin Steps

1. Write n-1 = d * 2^r where d is odd
2. For each base a in test set:
   a. Compute x = a^d mod n
   b. If x == 1 or x == n-1, continue to next base
   c. Repeat r-1 times: x = x^2 mod n
      - If x == n-1, break (inconclusive for this base)
      - If x == 1, n is composite
   d. If loop completes without breaking, n is composite
3. If all bases pass, n is probably prime
