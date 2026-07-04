# Security in Algebra

## Homomorphic Encryption

Algebraic structures enable computation on encrypted data:

- **Paillier**: additive homomorphic ($E(a) \cdot E(b) = E(a+b)$)
- **RSA**: multiplicative homomorphic
- **Fully Homomorphic Encryption (FHE)**: arbitrary computations on ciphertexts

## Secret Sharing (Shamir's Scheme)

Based on polynomial interpolation: a degree $k-1$ polynomial is determined by $k$ points.

```java
public class ShamirSecretSharing {
    // Generate random polynomial with secret as constant term
    public static int[] evaluate(int secret, int k, int n, int prime) {
        int[] coeffs = new int[k];
        coeffs[0] = secret;
        for (int i = 1; i < k; i++)
            coeffs[i] = new Random().nextInt(prime);
        int[] shares = new int[n];
        for (int i = 1; i <= n; i++)
            shares[i-1] = evaluatePolynomial(coeffs, i, prime);
        return shares;
    }

    // Lagrange interpolation to recover secret
    public static int recover(int[] xVals, int[] yVals, int k, int prime) {
        int secret = 0;
        for (int i = 0; i < k; i++) {
            int num = 1, den = 1;
            for (int j = 0; j < k; j++)
                if (i != j) {
                    num = (num * (-xVals[j])) % prime;
                    den = (den * (xVals[i] - xVals[j])) % prime;
                }
            secret = (secret + yVals[i] * num * modInverse(den, prime)) % prime;
        }
        return (secret + prime) % prime;
    }
}
```

## Error-Correcting Codes (Reed-Solomon)

Polynomial evaluation over finite fields enables detection and correction of transmission errors.
