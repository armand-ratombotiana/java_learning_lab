# Step by Step: Hash Table Operations

## Put with Separate Chaining

```
Capacity = 4, Load Factor = 0.75, Threshold = 3

put("cat", 1):
  hash("cat") = 98234
  index = 98234 & 3 = 2
  bucket[2] is empty → insert
  size = 1

put("dog", 2):
  hash("dog") = 98347
  index = 98347 & 3 = 3
  bucket[3] is empty → insert
  size = 2

put("bat", 3):
  hash("bat") = 98112
  index = 98112 & 3 = 2  (collision with "cat")
  chain: cat → bat
  size = 3
  load = 3/4 = 0.75 → threshold reached!

Resize triggered:
  new capacity = 8
  rehash all entries:
    "cat": 98234 & 7 = 2
    "bat": 98112 & 7 = 0
    "dog": 98347 & 7 = 3
  New table:
  [0] "bat"
  [1] null
  [2] "cat"
  [3] "dog"
  [4-7] null
```

## Get with Chaining

```
get("bat"):
  hash("bat") = 98112
  index = 98112 & 7 = 0
  bucket[0] = "bat" → key matches → return 3
```

## Get with Linear Probing

```
Table:
[0] null
[1] null
[2] "cat"
[3] "bat"
[4] "dog"
[5] null

get("dog"):
  hash("dog") = 98347
  index = 98347 % 8 = 3
  table[3] = "bat" → not match
  table[4] = "dog" → match! → return value
```

## Delete from Linear Probing

```
Before delete:
[2] "cat"
[3] "bat"
[4] "dog"

delete("bat"):
  hash("bat") = 98112 → index 0
  scan until "bat" found at index 3
  keys[3] = null, values[3] = null
  size--
  Reinsert "dog" (keys[4]):
    hash("dog") → index 3
    table[3] is empty → insert there
  Now:
  [2] "cat"
  [3] "dog"
  [4] null
```
