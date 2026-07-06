# Visual Guide to Skip Lists

## Insertion of Key 4 with Level 2

Before:
`
L3: header â”€â”€â†’ 5
L2: header â”€â”€â†’ 3 â”€â”€â†’ 5
L1: header â†’ 1 â†’ 3 â†’ 5
L0: header â†’ 1 â†’ 2 â†’ 3 â†’ 5
`

Search path for 4: header(L3)â†’5(L2â†’drop to L2)â†’3(L2)â†’5(L1â†’drop to L1)â†’3(L1)â†’5(L0â†’drop to L0)â†’3(L0)â†’4 (found position)

After insertion:
`
L3: header â”€â”€â”€â”€â”€â”€â”€â”€â†’ 5
L2: header â”€â”€â†’ 3 â”€â”€â”€â†’ 5
L1: header â†’ 1 â†’ 3 â†’ 4 â†’ 5
L0: header â†’ 1 â†’ 2 â†’ 3 â†’ 4 â†’ 5
`
"@

System.Collections.Hashtable["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: SkipList.java

## Key Design Decisions

1. **Generic Types**: Supports any comparable key type
2. **Header Node**: A sentinel node with key = null
3. **MAX_LEVEL**: Set to 32 (supports up to 2^32 elements)
4. **Random Level**: Geometric distribution with p = 0.5
5. **Thread Safety**: Not thread-safe by default

## Search Implementation

The search algorithm traverses from the top level down, finding the predecessor at each level before moving to the next.
