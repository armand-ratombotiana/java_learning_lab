# Failure Detection: Visual Guide

## Heartbeat Timeline

```
Node A heartbeat stream:
     │     │     │     │     │     │     │     │     │
     H     H     H     H     H     H     H     H     H
     └─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────▶ time

If timeout = 3× interval:
     │     │     │     │     │     │     │
     H     H     H     H           [TIMEOUT!]
                                   Declare Node A failed
```

## Phi Value Over Time

```
Phi  ▲
10   ┤
 9   ┤
 8   ┤ ╔══════════ Threshold = 8
 7   ┤ ║
 6   ┤ ║
 5   ┤ ║
 4   ┤ ║
 3   ┤ ║
 2   ┤ ║
 1   ┤╔═╝
 0   ┤║
     └───────────────────────────────▶ Time since last heartbeat
     Normal   Normal  Missing  Declared
     heartbeat pattern    suspicious  failed
```

## Gossip Propagation

```
Round 1: A→B    A: knows {B,C}  B: knows {A}  C: knows {}
Round 2: B→C    A: knows {B,C}  B: knows {A,C} C: knows {A,B}
Round 3: C→A    A: knows all    B: knows all    C: knows all
```
