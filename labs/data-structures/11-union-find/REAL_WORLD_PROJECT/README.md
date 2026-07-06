# Real World Project: Minimum Spanning Tree for Network Design

Design a tool to plan cost-effective network infrastructure using Kruskal's algorithm with DSU.

## Scenario

A telecommunications company needs to connect N cities with fiber optic cables. Given the cost of laying cable between each pair of cities, find the minimum cost to connect all cities.

## Features

1. Load city locations and cable costs
2. Compute MST using Kruskal's algorithm
3. Visualize the resulting network topology
4. Handle edge cases: disconnected cities, varying costs
5. Output total cost and cable routes

## Architecture

- `NetworkPlanner.java` — main orchestrator
- `City.java` — city with coordinates
- `CableConnection.java` — weighted edge between cities
- Uses `DisjointSetUnion` for cycle detection
