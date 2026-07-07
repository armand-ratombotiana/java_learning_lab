# Push-Relabel Flow — Mental Models

## Plumbing System with Overflow

The push-relabel algorithm is like a plumbing system where water can temporarily overflow at intermediate nodes. Unlike augmenting paths (which only move as much water as the smallest pipe allows), push-relabel lets water accumulate, then pushes it further downhill. Heights represent elevation: water only flows downhill (from higher to lower elevation). If water gets stuck, the elevation of that node is raised (relabeled).

## Elevation as Distance Markers

Heights in push-relabel are like distance markers on a hike. The sink is at sea level (height 0). The source starts at height n. Heights estimate the distance to the sink. Water flows from higher to lower heights. If a node's height is higher than all its neighbors, water is trapped (local minimum), so we increase the node's height until it can flow again.

## Gap Heuristic as Running out of Rungs

Imagine a ladder where a rung at height h is missing (no node has that height). Nodes above the gap cannot reach the ground (sink) without climbing through that missing height. These nodes are essentially isolated from the sink, so we can immediately set their height to n (very high), allowing their flow to be pushed back toward the source.

## Min-Cost Flow as Toll Roads

In min-cost flow, each edge is a toll road with both a capacity (how many cars can pass) and a toll per car. The goal is to send the required amount of cars from source to sink at minimum total toll cost. Potentials are like a toll discount program: each node has a discount offer, and the reduced cost is the toll minus the discount you get from the start node plus the discount you give to the end node.

## Residual Graph as Two-Way Streets

The residual graph represents both forward flow capacity (how much more can go from u to v) and backward flow capacity (how much existing flow can be undone, representing reverse movement). This is like a two-way street with separate lanes for each direction; using one lane reduces its capacity and increases the capacity of the opposite lane.