# Step by Step: Trie Operations

## Insert "apple"

```
Start at root
'a' not in root.children → create node 'a'
'p' not in 'a'.children → create node 'p'
'p' not in 'p'.children → create node 'p'
'l' not in 'p'.children → create node 'l'
'e' not in 'l'.children → create node 'e'
'e'.isEndOfWord = true
```

## Insert "app" (shares prefix with "apple")

```
Start at root
'a' in root.children → go to 'a'
'p' in 'a'.children → go to 'p'
'p' in 'p'.children → go to 'p'
'p'.isEndOfWord = true  (new terminal)

Tree:
    root
     |
     a
     |
     p
     |
     p ← isEndOfWord for "app"
     |
     l
     |
     e ← isEndOfWord for "apple"
```

## Search "app"

```
root → 'a' found → 'p' found → 'p' found → isEndOfWord? → true
```

## Search "ap"

```
root → 'a' found → 'p' found → isEndOfWord? → false
("ap" is a prefix but not a complete word)
```

## Search "ape"

```
root → 'a' found → 'p' found → 'e'? 'p' has no child 'e' → false
```

## Delete "app"

```
Traverse to 'app' node
Set isEndOfWord = false
Check: 'p' has child 'l' → cannot prune
Tree unchanged structurally but "app" no longer searchable.

Delete "apple" next:
Traverse to 'apple' node
Set isEndOfWord = false
'e' has no children → remove 'e' from 'l'
'l' has no children → remove 'l' from 'p'
'p' has no other children and isEndOfWord = false → remove 'p' from 'p'
'p' still has no other children and isEndOfWord = false → remove 'p' from 'a'
'a' has no other children → remove 'a' from root
Tree empty.
```

## Autocomplete for "ap"

```
Search prefix "ap" → get node for 'p'
DFS from 'p': 
  'p' has no terminal → continue
  'p' has child 'l' → "appl..."
  "appl" not terminal → continue
  'l' has child 'e' → "apple"
  "apple" is terminal → add to results
  'e' has no children → backtrack

Results: ["apple", "app"] (if both inserted)
```
