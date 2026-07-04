# Security in Probability

## Cryptographic Randomness

```java
// INSECURE: predictable seed
Random rng = new Random(12345);

// SECURE: cryptographically strong
SecureRandom csrng = new SecureRandom();
byte[] key = new byte[32];
csrng.nextBytes(key);
```

## Side-Channel Attacks

Timing differences in probability distributions can leak information. Use constant-time operations.

## Probability in Security Analysis

### Randomness Testing

```
NIST SP 800-22 tests:
├── Frequency (monobit)
├── Runs test
├── Discrete Fourier Transform
├── Approximate Entropy
└── Linear Complexity
```

### Guessing Attacks

Password entropy:
$$
H = L \cdot \log_2(N)
$$

Where $L$ = length, $N$ = alphabet size.

```java
public static double entropy(String password) {
    long charset = 0;
    if (password.matches(".*[a-z].*")) charset += 26;
    if (password.matches(".*[A-Z].*")) charset += 26;
    if (password.matches(".*\\d.*")) charset += 10;
    if (password.matches(".*[^a-zA-Z0-9].*")) charset += 33;
    return password.length() * (Math.log(charset) / Math.log(2));
}
```
