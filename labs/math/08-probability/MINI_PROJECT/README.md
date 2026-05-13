# Mini Project: Dice Rolling Simulator

```java
package com.mathacademy.probability.mini;

import java.util.*;

public class DiceSimulator {
    public Map<Integer, Integer> rollMany(int rolls, int dice) {
        Map<Integer, Integer> counts = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < rolls; i++) {
            int sum = 0;
            for (int d = 0; d < dice; d++) sum += random.nextInt(6) + 1;
            counts.put(sum, counts.getOrDefault(sum, 0) + 1);
        }
        return counts;
    }
}
```