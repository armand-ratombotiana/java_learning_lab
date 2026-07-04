# Security in Arithmetic

## Integer Overflow Attacks

Attackers can exploit overflow to bypass checks:

```java
// VULNERABLE
if (amount > balance) throw new Exception();
balance -= amount; // overflow causes balance to increase!

// SAFE: use Math.subtractExact
balance = Math.subtractExact(balance, amount);
```

## Arithmetic in Cryptography

Modular arithmetic is the foundation of modern cryptography:

- **RSA**: $c = m^e \mod n$
- **Diffie-Hellman**: $g^{ab} \mod p$
- **Elliptic Curve**: point addition on elliptic curves

```java
// Modular exponentiation (square-and-multiply)
public static long modPow(long base, long exp, long mod) {
    long result = 1;
    base = base % mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = (result * base) % mod;
        exp >>= 1;
        base = (base * base) % mod;
    }
    return result;
}
```

## Safe Math in Java

Use `Math.addExact`, `Math.subtractExact`, `Math.multiplyExact`, `Math.toIntExact` for overflow-safe arithmetic.
