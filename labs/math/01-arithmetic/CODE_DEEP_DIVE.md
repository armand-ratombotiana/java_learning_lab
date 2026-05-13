# Code Deep Dive: Arithmetic Implementations in Java

## 1. Basic Operations Implementation

### 1.1 ArithmeticOperations Class

```java
package com.mathacademy.arithmetic;

public class ArithmeticOperations {
    
    public static long add(long a, long b) {
        return a + b;
    }
    
    public static long subtract(long a, long b) {
        return a - b;
    }
    
    public static long multiply(long a, long b) {
        return a * b;
    }
    
    public static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
    
    public static long[] divideWithRemainder(long dividend, long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        long quotient = dividend / divisor;
        long remainder = dividend % divisor;
        return new long[]{quotient, remainder};
    }
}
```

### 1.2 Exponentiation

```java
package com.mathacademy.arithmetic;

public class Exponentiation {
    
    public static long power(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative");
        }
        if (exponent == 0) {
            return 1;
        }
        
        long result = 1;
        long baseLong = base;
        int exp = exponent;
        
        while (exp > 0) {
            if (exp % 2 == 1) {
                result *= baseLong;
            }
            baseLong *= baseLong;
            exp /= 2;
        }
        return result;
    }
    
    public static double powerWithNegativeExponent(int base, int exponent) {
        if (exponent == 0) {
            return 1;
        }
        if (exponent < 0) {
            return 1.0 / power(base, -exponent);
        }
        return power(base, exponent);
    }
    
    public static long fastPowerRecursive(int base, int exponent) {
        if (exponent == 0) return 1;
        if (exponent == 1) return base;
        
        long temp = fastPowerRecursive(base, exponent / 2);
        
        if (exponent % 2 == 0) {
            return temp * temp;
        } else {
            return temp * temp * base;
        }
    }
}
```

## 2. Number Theory Implementation

### 2.1 Prime Number Utilities

```java
package com.mathacademy.arithmetic;

import java.util.ArrayList;
import java.util.List;

public class PrimeUtils {
    
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
    
    public static List<Integer> sieveOfEratosthenes(int limit) {
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
        List<Integer> basePrimes = sieveOfEratosthenes((int) sqrtHigh);
        
        for (int prime : basePrimes) {
            long start = Math.max(prime * prime, 
                ((low + prime - 1) / prime) * prime);
            
            for (long j = start; j <= high; j += prime) {
                isComposite[(int)(j - low)] = true;
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
    
    public static int primeCountApproximation(long x) {
        return (int) (x / Math.log(x));
    }
}
```

### 2.2 GCD and LCM

```java
package com.mathacademy.arithmetic;

public class GCDLCM {
    
    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    public static long gcdRecursive(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return b == 0 ? a : gcdRecursive(b, a % b);
    }
    
    public static long lcm(long a, long b) {
        return Math.abs(a) / gcd(a, b) * Math.abs(b);
    }
    
    public static long[] extendedGCD(long a, long b) {
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        
        long[] result = extendedGCD(b, a % b);
        long gcd = result[0];
        long x1 = result[1];
        long y1 = result[2];
        
        long x = y1;
        long y = x1 - (a / b) * y1;
        
        return new long[]{gcd, x, y};
    }
    
    public static long gcdMultiple(long... numbers) {
        long result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = gcd(result, numbers[i]);
        }
        return result;
    }
}
```

## 3. Modular Arithmetic

### 3.1 Modular Operations

```java
package com.mathacademy.arithmetic;

public class ModularArithmetic {
    
    public static long modAdd(long a, long b, long mod) {
        return ((a % mod) + (b % mod)) % mod;
    }
    
    public static long modSubtract(long a, long b, long mod) {
        long result = ((a % mod) - (b % mod)) % mod;
        return result < 0 ? result + mod : result;
    }
    
    public static long modMultiply(long a, long b, long mod) {
        return ((a % mod) * (b % mod)) % mod;
    }
    
    public static long modPow(long base, long exponent, long mod) {
        long result = 1;
        long baseMod = base % mod;
        
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = modMultiply(result, baseMod, mod);
            }
            baseMod = modMultiply(baseMod, baseMod, mod);
            exponent /= 2;
        }
        return result;
    }
    
    public static long modInverse(long a, long mod) {
        long[] extended = GCDLCM.extendedGCD(a, mod);
        long gcd = extended[0];
        long x = extended[1];
        
        if (gcd != 1) {
            throw new ArithmeticException("Modular inverse does not exist");
        }
        
        long result = (x % mod + mod) % mod;
        return result;
    }
    
    public static long modFactorial(long n, long mod) {
        long result = 1;
        for (long i = 2; i <= n; i++) {
            result = (result * i) % mod;
        }
        return result;
    }
}
```

### 3.2 Chinese Remainder Theorem

```java
package com.mathacademy.arithmetic;

public class ChineseRemainderTheorem {
    
    public static long[] solve(int[] remainders, int[] moduli) {
        if (remainders.length != moduli.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        
        long prod = 1;
        for (int m : moduli) {
            prod *= m;
        }
        
        long result = 0;
        for (int i = 0; i < remainders.length; i++) {
            long Mi = prod / moduli[i];
            long yi = ModularArithmetic.modInverse(Mi % moduli[i], moduli[i]);
            result = (result + remainders[i] * Mi * yi) % prod;
        }
        
        return new long[]{result, prod};
    }
    
    public static long solvePairs(long a1, long m1, long a2, long m2) {
        long[] result = solve(new int[]{(int)a1, (int)a2}, new int[]{(int)m1, (int)m2});
        return result[0];
    }
}
```

## 4. Base Conversion

### 4.1 BaseConverter Class

```java
package com.mathacademy.arithmetic;

import java.util.ArrayList;
import java.util.List;

public class BaseConverter {
    
    public static String toBase(int number, int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("Base must be between 2 and 36");
        }
        
        if (number == 0) {
            return "0";
        }
        
        boolean negative = number < 0;
        number = Math.abs(number);
        
        String digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        
        while (number > 0) {
            int remainder = number % base;
            result.append(digits.charAt(remainder));
            number /= base;
        }
        
        if (negative) {
            result.append("-");
        }
        
        return result.reverse().toString();
    }
    
    public static int fromBase(String number, int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("Base must be between 2 and 36");
        }
        
        String num = number.toUpperCase();
        int result = 0;
        int power = 1;
        
        for (int i = num.length() - 1; i >= 0; i--) {
            char c = num.charAt(i);
            int digit;
            
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'A' && c <= 'Z') {
                digit = c - 'A' + 10;
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
            
            if (digit >= base) {
                throw new IllegalArgumentException("Digit " + digit + 
                    " is invalid for base " + base);
            }
            
            result += digit * power;
            power *= base;
        }
        
        return result;
    }
    
    public static String convert(String number, int fromBase, int toBase) {
        int decimal = fromBase(number, fromBase);
        return toBase(decimal, toBase);
    }
    
    public static List<Integer> toBaseArray(int number, int base) {
        List<Integer> digits = new ArrayList<>();
        
        if (number == 0) {
            digits.add(0);
            return digits;
        }
        
        while (number > 0) {
            digits.add(number % base);
            number /= base;
        }
        
        java.util.Collections.reverse(digits);
        return digits;
    }
}
```

## 5. Fibonacci and Special Sequences

### 5.1 Fibonacci Implementations

```java
package com.mathacademy.arithmetic;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Fibonacci {
    
    public static long fibonacciIterative(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }
    
    public static long fibonacciRecursive(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }
    
    public static long fibonacciMemoized(int n) {
        return fibonacciMemoizedHelper(n, new HashMap<>());
    }
    
    private static long fibonacciMemoizedHelper(int n, Map<Integer, Long> memo) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        
        long result = fibonacciMemoizedHelper(n - 1, memo) + 
                     fibonacciMemoizedHelper(n - 2, memo);
        memo.put(n, result);
        return result;
    }
    
    public static long fibonacciMatrix(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        
        long[][] matrix = {{1, 1}, {1, 0}};
        long[][] result = matrixPower(matrix, n - 1);
        return result[0][0];
    }
    
    private static long[][] matrixPower(long[][] matrix, int power) {
        if (power == 1) return matrix;
        
        long[][] result = {{1, 0}, {0, 1}};
        
        while (power > 0) {
            if (power % 2 == 1) {
                result = matrixMultiply(result, matrix);
            }
            matrix = matrixMultiply(matrix, matrix);
            power /= 2;
        }
        
        return result;
    }
    
    private static long[][] matrixMultiply(long[][] a, long[][] b) {
        return new long[][]{
            {a[0][0] * b[0][0] + a[0][1] * b[1][0], 
             a[0][0] * b[0][1] + a[0][1] * b[1][1]},
            {a[1][0] * b[0][0] + a[1][1] * b[1][0], 
             a[1][0] * b[0][1] + a[1][1] * b[1][1]}
        };
    }
    
    public static BigInteger fibonacciBig(int n) {
        if (n <= 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        
        BigInteger prev = BigInteger.ZERO;
        BigInteger curr = BigInteger.ONE;
        
        for (int i = 2; i <= n; i++) {
            BigInteger next = prev.add(curr);
            prev = curr;
            curr = next;
        }
        return curr;
    }
    
    public static double fibonacciClosedForm(int n) {
        double phi = (1 + Math.sqrt(5)) / 2;
        double phiBar = (1 - Math.sqrt(5)) / 2;
        return (Math.pow(phi, n) - Math.pow(phiBar, n)) / Math.sqrt(5);
    }
}
```

## 6. Rational Number Arithmetic

### 6.1 Fraction Class

```java
package com.mathacademy.arithmetic;

import java.util.Objects;

public class Fraction {
    private final long numerator;
    private final long denominator;
    
    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        
        long gcd = GCDLCM.gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= gcd;
        denominator /= gcd;
        
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public static Fraction add(Fraction a, Fraction b) {
        long newNumerator = a.numerator * b.denominator + 
                            b.numerator * a.denominator;
        long newDenominator = a.denominator * b.denominator;
        return new Fraction(newNumerator, newDenominator);
    }
    
    public static Fraction subtract(Fraction a, Fraction b) {
        long newNumerator = a.numerator * b.denominator - 
                            b.numerator * a.denominator;
        long newDenominator = a.denominator * b.denominator;
        return new Fraction(newNumerator, newDenominator);
    }
    
    public static Fraction multiply(Fraction a, Fraction b) {
        return new Fraction(a.numerator * b.numerator, 
                           a.denominator * b.denominator);
    }
    
    public static Fraction divide(Fraction a, Fraction b) {
        if (b.numerator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new Fraction(a.numerator * b.denominator, 
                           a.denominator * b.numerator);
    }
    
    public double toDouble() {
        return (double) numerator / denominator;
    }
    
    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }
        return numerator + "/" + denominator;
    }
    
    public long getNumerator() { return numerator; }
    public long getDenominator() { return denominator; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Fraction) {
            Fraction other = (Fraction) obj;
            return numerator == other.numerator && 
                   denominator == other.denominator;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }
}
```

## 7. Number System Conversion Utilities

### 7.1 NumberSystem Class

```java
package com.mathacademy.arithmetic;

import java.util.ArrayList;
import java.util.List;

public class NumberSystem {
    
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isRational(String s) {
        return s.matches("-?\\d+/?\\d+");
    }
    
    public static List<Long> primeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        long temp = n;
        
        for (long i = 2; i * i <= temp; i++) {
            while (temp % i == 0) {
                factors.add(i);
                temp /= i;
            }
        }
        
        if (temp > 1) {
            factors.add(temp);
        }
        
        return factors;
    }
    
    public static long factorial(long n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n > 20) throw new IllegalArgumentException("n too large for long");
        
        long result = 1;
        for (long i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    public static double factorialDouble(long n) {
        double result = 1;
        for (long i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
```

## 8. Testing and Validation

### 8.1 ArithmeticTest Class

```java
package com.mathacademy.arithmetic;

public class ArithmeticTest {
    
    public static void main(String[] args) {
        testGCD();
        testPrime();
        testFibonacci();
        testModularArithmetic();
        testBaseConversion();
        testFraction();
    }
    
    private static void testGCD() {
        System.out.println("Testing GCD:");
        System.out.println("gcd(48, 18) = " + GCDLCM.gcd(48, 18));
        System.out.println("gcd(1071, 462) = " + GCDLCM.gcd(1071, 462));
        
        long[] extended = GCDLCM.extendedGCD(35, 15);
        System.out.println("Extended GCD(35, 15): gcd=" + extended[0] + 
                          ", x=" + extended[1] + ", y=" + extended[2]);
    }
    
    private static void testPrime() {
        System.out.println("\nTesting Primes:");
        System.out.println("isPrime(17) = " + PrimeUtils.isPrime(17));
        System.out.println("isPrime(1) = " + PrimeUtils.isPrime(1));
        
        System.out.println("Primes up to 100: " + 
            PrimeUtils.sieveOfEratosthenes(100));
    }
    
    private static void testFibonacci() {
        System.out.println("\nTesting Fibonacci:");
        for (int i = 0; i <= 10; i++) {
            System.out.print(Fibonacci.fibonacciIterative(i) + " ");
        }
        System.out.println();
        System.out.println("F(50) = " + Fibonacci.fibonacciBig(50));
    }
    
    private static void testModularArithmetic() {
        System.out.println("\nTesting Modular Arithmetic:");
        System.out.println("2^10 mod 1000 = " + 
            ModularArithmetic.modPow(2, 10, 1000));
        System.out.println("3^100 mod 13 = " + 
            ModularArithmetic.modPow(3, 100, 13));
        System.out.println("Inverse of 3 mod 7 = " + 
            ModularArithmetic.modInverse(3, 7));
    }
    
    private static void testBaseConversion() {
        System.out.println("\nTesting Base Conversion:");
        System.out.println("255 in binary = " + BaseConverter.toBase(255, 2));
        System.out.println("255 in hex = " + BaseConverter.toBase(255, 16));
        System.out.println("101010 in binary to decimal = " + 
            BaseConverter.fromBase("101010", 2));
    }
    
    private static void testFraction() {
        System.out.println("\nTesting Fraction:");
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);
        System.out.println(a + " + " + b + " = " + Fraction.add(a, b));
        System.out.println(a + " * " + b + " = " + Fraction.multiply(a, b));
    }
}
```