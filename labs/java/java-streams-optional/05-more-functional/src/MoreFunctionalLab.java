package com.java.streams.optional.lab05;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MoreFunctionalLab {

    public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return t -> cache.computeIfAbsent(t, fn);
    }

    public static void main(String[] args) {
        // Function composition
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> firstWord = s -> s.split("\\s+")[0];

        Function<String, String> process = trim.andThen(upper).andThen(firstWord);
        System.out.println("Composed: " + process.apply("  hello world  "));

        // Currying
        Function<Integer, Function<Integer, Integer>> add = a -> b -> a + b;
        Function<Integer, Integer> add5 = add.apply(5);
        System.out.println("add5(3): " + add5.apply(3));
        System.out.println("add5(10): " + add5.apply(10));

        // Memoized Fibonacci
        Function<Long, BigInteger> fib = memoize(new Function<>() {
            @Override
            public BigInteger apply(Long n) {
                if (n <= 1) return BigInteger.valueOf(n);
                return this.apply(n - 1).add(this.apply(n - 2));
            }
        });

        long start = System.currentTimeMillis();
        BigInteger result = fib.apply(100L);
        long time = System.currentTimeMillis() - start;
        System.out.println("fib(100) = " + result + " (" + time + "ms)");

        // Second call is instant (cache)
        start = System.currentTimeMillis();
        fib.apply(100L);
        time = System.currentTimeMillis() - start;
        System.out.println("fib(100) cached: " + time + "ms");
    }
}
