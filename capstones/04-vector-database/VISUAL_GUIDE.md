# Visual Guide: Vector Database

## HNSW Graph Structure
```
Layer 3 (top):  [A] ----- [B]
                   \       /
Layer 2:     [C] - [D] - [E] - [F]
                \   |   /   |   /
Layer 1:   [G] - [H] - [I] - [J] - [K]
             |   / |   | \   |   \   |
Layer 0:  [L]-[M]-[N]-[O]-[P]-[Q]-[R]-[S]
(bottom)
```

## Search Flow
```
Query Vector -> Start at entry point (layer top)
    -> Greedily traverse each layer (find nearest at current layer)
    -> Descend to next layer
    -> Repeat until layer 0
    -> Expand efSearch nearest neighbors at layer 0
    -> Apply optional metadata filter
    -> Return top-K results
```

## Insert Flow
```
Vector -> Assign random level (layer 0..max)
    -> Find entry point at top layer
    -> Navigate down to assigned level, collecting nearest neighbors
    -> Connect bidirectional to efConstruction neighbors at each level
    -> Update entry point if vector is nearest
```
