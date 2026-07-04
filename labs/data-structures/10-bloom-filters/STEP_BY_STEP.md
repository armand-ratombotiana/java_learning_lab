# Step by Step: Bloom Filter Operations

## Creating a Bloom Filter

```
Parameters:
  Expected insertions (n) = 100
  Desired false positive rate (P) = 0.01

Calculate m:
  m = -n × ln(P) / ln(2)²
  m = -100 × ln(0.01) / (0.693)²
  m = -100 × (-4.605) / 0.480
  m = 460.5 / 0.480
  m ≈ 959 bits ≈ 120 bytes

Calculate k:
  k = (m/n) × ln(2)
  k = (959/100) × 0.693
  k ≈ 6.6 → 7 hash functions
```

## Insertion Process

```
Element: "hello"
Hash values (k=3, m=16):
  h₁("hello") = 2
  h₂("hello") = 9
  h₃("hello") = 15

Initial bits: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
After set:    [0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1]
Positions:          2                    9                  15
```

## Query Process

```
Element: "world" (never inserted)
  h₁("world") = 8
  h₂("world") = 9
  h₃("world") = 14

Current bits: [0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1]
Check bit 8:  0 → FALSE → definitely not present
```

## False Positive Scenario

```
Inserted: "hello" sets bits [2, 9, 15]
Inserted: "world" sets bits [1, 5, 14]

Current bits: [0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1]
                   1   2      5           9               14  15

Query "bloom" (never inserted):
  h₁("bloom") = 2  → bit 2 = 1 ✓
  h₂("bloom") = 5  → bit 5 = 1 ✓
  h₃("bloom") = 9  → bit 9 = 1 ✓
  
All bits 1 → FALSE POSITIVE!
```

## Counting Bloom Filter Insert/Delete

```
Initial counters: [0, 0, 0, 0, 0, 0, 0, 0]

Insert "a" → positions [1, 3, 5]
Counters: [0, 1, 0, 1, 0, 1, 0, 0]

Insert "b" → positions [2, 3, 6]
Counters: [0, 1, 1, 2, 0, 1, 1, 0]
                     ↑ doubled

Delete "a" → positions [1, 3, 5]
Counters: [0, 0, 1, 1, 0, 0, 1, 0]
             ↓    ↓         ↓
          return to 0  return to 1  return to 0

Query "a" → position 3 = 1 → but position 1 = 0 → definitely absent ✓
```
