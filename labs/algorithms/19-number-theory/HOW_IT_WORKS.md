# How Number Theory Algorithms Work

## Euclidean Algorithm Example

GCD(252, 105):
252 = 2 * 105 + 42
105 = 2 * 42 + 21
42 = 2 * 21 + 0
GCD = 21

## Extended Euclidean Algorithm Example

GCD(252, 105) = 21, find x,y: 252x + 105y = 21
42 = 252 - 2*105
21 = 105 - 2*42 = 105 - 2*(252 - 2*105) = 5*105 - 2*252
So x = -2, y = 5

## Miller-Rabin Test for n = 221

n-1 = 220 = 2^2 * 55 (r=2, d=55)
Base a = 174: a^55 mod 221 = 47 != 1, != n-1
Check a^(55*2) mod 221 = 47^2 mod 221 = 220 = n-1 -> base 174 is witness
That tells us nothing. Try a = 137: a^55 mod 221 = ...
Continue with multiple bases. 221 = 13 * 17, will eventually be detected.

## Sieve for n = 30

Initial: [true, true, true, true, true, ...] for 0..30
i=2: mark 4,6,8,10,12,14,16,18,20,22,24,26,28,30 as false
i=3: mark 6,9,12,15,18,21,24,27,30 as false
i=5: mark 10,15,20,25,30 as false
i=7: 7^2 = 49 > 30, stop
Primes: 2,3,5,7,11,13,17,19,23,29
