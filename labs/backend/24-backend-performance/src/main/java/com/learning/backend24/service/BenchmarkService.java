package com.learning.backend24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class BenchmarkService {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkService.class);

    public double computePi(int iterations) {
        long insideCircle = 0;
        for (int i = 0; i < iterations; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if (x * x + y * y <= 1) insideCircle++;
        }
        return 4.0 * insideCircle / iterations;
    }

    public String stringConcatBenchmark(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += String.valueOf(i);
        }
        return result;
    }

    public String stringBuilderBenchmark(int count) {
        StringBuilder sb = new StringBuilder(count * 10);
        for (int i = 0; i < count; i++) {
            sb.append(i);
        }
        return sb.toString();
    }
}
