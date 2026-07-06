# Visual Guide — Randomized Algorithms

## Quickselect Partitioning

Before: [7, 3, 9, 2, 8, 5, 1, 4] pivot=2 (index 3)
After:  [1, 2, 7, 3, 9, 8, 5, 4]
          ↑ pivot at correct position

## Reservoir Sampling Walkthrough

Stream: [a, b, c, d, e, f, g, h], k=3
i=0: reservoir = [a, -, -]
i=1: reservoir = [a, b, -]
i=2: reservoir = [a, b, c]
i=3: d replaces c with prob 3/4 -> [a, b, d] (say)
i=4: e replaces a with prob 3/5 -> [e, b, d] (say)
i=5: f replaces nothing (prob 3/6 = 50%) -> [e, b, d] (no change)
i=6: g replaces b with prob 3/7 -> [e, g, d] (say)
i=7: h replaces d with prob 3/8 -> [e, g, h] (say)

Each original element has probability 3/8 of being in final reservoir.
