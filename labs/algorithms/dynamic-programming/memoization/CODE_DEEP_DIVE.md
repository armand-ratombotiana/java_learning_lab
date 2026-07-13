# Memoization Code Deep Dive

This lab provides pure Java implementations comparing the Naive Recursive approach, the Top-Down Memoization approach, and the Bottom-Up Tabulation approach for solving the Fibonacci sequence.

## 💻 Pure Java Implementation

```java file="labs/algorithms/dynamic-programming/memoization/SOLUTION/FibonacciDP.java"
package algorithms.dynamicprogramming.memoization;

import java.util.HashMap;
import java.util.Map;

/**
 * A demonstration of Dynamic Programming techniques.
 */
public class FibonacciDP {

    // Global counter to track how many times the recursive method is actually called
    private static long methodCalls = 0;

    /**
     * 1. Naive Recursion
     * Time Complexity: O(2^N)
     * Space Complexity: O(N) (Call Stack)
     */
    public static long fibNaive(int n) {
        methodCalls++;
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    /**
     * 2. Top-Down Memoization
     * Time Complexity: O(N)
     * Space Complexity: O(N) (Call Stack) + O(N) (HashMap)
     */
    public static long fibMemoized(int n, Map<Integer, Long> memo) {
        methodCalls++;
        
        // Base cases
        if (n <= 1) return n;
        
        // Check the cache FIRST
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        
        // If not in cache, compute it recursively
        long result = fibMemoized(n - 1, memo) + fibMemoized(n - 2, memo);
        
        // Save the result in the cache before returning
        memo.put(n, result);
        
        return result;
    }

    /**
     * 3. Bottom-Up Tabulation (Iterative)
     * Time Complexity: O(N)
     * Space Complexity: O(N) (Array)
     */
    public static long fibTabulated(int n) {
        if (n <= 1) return n;
        
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        
        return dp[n];
    }

    /**
     * 4. Bottom-Up Space Optimized
     * Time Complexity: O(N)
     * Space Complexity: O(1)
     */
    public static long fibOptimized(int n) {
        if (n <= 1) return n;
        
        long prev2 = 0;
        long prev1 = 1;
        long current = 0;
        
        for (int i = 2; i <= n; i++) {
            current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        
        return current;
    }

    public static void main(String[] args) {
        int n = 40; // Try changing this to 50 for the naive approach!
        
        System.out.println("--- 1. Naive Recursion ---");
        methodCalls = 0;
        long start = System.currentTimeMillis();
        long result1 = fibNaive(n);
        System.out.printf("Result: %d | Time: %d ms | Method Calls: %,d%n", 
                          result1, (System.currentTimeMillis() - start), methodCalls);
        
        System.out.println("\n--- 2. Top-Down Memoization ---");
        methodCalls = 0;
        start = System.currentTimeMillis();
        Map<Integer, Long> memo = new HashMap<>();
        long result2 = fibMemoized(n, memo);
        System.out.printf("Result: %d | Time: %d ms | Method Calls: %,d%n", 
                          result2, (System.currentTimeMillis() - start), methodCalls);
                          
        System.out.println("\n--- 3. Bottom-Up Tabulation ---");
        start = System.currentTimeMillis();
        long result3 = fibTabulated(n);
        System.out.printf("Result: %d | Time: %d ms%n", 
                          result3, (System.currentTimeMillis() - start));
    }
}
```

## 🔍 Key Takeaways
1. **The Method Call Explosion**: If you run this code for $N=40$, the Naive approach will take several seconds and output over **331 million** method calls. The Memoized approach will output the exact same result in 0 milliseconds, with exactly **79** method calls. This perfectly demonstrates the $O(2^N)$ vs $O(N)$ mathematical proof.
2. **Space Optimization**: Look at `fibOptimized`. The recurrence relation $F(n) = F(n-1) + F(n-2)$ only requires the *previous two* values to calculate the next one. We don't actually need to store the entire array of size $N$ in memory. We can just use two variables (`prev1` and `prev2`) and update them as we iterate. This reduces the Space Complexity from $O(N)$ to $O(1)$.