package com.learning.lab14;

import java.util.function.*;

/**
 * Demonstrates variable capture in lambdas (effectively final variables).
 */
public class LambdaVariableCaptureExample {

    public static void showVariableCapture() {
        System.out.println("=== Lambda Variable Capture ===");

        String prefix = "Value: ";
        int multiplier = 2;

        Function<Integer, String> formatAndDouble = n -> prefix + (n * multiplier);

        System.out.println(formatAndDouble.apply(5));
        System.out.println(formatAndDouble.apply(10));

        int[] counter = {0};
        Runnable incrementor = () -> counter[0]++;
        incrementor.run();
        incrementor.run();
        incrementor.run();
        System.out.println("Counter after 3 increments: " + counter[0]);
    }
}
