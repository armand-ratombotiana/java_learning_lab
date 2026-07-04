# Visual Guide to Bloom Filters

## Empty Bloom Filter

```
m = 12 bits, all 0:
bits: [0][0][0][0][0][0][0][0][0][0][0][0]
       0  1  2  3  4  5  6  7  8  9  10 11
```

## Insert "cat"

```
hash functions: h₁, h₂, h₃
h₁("cat") = 2, h₂("cat") = 5, h₃("cat") = 9

bits: [0][0][1][0][0][1][0][0][0][1][0][0]
       0  1  2  3  4  5  6  7  8  9  10 11
               ↑        ↑              ↑
```

## Insert "dog"

```
h₁("dog") = 1, h₂("dog") = 5, h₃("dog") = 11

bits: [0][1][1][0][0][1][0][0][0][1][0][1]
       0  1  2  3  4  5  6  7  8  9  10 11
          ↑     ↑     ↑              ↑     ↑
        (new)    (already set)         (new)
```

## Query: "cat" (present)

```
h₁("cat") = 2 → bit 2 = 1 ✓
h₂("cat") = 5 → bit 5 = 1 ✓
h₃("cat") = 9 → bit 9 = 1 ✓
Result: probably present
```

## Query: "rat" (not present)

```
h₁("rat") = 3 → bit 3 = 0 ✗
Result: definitely NOT present
```

## Query: "bat" (false positive)

```
h₁("bat") = 1 → bit 1 = 1 ✓ (set by "dog")
h₂("bat") = 5 → bit 5 = 1 ✓ (set by "cat" and "dog")
h₃("bat") = 9 → bit 9 = 1 ✓ (set by "cat")
Result: probably present — FALSE POSITIVE!
```

## Fill Level Visualization

```
25% full:  [1][0][0][1][0][1][0][0][1][0][0][1]
25% set → low false positive rate

50% full:  [1][0][1][1][0][1][1][0][1][0][1][1]
50% set → moderate false positive rate

75% full:  [1][1][1][1][0][1][1][1][1][0][1][1]
75% set → high false positive rate
```

## Memory Comparison (1M elements, 1% FPP)

```
Bloom Filter:  ~12 MB  (BitSet)
HashSet:       ~64 MB  (for Integer keys)
HashSet:      ~160 MB  (for String keys, avg 20 chars)
```

## Optimal k vs m/n Ratio

```
m/n  |  k (optimal)
-----|-------------
2    |  1
4    |  3
6    |  4
8    |  6
10   |  7
12   |  8
14   |  10
16   |  11
```
