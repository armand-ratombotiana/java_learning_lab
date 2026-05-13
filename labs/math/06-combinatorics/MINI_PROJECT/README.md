# Mini Project: Lottery Number Generator

```java
package com.mathacademy.combinatorics.mini;

import java.util.*;

public class LotteryGenerator {
    public List<Integer> generate(int count, int max) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        while (numbers.size() < count) {
            int num = random.nextInt(max) + 1;
            if (!numbers.contains(num)) numbers.add(num);
        }
        return numbers;
    }
}
```