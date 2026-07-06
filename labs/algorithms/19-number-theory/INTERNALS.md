# Number Theory — Internal Mechanics

## Euclidean Algorithm

`java
long gcd(long a, long b) {
    while (b != 0) {
        long t = b;
        b = a % b;
        a = t;
    }
    return a;
}
`

## Extended Euclidean Algorithm

`java
long[] extendedGcd(long a, long b) {
    if (b == 0) return new long[]{a, 1, 0};
    long[] vals = extendedGcd(b, a % b);
    long d = vals[0], x1 = vals[1], y1 = vals[2];
    return new long[]{d, y1, x1 - (a / b) * y1};
}
`

## Modular Exponentiation (Square-and-Multiply)

`java
long modPow(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = (result * base) % mod;
        base = (base * base) % mod;
        exp >>= 1;
    }
    return result;
}
`

## Modular Inverse (using Extended GCD)

`java
long modInverse(long a, long m) {
    long[] vals = extendedGcd(a, m);
    if (vals[0] != 1) return -1;  // inverse doesn't exist
    return (vals[1] % m + m) % m;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Number Theory

## Euclidean Algorithm Correctness

gcd(a,b) = gcd(b, a mod b). Proof: Any divisor of a and b also divides b and a - q*b. The algorithm reduces arguments until reaching zero. The number of steps is O(log min(a,b)) because the arguments decrease by at least a factor of phi (golden ratio) every two steps.

## Miller-Rabin Error Probability

For a composite n, at most 1/4 of bases a in [1, n-1] are false witnesses (Miller-Rabin is not a liar). With k independent random bases, the probability of falsely declaring n prime is 4^{-k}. For k = 10, error probability < 10^{-6}.

## CRT Uniqueness

If x ≡ a_i (mod n_i) for coprime n_i, the solution is unique modulo N = product n_i. Proof: If x and y are both solutions, x-y ≡ 0 (mod n_i) for all i, so x-y ≡ 0 (mod N) by coprimality.

## Euler's Totient

φ(n) = n * product_{p|n} (1 - 1/p). For prime power p^k, φ(p^k) = p^k - p^{k-1}. φ is multiplicative: φ(mn) = φ(m)φ(n) for coprime m,n.

## Fermat's Little Theorem

For prime p and a not divisible by p: a^{p-1} ≡ 1 (mod p). This is the basis for the Miller-Rabin test and for computing modular inverses.
