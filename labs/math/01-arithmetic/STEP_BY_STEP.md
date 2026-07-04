# Step-by-Step: Arithmetic in Java

## Long Addition (manual algorithm)

```java
public static String longAddition(String a, String b) {
    StringBuilder result = new StringBuilder();
    int carry = 0;
    int i = a.length() - 1, j = b.length() - 1;

    while (i >= 0 || j >= 0 || carry > 0) {
        int digitA = (i >= 0) ? a.charAt(i--) - '0' : 0;
        int digitB = (j >= 0) ? b.charAt(j--) - '0' : 0;
        int sum = digitA + digitB + carry;
        result.append(sum % 10);
        carry = sum / 10;
    }
    return result.reverse().toString();
}
```

## GCD via Euclidean Algorithm

```java
public static int gcd(int a, int b) {
    while (b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}
```

## Prime Factorization

```java
public static List<Integer> primeFactors(int n) {
    List<Integer> factors = new ArrayList<>();
    for (int i = 2; i * i <= n; i++) {
        while (n % i == 0) {
            factors.add(i);
            n /= i;
        }
    }
    if (n > 1) factors.add(n);
    return factors;
}
```
