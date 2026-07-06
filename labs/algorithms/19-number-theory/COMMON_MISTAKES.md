# Common Mistakes — Number Theory

- **Forgetting modulo in every multiplication** — Intermediate multiplication of large numbers overflows Java long
- **Modular inverse with non-coprime numbers** — Inverse doesn't exist when gcd(a,m) != 1
- **Negative modulo** — Java's % returns negative for negative numbers; always add m before % m
- **Sieve array too large** — Boolean array of size 10^9 uses 1GB; use segmented sieve
- **Miller-Rabin without strong liars check** — Carmichael numbers pass Fermat test but fail Miller-Rabin
- **Integer overflow in fast exponentiation** — Use java.math.BigInteger for numbers > 2^63
- **GCD of negative numbers** — Handle sign; return absolute value
- **CRT moduli not coprime** — CRT requires pairwise coprime moduli
- **Forgetting long conversion** — Multiplying two ints may overflow before being stored in long
- **Sieve starting from i^2** — Starting from i instead of i^2 wastes operations
