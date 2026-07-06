# Mental Models for Network Flow

## Augmenting Path — "Find and Fill"

Think of a water distribution system with pipes of varying diameters. Each pipe has a maximum capacity. You want to send as much water as possible from the source to the sink. You find a path from source to sink, fill it to its minimum pipe capacity, then look for the next path through remaining capacity. Keep going until no more water can get through.

## Residual Network — "The Second Direction"

The residual network represents remaining capacity forward AND backward. Backward residual edges are like allowing water to flow backward through a pipe to undo a previous decision. This is the key insight that makes flow algorithms correct: you can always reverse flow to make room for better alternatives.

## Level Graph (Dinic) — "Layered City"

Imagine a city with buildings at different heights. You can only move from a lower floor to a higher floor. The level graph in Dinic ensures you always make forward progress toward the sink, never sideways or backward. This prevents wasted exploration.

## Min-Cut — "The Bottleneck"

A cut is a set of edges that, if removed, disconnect the source from the sink. The min-cut is the smallest total capacity of such a set. It represents the fundamental bottleneck in the network. The max-flow equals the min-cut, so finding one gives you the other.

## Push-Relabel — "Overflow then Fix"

Unlike the disciplined augmenting path approach, push-relabel allows some nodes to be temporarily overloaded (have excess flow). The algorithm pushes excess to neighbors with lower height, and if stuck, increases the node's height (relabel) so flow can escape. This localized approach converges efficiently without global path searches.
