# LEETCODE_SOLUTION — 380. Insert Delete GetRandom O(1)

## Problem
Implement a data structure supporting insert, delete, and random get in O(1).

## Module Context
The solution uses `java.util.Random` and `java.util.HashMap` — both in `java.base`.

```java
class RandomizedSet {
    private Map<Integer, Integer> map = new HashMap<>();
    private List<Integer> list = new ArrayList<>();
    private Random rand = new Random();

    public boolean insert(int val) {
        if (map.containsKey(val)) return false;
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) return false;
        int idx = map.get(val);
        int last = list.get(list.size() - 1);
        list.set(idx, last);
        map.put(last, idx);
        list.remove(list.size() - 1);
        map.remove(val);
        return true;
    }

    public int getRandom() { return list.get(rand.nextInt(list.size())); }
}
```

## Key Insight
Module boundaries ensure `java.base` is always available; no `requires` needed.

## Complexity
- Time: O(1) per operation
- Space: O(n)
