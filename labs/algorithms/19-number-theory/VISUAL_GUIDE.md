# Visual Guide — Number Theory

## Sieve of Eratosthenes for n=20

Step 1: [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13] [14] [15] [16] [17] [18] [19] [20]
Step 2: Mark multiples of 2 -> [4] [6] [8] [10] [12] [14] [16] [18] [20] crossed
Step 3: Mark multiples of 3 -> [6] [9] [12] [15] [18] crossed
Step 5: Mark multiples of 5 -> [10] [15] [20] crossed (already crossed)
Stop at sqrt(20) ≈ 4.5 (stop after 5^2 > 20)

Primes: 2, 3, 5, 7, 11, 13, 17, 19

## Modular Exponentiation Visualization

3^13 mod 5: 13 = 1101 binary
  bit 1 (LSB): result = 3^1 mod 5 = 3
  bit 0: base = 3^2 = 9 mod 5 = 4
  bit 1: result = 3 * 4^2 = 3 * 16 mod 5 = 48 mod 5 = 3
           base = 4^2 = 16 mod 5 = 1
  bit 1: result = 3 * 1^2 = 3
  Final: 3^13 mod 5 = 3
