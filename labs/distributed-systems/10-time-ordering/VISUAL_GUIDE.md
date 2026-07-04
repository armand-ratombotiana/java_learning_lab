# Time and Ordering: Visual Guide

## Lamport Clock Example

```
Process P1:    Process P2:    Process P3:
 1: event A     1: event C     1: event E
 2: send M1     2: recv M1     2:
 3:              3: event D     3: recv M2
 4: event B     4: send M2     4: event F
 5: recv M3     5:              5:
```

Cannot determine if A and C are concurrent.

## Vector Clock Example

```
P1: (1,0,0) → event A
P1: (2,0,0) → send M1 to P2

P2: (0,1,0) → event C
P2: (0,2,0) → recv M1 → (2,3,0)
P2: (2,3,1) → send M2 to P3

P3: (0,0,1) → event E
P3: (2,3,2) → recv M2 → (2,3,3)
P3: (2,3,3) → event F

Can determine: A → C? (1,0,0) < (0,2,0)? No → concurrent
```

## HLC Timeline
```
Physical: 1000  1001  1002  1003  1004
HLC(c,l): 1000   1001  1002  1003  1004
                     \      \
                      \ 1002,2 (multiple events in same ms)
                       \ 1002,3
