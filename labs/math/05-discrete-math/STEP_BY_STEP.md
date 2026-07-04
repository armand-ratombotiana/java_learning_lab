# Step-by-Step: Discrete Math in Java

## Extended Euclidean Algorithm

```java
public static int[] extendedGCD(int a, int b) {
    if (b == 0) return new int[]{a, 1, 0};
    int[] prev = extendedGCD(b, a % b);
    int gcd = prev[0], x1 = prev[1], y1 = prev[2];
    return new int[]{gcd, y1, x1 - (a / b) * y1};
}
// Returns [gcd, x, y] such that ax + by = gcd
```

## Sieve of Eratosthenes

```java
public static boolean[] sieve(int n) {
    boolean[] isPrime = new boolean[n + 1];
    Arrays.fill(isPrime, true);
    isPrime[0] = isPrime[1] = false;
    for (int i = 2; i * i <= n; i++)
        if (isPrime[i])
            for (int j = i * i; j <= n; j += i)
                isPrime[j] = false;
    return isPrime;
}
```

## Power Set Generation

```java
public static <T> List<Set<T>> powerSet(List<T> list) {
    int n = list.size();
    List<Set<T>> result = new ArrayList<>();
    for (int mask = 0; mask < (1 << n); mask++) {
        Set<T> subset = new HashSet<>();
        for (int i = 0; i < n; i++)
            if ((mask & (1 << i)) != 0)
                subset.add(list.get(i));
        result.add(subset);
    }
    return result;
}
```

## Check if Relation is Equivalence

```java
public static boolean isEquivalenceRelation(int[][] matrix) {
    int n = matrix.length;
    // Reflexive: all diagonal entries = 1
    for (int i = 0; i < n; i++)
        if (matrix[i][i] != 1) return false;
    // Symmetric
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (matrix[i][j] != matrix[j][i]) return false;
    // Transitive: if M[i][j]=1 and M[j][k]=1 then M[i][k]=1
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (matrix[i][j] == 1)
                for (int k = 0; k < n; k++)
                    if (matrix[j][k] == 1 && matrix[i][k] != 1)
                        return false;
    return true;
}
```
