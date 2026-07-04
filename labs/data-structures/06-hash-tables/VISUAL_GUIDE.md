# Visual Guide to Hash Tables

## Separate Chaining

```
Key: "cat" → hash: 98234 → index: 2
Key: "dog" → hash: 98347 → index: 5
Key: "bat" → hash: 98112 → index: 2 (collision!)

Buckets:
[0] null
[1] null
[2] Entry("cat") → Entry("bat")
[3] null
[4] null
[5] Entry("dog")
[6] null
[7] null
```

## Linear Probing

```
Insert: "cat" → hash → index 2
Insert: "bat" → hash → index 2 (occupied!)
  Probe i=1: index 3 (empty) → insert here

Table:
[0] null
[1] null
[2] "cat"
[3] "bat"    ← placed here due to collision
[4] null
[5] "dog"
[6] null
[7] null

Search for "bat":
  1. hash("bat") → index 2
  2. table[2] = "cat" → not match
  3. table[3] = "bat" → found!
```

## Rehashing

```
Before resize (capacity=4, load=3/4=0.75, threshold reached):
[0] null
[1] "A"
[2] "B"
[3] "C"

After resize (capacity=8, rehash all):
hash("A") % 8 → index 1
hash("B") % 8 → index 2
hash("C") % 8 → index 3

[0] null
[1] "A"
[2] "B"
[3] "C"
[4] null
[5] null
[6] null
[7] null
```

## Hash Distribution Visualization

```
Good hash function (uniform):
[0]  ■■■
[1]  ■■■
[2]  ■■■■
[3]  ■■■
[4]  ■■■■
[5]  ■■■
[6]  ■■■
[7]  ■■■■

Bad hash function (clustered):
[0]  ■
[1]  ■
[2]  ■■■■■■■■■■
[3]  ■■■■■■■■■■
[4]  ■
[5]  ■
[6]  ■
[7]  ■■■■■■■■■■
```
