# Push-Relabel &amp; Min-Cost Flow — Overview

This lab covers the push-relabel algorithm for maximum flow and the successive shortest augmenting path algorithm for min-cost max flow. Push-relabel, also known as the preflow-push algorithm, offers a different paradigm from augmenting path methods and often outperforms Dinic on dense graphs. Min-cost max flow extends flow networks by assigning costs to edges and finding minimum-cost flows.

## Learning Objectives

- Implement the push-relabel max flow algorithm with gap heuristic
- Understand the relabel-to-front and FIFO variants
- Implement min-cost max flow using potentials (Johnson's reweighting)
- Apply capacity scaling to improve flow algorithm performance
- Find the minimum cut from a residual graph

## Prerequisites

- Graph representation (adjacency list)
- Network flow basics (Ford-Fulkerson, Dinic)
- Basic understanding of shortest paths with negative edges

## Estimated Time

- **Theory**: 90 minutes
- **Practice**: 120 minutes
- **Exercises**: 90 minutes
- **Total**: 5-6 hours