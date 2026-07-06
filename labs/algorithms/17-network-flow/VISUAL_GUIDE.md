# Visual Guide — Network Flow

## Residual Network After First Augmentation

Original: s-(10)->a, s-(5)->b, a-(15)->b, a-(10)->t, b-(10)->t

After s->a->t (10):
- s->a: 0 forward, 10 backward
- a->t: 0 forward, 10 backward
- Others unchanged

After s->b->t (5):
- s->b: 0 forward, 5 backward
- b->t: 5 forward, 5 backward
- a->b: still 15 forward

Final flow: 15 (saturated both s->a and s->b)

## Level Graph Visualization

Level 0: [s]
Level 1: [a, b] (both reachable from s with capacity)
Level 2: [t] (reachable from a or b)
