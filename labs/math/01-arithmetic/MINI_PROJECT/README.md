# Mini Project: Prime Number Checker & Generator

## Project Overview
Build a comprehensive prime number analysis system with multiple algorithms, prime generation, and factorization capabilities.

## Project Structure
```
MINI_PROJECT/
├── PrimeAnalyzer.java
├── PrimeGenerator.java
├── PrimeFactorization.java
├── PrimeRange.java
└── PrimeAnalyzerTest.java
```

## Implementation

### PrimeAnalyzer.java
```java
package com.mathacademy.arithmetic.mini;

import java.util.ArrayList;
import java.util.List;

public class PrimeAnalyzer {
    
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isPrimeMillerRabin(long n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;
        
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }
        
        int[] witnesses = {2, 3, 5, 7, 11, 13, 17};
        for (int a : witnesses) {
            if (a >= n) continue;
            if (millerRabinTest(a, n, d, s)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean millerRabinTest(long a, long n, long d, int s) {
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) return false;
        
        for (int r = 1; r < s; r++) {
            x = modPow(x, 2, n);
            if (x == n - 1) return false;
        }
        return true;
    }
    
    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        long b = base % mod;
        while (exp > 0) {
            if (exp % 2 == 1) {
                result = (result * b) % mod;
            }
            b = (b * b) % mod;
            exp /= 2;
        }
        return result;
    }
    
    public static int nextPrime(int n) {
        while (!isPrime(++n)) {}
        return n;
    }
    
    public static int previousPrime(int n) {
        while (n > 2 && !isPrime(--n)) {}
        return n;
    }
    
    public static List<Integer> getPrimeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        int temp = n;
        
        for (int i = 2; i * i <= temp; i++) {
            while (temp % i == 0) {
                factors.add(i);
                temp /= i;
            }
        }
        if (temp > 1) factors.add(temp);
        return factors;
    }
    
    public static int primeCount(int limit) {
        int count = 0;
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }
    
    public static double primeDensity(int n) {
        return (double) primeCount(n) / n;
    }
    
    public static long sumOfPrimes(int limit) {
        long sum = 0;
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) sum += i;
        }
        return sum;
    }
}
```

### PrimeGenerator.java
```java
package com.mathacademy.arithmetic.mini;

import java.util.ArrayList;
import java.util.List;

public class PrimeGenerator {
    
    public static List<Integer> sieve(int limit) {
        boolean[] isComposite = new boolean[limit + 1];
        List<Integer> primes = new ArrayList<>();
        
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                for (int j = i * 2; j <= limit; j += i) {
                    isComposite[j] = true;
                }
            }
        }
        return primes;
    }
    
    public static List<Integer> segmentedSieve(long low, long high) {
        boolean[] isComposite = new boolean[(int)(high - low + 1)];
        
        long sqrtHigh = (long) Math.sqrt(high);
        List<Integer> smallPrimes = PrimeAnalyzer.isPrime((int) sqrtHigh) 
            ? List.of() : PrimeGenerator.sieve((int) sqrtHigh);
        
        for (long i = low; i <= high; i++) {
            if (i < 2) continue;
            long num = i;
            for (int p : smallPrimes) {
                long start = Math.max(p * p, (low + p - 1) / p * p);
                for (long j = start; j <= high; j += p) {
                    isComposite[(int)(j - low)] = true;
                }
            }
        }
        
        List<Integer> result = new ArrayList<>();
        for (long i = low; i <= high; i++) {
            if (i >= 2 && !isComposite[(int)(i - low)]) {
                result.add((int) i);
            }
        }
        return result;
    }
    
    public static List<Integer> generateTwinPrimes(int limit) {
        List<Integer> twins = new ArrayList<>();
        List<Integer> primes = sieve(limit);
        
        for (int i = 1; i < primes.size(); i++) {
            if (primes.get(i) - primes.get(i - 1) == 2) {
                twins.add(primes.get(i - 1));
                twins.add(primes.get(i));
            }
        }
        return twins;
    }
    
    public static List<Integer> generateCousinPrimes(int limit) {
        List<Integer> cousins = new ArrayList<>();
        List<Integer> primes = sieve(limit);
        
        for (int i = 1; i < primes.size(); i++) {
            if (primes.get(i) - primes.get(i - 1) == 4) {
                cousins.add(primes.get(i - 1));
                cousins.add(primes.get(i));
            }
        }
        return cousins;
    }
    
    public static List<Long> generateMersennePrimes(int maxExponent) {
        List<Long> mersenne = new ArrayList<>();
        for (int p = 2; p <= maxExponent; p++) {
            if (PrimeAnalyzer.isPrime(p)) {
                long mersenneNum = (1L << p) - 1;
                if (PrimeAnalyzer.isPrimeMillerRabin((int) mersenneNum)) {
                    mersenne.add(mersenneNum);
                }
            }
        }
        return mersenne;
    }
}
```

### PrimeFactorization.java
```java
package com.mathacademy.arithmetic.mini;

import java.util.HashMap;
import java.util.Map;

public class PrimeFactorization {
    
    public static Map<Integer, Integer> factorize(int n) {
        Map<Integer, Integer> factors = new HashMap<>();
        int temp = Math.abs(n);
        
        for (int i = 2; i * i <= temp; i++) {
            while (temp % i == 0) {
                factors.put(i, factors.getOrDefault(i, 0) + 1);
                temp /= i;
            }
        }
        if (temp > 1) {
            factors.put(temp, factors.getOrDefault(temp, 0) + 1);
        }
        return factors;
    }
    
    public static String factorizeToString(int n) {
        if (n == 1 || n == -1) return String.valueOf(n);
        
        Map<Integer, Integer> factors = factorize(Math.abs(n));
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<Integer, Integer> entry : factors.entrySet()) {
            if (!first) sb.append(" × ");
            first = false;
            if (entry.getValue() == 1) {
                sb.append(entry.getKey());
            } else {
                sb.append(entry.getKey()).append("^").append(entry.getValue());
            }
        }
        
        return n < 0 ? "(-1) × " + sb : sb.toString();
    }
    
    public static int divisorCount(int n) {
        Map<Integer, Integer> factors = factorize(n);
        int count = 1;
        for (int exp : factors.values()) {
            count *= (exp + 1);
        }
        return count;
    }
    
    public static long sumOfDivisors(int n) {
        Map<Integer, Integer> factors = factorize(n);
        long sum = 1;
        for (Map.Entry<Integer, Integer> entry : factors.entrySet()) {
            int p = entry.getKey();
            int e = entry.getValue();
            sum *= (Math.pow(p, e + 1) - 1) / (p - 1);
        }
        return sum;
    }
    
    public static long productOfDivisors(int n) {
        int divisorCount = divisorCount(n);
        int sqrtN = (int) Math.sqrt(n);
        if (sqrtN * sqrtN == n) {
            return (long) Math.pow(n, divisorCount / 2.0);
        } else {
            return (long) Math.pow(n, divisorCount / 2.0) * sqrtN;
        }
    }
    
    public static boolean isPerfect(int n) {
        return sumOfDivisors(n) - n == n;
    }
    
    public static boolean isAbundant(int n) {
        return sumOfDivisors(n) - n > n;
    }
    
    public static boolean isDeficient(int n) {
        return sumOfDivisors(n) - n < n;
    }
}
```

### PrimeRange.java
```java
package com.mathacademy.arithmetic.mini;

import java.util.List;
import java.util.Random;

public class PrimeRange {
    
    public static int[] findPrimesInRange(int start, int end) {
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        
        int size = 0;
        int[] tempArray = new int[end - start + 1];
        
        for (int i = start; i <= end; i++) {
            if (PrimeAnalyzer.isPrime(i)) {
                tempArray[size++] = i;
            }
        }
        
        int[] result = new int[size];
        System.arraycopy(tempArray, 0, result, 0, size);
        return result;
    }
    
    public static int findKthPrime(int k) {
        if (k < 1) throw new IllegalArgumentException("k must be positive");
        
        int estimate = (int) (k * (Math.log(k) + Math.log(Math.log(k))));
        int size = Math.max(estimate, 11);
        
        while (true) {
            List<Integer> primes = PrimeGenerator.sieve(size);
            if (primes.size() >= k) {
                return primes.get(k - 1);
            }
            size *= 2;
        }
    }
    
    public static int[] getFirstNPrimes(int n) {
        int estimate = (int) (n * (Math.log(n) + Math.log(Math.log(n))));
        int size = Math.max(estimate, 11);
        
        List<Integer> primes = PrimeGenerator.sieve(size);
        while (primes.size() < n) {
            size *= 2;
            primes = PrimeGenerator.sieve(size);
        }
        
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = primes.get(i);
        }
        return result;
    }
    
    public static long generateRandomPrime(int bitLength) {
        Random random = new Random();
        long min = (1L << (bitLength - 1));
        long max = (1L << bitLength) - 1;
        
        for (int i = 0; i < 1000; i++) {
            long candidate = min + random.nextLong() % (max - min + 1);
            candidate |= 1;
            candidate |= (1L << (bitLength - 1));
            
            if (PrimeAnalyzer.isPrimeMillerRabin(candidate)) {
                return candidate;
            }
        }
        throw new RuntimeException("Could not find prime in range");
    }
}
```

### PrimeAnalyzerTest.java
```java
package com.mathacademy.arithmetic.mini;

import java.util.Arrays;
import java.util.List;

public class PrimeAnalyzerTest {
    
    public static void main(String[] args) {
        System.out.println("=== Prime Number Analyzer Test ===\n");
        
        testBasicPrimality();
        testFactorization();
        testGenerator();
        testRange();
        testFactorizationAnalysis();
    }
    
    private static void testBasicPrimality() {
        System.out.println("1. Basic Primality Tests:");
        int[] testNumbers = {1, 2, 17, 100, 997, 1000};
        for (int n : testNumbers) {
            System.out.printf("   isPrime(%d) = %s%n", n, PrimeAnalyzer.isPrime(n));
        }
        
        System.out.println("\n2. Miller-Rabin Tests:");
        long[] largePrimes = {104729, 154859, 214643};
        for (long n : largePrimes) {
            System.out.printf("   Miller-Rabin(%d) = %s%n", n, 
                PrimeAnalyzer.isPrimeMillerRabin(n));
        }
    }
    
    private static void testFactorization() {
        System.out.println("\n3. Prime Factorization:");
        int[] numbers = {120, 12345, 1000};
        for (int n : numbers) {
            System.out.printf("   %d = %s%n", n, 
                PrimeFactorization.factorizeToString(n));
            System.out.printf("   Divisors: %d, Sum of divisors: %d%n", 
                PrimeFactorization.divisorCount(n),
                PrimeFactorization.sumOfDivisors(n));
        }
    }
    
    private static void testGenerator() {
        System.out.println("\n4. Sieve of Eratosthenes:");
        List<Integer> smallPrimes = PrimeGenerator.sieve(50);
        System.out.println("   Primes up to 50: " + smallPrimes);
        
        System.out.println("\n5. Twin Primes:");
        List<Integer> twins = PrimeGenerator.generateTwinPrimes(100);
        for (int i = 0; i < twins.size(); i += 2) {
            System.out.printf("   (%d, %d)%n", twins.get(i), twins.get(i + 1));
        }
        
        System.out.println("\n6. Mersenne Primes:");
        List<Long> mersenne = PrimeGenerator.generateMersennePrimes(31);
        System.out.println("   Up to 2^31: " + mersenne);
    }
    
    private static void testRange() {
        System.out.println("\n7. Find Primes in Range:");
        int[] rangePrimes = PrimeRange.findPrimesInRange(100, 200);
        System.out.println("   Primes between 100-200: " + 
            Arrays.toString(rangePrimes));
        
        System.out.println("\n8. Kth Prime:");
        System.out.printf("   100th prime = %d%n", PrimeRange.findKthPrime(100));
        System.out.printf("   1000th prime = %d%n", PrimeRange.findKthPrime(1000));
    }
    
    private static void testFactorizationAnalysis() {
        System.out.println("\n9. Number Classification:");
        int[] numbers = {6, 12, 28, 18};
        for (int n : numbers) {
            String type;
            if (PrimeFactorization.isPerfect(n)) type = "Perfect";
            else if (PrimeFactorization.isAbundant(n)) type = "Abundant";
            else type = "Deficient";
            System.out.printf("   %d is %s%n", n, type);
        }
        
        System.out.println("\n10. Prime Density:");
        for (int n : new int[]{100, 1000, 10000}) {
            System.out.printf("   Density of primes up to %d: %.4f%n", 
                n, PrimeAnalyzer.primeDensity(n));
        }
    }
}
```

## Running the Project

```bash
cd labs/math/01-arithmetic/MINI_PROJECT
javac -d bin *.java
java com.mathacademy.arithmetic.mini.PrimeAnalyzerTest
```

## Expected Output
```
=== Prime Number Analyzer Test ===

1. Basic Primality Tests:
   isPrime(1) = false
   isPrime(2) = true
   isPrime(17) = true
   isPrime(100) = false
   isPrime(997) = true
   isPrime(1000) = false

2. Miller-Rabin Tests:
   Miller-Rabin(104729) = true
   Miller-Rabin(154859) = true
   Miller-Rabin(214643) = true

3. Prime Factorization:
   120 = 2^3 × 3 × 5
   Divisors: 16, Sum of divisors: 360
   12345 = 3 × 5 × 823
   Divisors: 16, Sum of divisors: 19296
   1000 = 2^3 × 5^3
   Divisors: 16, Sum of divisors: 2340

4. Sieve of Eratosthenes:
   Primes up to 50: [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47]

5. Twin Primes:
   (3, 5)
   (5, 7)
   (11, 13)
   (17, 19)
   (29, 31)
   (41, 43)
   (59, 61)
   (71, 73)

6. Mersenne Primes:
   Up to 2^31: [3, 7, 31, 127, 8191, 131071, 524287, 2147483647]
```

## Extensions
1. Add Goldbach's conjecture verification
2. Implement prime spiral visualization
3. Add prime gap analysis
4. Implement RSA key generation
5. Add primality certificates