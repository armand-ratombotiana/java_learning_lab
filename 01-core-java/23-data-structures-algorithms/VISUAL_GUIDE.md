# Data Structures & Algorithms - Visual Guide

This guide provides ASCII visualizations of core data structures and algorithms to help you build solid mental models of how they operate under the hood.

---

## 1. Data Structures

### Linked List
A sequential collection of nodes, where each node points to the next.

**Singly Linked List:**
```mermaid
graph LR
    Head((Head)) --> N1[Data | Next]
    N1 --> N2[Data | Next]
    N2 --> N3[Data | Next]
    N3 --> Null((null))
    
    classDef node fill:#f9f,stroke:#333,stroke-width:2px;
    class N1,N2,N3 node;
```

**Doubly Linked List:**
```mermaid
graph LR
    Null1((null)) <-- Prev --> N1[Prev | Data | Next]
    N1 <-- Prev/Next --> N2[Prev | Data | Next]
    N2 <-- Prev/Next --> N3[Prev | Data | Next]
    N3 --> Null2((null))
    
    classDef node fill:#bbf,stroke:#333,stroke-width:2px;
    class N1,N2,N3 node;
```

### Stack (LIFO - Last In, First Out)
Elements are added and removed from the "top".

```mermaid
graph TD
    Push((push 3)) -->|in| T[Top: 3]
    T --> M[2]
    M --> B[Bottom: 1]
    T -->|out| Pop((pop))
    
    subgraph Stack
    T
    M
    B
    end
    
    classDef action fill:#dfd,stroke:#333,stroke-width:2px;
    class Push,Pop action;
```

### Queue (FIFO - First In, First Out)
Elements are added at the "rear" and removed from the "front".

```mermaid
graph LR
    Enqueue((Enqueue 5)) -->|in| R[Rear: 4]
    R --> M2[3]
    M2 --> M1[2]
    M1 --> F[Front: 1]
    F -->|out| Dequeue((Dequeue))
    
    subgraph Queue
    R
    M2
    M1
    F
    end
    
    classDef action fill:#dfd,stroke:#333,stroke-width:2px;
    class Enqueue,Dequeue action;
```

### Binary Search Tree (BST)
A tree where the left child is smaller than the parent, and the right child is larger.

```mermaid
graph TD
    Root((8))
    N3((3))
    N10((10))
    N1((1))
    N6((6))
    N14((14))
    N4((4))
    N7((7))
    N13((13))

    Root --> N3
    Root --> N10
    N3 --> N1
    N3 --> N6
    N6 --> N4
    N6 --> N7
    N10 --> N14
    N14 --> N13

    classDef bst fill:#f9d0c4,stroke:#333,stroke-width:2px;
    class Root,N3,N10,N1,N6,N14,N4,N7,N13 bst;
```

### Graph (Adjacency Representation)
A non-linear data structure consisting of nodes (vertices) and edges.

```mermaid
graph LR
    A((A)) --- B((B))
    A --- C((C))
    B --- D((D))
    C --- D
    C --- E((E))
    D --- E
    
    classDef graphNode fill:#ffd0a0,stroke:#333,stroke-width:2px;
    class A,B,C,D,E graphNode;
```

### Hash Table
Key-value storage using a hash function, with collision resolution via chaining (Linked List).

```mermaid
graph LR
    subgraph Array/Buckets
        B0[0]
        B1[1]
        B2[2]
        B3[3]
    end

    subgraph Linked Lists (Chains)
        N1["'apple': 5"]
        N2["'banana': 2"]
        N3["'orange': 8"]
        N4["'grape': 1"]
    end

    B0 --> N1
    N1 --> N2
    B2 --> N3
    B3 --> N4
```

---

## 2. Algorithms

### Binary Search
Finding an element in a sorted array by repeatedly dividing the search interval in half.

**Searching for `7`:**
```mermaid
graph TD
    subgraph Step 1: L=0, Mid=4, R=8
    A1[1] --- A2[3] --- A3[4] --- A4[6] --- A5((7:Mid)) --- A6[8] --- A7[10] --- A8[13] --- A9[14]
    end
    
    subgraph Step 2: L=4, Mid=6, R=8
    B1((7:L)) --- B2[8] --- B3((10:Mid)) --- B4[13] --- B5((14:R))
    end
    
    subgraph Step 3: L=4, Mid=4, R=5
    C1(((7:Match!))) --- C2((8:R))
    end

    Step1 -->|6 < 7: Search Right| Step2
    Step2 -->|10 > 7: Search Left| Step3
    
    classDef match fill:#0f0,stroke:#333,stroke-width:3px;
    class C1 match;
```

### Merge Sort (Divide & Conquer)
Recursively divide the array into halves until each has one element, then merge them in sorted order.

```mermaid
graph TD
    S0["[ 38, 27, 43, 3, 9, 82, 10 ]"]
    
    S1L["[ 38, 27, 43 ]"]
    S1R["[ 3, 9, 82, 10 ]"]
    
    S2L1["[ 38, 27 ]"]
    S2L2["[ 43 ]"]
    S2R1["[ 3, 9 ]"]
    S2R2["[ 82, 10 ]"]
    
    S3L1["[38]"]
    S3L2["[27]"]
    S3R1["[3]"]
    S3R2["[9]"]
    S3R3["[82]"]
    S3R4["[10]"]

    S0 -->|Divide| S1L & S1R
    S1L -->|Divide| S2L1 & S2L2
    S1R -->|Divide| S2R1 & S2R2
    
    S2L1 -->|Divide| S3L1 & S3L2
    S2L2 -.->|Single| S2L2_copy["[43]"]
    S2R1 -->|Divide| S3R1 & S3R2
    S2R2 -->|Divide| S3R3 & S3R4

    S3L1 & S3L2 -->|Merge| M1L["[ 27, 38 ]"]
    S2L2_copy -.->|Merge| M1L_copy["[ 43 ]"]
    
    S3R1 & S3R2 -->|Merge| M1R1["[ 3, 9 ]"]
    S3R3 & S3R4 -->|Merge| M1R2["[ 10, 82 ]"]

    M1L & M1L_copy -->|Merge| M2L["[ 27, 38, 43 ]"]
    M1R1 & M1R2 -->|Merge| M2R["[ 3, 9, 10, 82 ]"]

    M2L & M2R -->|Final Merge| Result["[ 3, 9, 10, 27, 38, 43, 82 ]"]

    classDef divide fill:#f9f,stroke:#333;
    classDef merge fill:#bbf,stroke:#333;
    class S0,S1L,S1R,S2L1,S2L2,S2R1,S2R2,S3L1,S3L2,S3R1,S3R2,S3R3,S3R4 divide;
    class M1L,M1R1,M1R2,M2L,M2R,Result merge;
```

### Quick Sort
Pick a "pivot" and partition the array around it, recursively sorting the sub-arrays.

```mermaid
graph TD
    Start["Array: [ 8, 3, 1, 7, 0, 10, 2 ]"] --> Pivot["Pivot: 2"]
    Pivot --> Partition["Partition around 2"]
    
    Partition --> Smaller["Smaller: [ 1, 0 ]"]
    Partition --> Equal["Pivot: [ 2 ]"]
    Partition --> Larger["Larger: [ 8, 3, 7, 10 ]"]
    
    Smaller --> SortS["Sort [1, 0] -> [0, 1]"]
    Larger --> SortL["Sort [8, 3, 7, 10] -> [3, 7, 8, 10]"]
    
    SortS --> Combine
    Equal --> Combine
    SortL --> Combine
    
    Combine(("Result: [0, 1, 2, 3, 7, 8, 10]"))
    
    classDef pivot fill:#ff9,stroke:#333,stroke-width:2px;
    class Pivot,Equal pivot;
```

### Graph Traversal: Breadth-First Search (BFS)
Explores the neighbor nodes first, before moving to the next level neighbors. Uses a Queue.

```mermaid
graph TD
    subgraph Level 0
    A((A))
    end
    
    subgraph Level 1
    B((B))
    C((C))
    end
    
    subgraph Level 2
    D((D))
    E((E))
    end

    A --> B
    A --> C
    B --> D
    B --> E
    
    classDef visited fill:#a0d0ff,stroke:#333,stroke-width:2px;
    class A,B,C,D,E visited;
```

### Graph Traversal: Depth-First Search (DFS)
Explores as far as possible along each branch before backtracking. Uses a Stack (or recursion).

```mermaid
graph TD
    A((A: 1)) --> B((B: 2))
    B --> D((D: 3))
    D -.->|Backtrack| B
    B --> E((E: 4))
    E -.->|Backtrack| B
    B -.->|Backtrack| A
    A --> C((C: 5))
    
    classDef visited fill:#ffa0a0,stroke:#333,stroke-width:2px;
    class A,B,C,D,E visited;
```