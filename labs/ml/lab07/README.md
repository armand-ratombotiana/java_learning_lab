# Lab 07: K-Means & Hierarchical Clustering

## Topics Covered
- K-Means Algorithm
- Inertia (Within-Cluster Sum of Squares)
- Elbow Method
- Silhouette Score
- Dendrograms
- Agglomerative Clustering

## Objective
Implement K-Means from scratch and explore hierarchical clustering concepts.

## Key Concepts
| Concept | Description |
|---|---|
| K-Means | Partition n points into K clusters by minimizing WCSS |
| Inertia | Σ Σ ‖x − μ‖² for each cluster |
| Elbow Method | Plot inertia vs K; choose the "elbow" point |
| Silhouette | (b−a) / max(a,b) — measures cluster cohesion vs separation |
| Dendrogram | Tree diagram showing hierarchical merges |

## Files
- `GUIDE.md` — Step-by-step lab walkthrough
- `INTERVIEW.md` — Interview Q&A on Clustering
- `src/com/ml/lab07/Main.java` — Compilable Java source with test cases
