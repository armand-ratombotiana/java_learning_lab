# Mental Models for Graphs

## The Road Map

Cities are vertices. Roads are edges. The distance between cities is the edge weight. Finding the shortest route from A to B is shortest-path search. A one-way street is a directed edge.

## The Social Network

People are vertices. Friendships are undirected edges. "Follows" are directed edges. The "friends of friends" recommendation is a 2-hop traversal. Finding influential people is PageRank.

## The Prerequisite Graph

Courses are vertices. "Course A requires Course B" is a directed edge B → A. The entire curriculum is a DAG. Finding a valid course order is topological sort. A cycle means contradictory prerequisites.

## The Web

Web pages are vertices. Hyperlinks are directed edges. BFS from a start page finds all pages reachable within k clicks. PageRank uses the graph structure to rank pages by importance.

## The Network Flow

Water pipes are weighted directed edges. Junctions are vertices. The maximum water that can flow from source to sink is the max flow. Finding bottlenecks is min cut.

## Adjacency List vs Matrix Visualization

```
Adjacency List:
0: → 1 → 2
1: → 0 → 3
2: → 0 → 3
3: → 1 → 2

Adjacency Matrix (undirected, 4 vertices):
    0  1  2  3
0  [0, 1, 1, 0]
1  [1, 0, 0, 1]
2  [1, 0, 0, 1]
3  [0, 1, 1, 0]
```
