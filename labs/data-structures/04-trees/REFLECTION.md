# Reflection: Trees

## What I Learned

- Trees model hierarchical relationships and enable O(log n) search when balanced
- Binary tree traversals (pre/in/post/level) each have distinct use cases
- BSTs maintain ordering but can degenerate to linked lists
- Recursive algorithms are natural for tree problems but risk stack overflow
- Iterative traversals use explicit stacks/queues for recursion-free processing
- Tree height, depth, diameter, and lowest common ancestor are fundamental properties

## Questions to Consider

1. Why does inorder traversal of a BST produce sorted output?
2. When would you choose a BST over a hash table for search?
3. How can you convert a recursive tree algorithm to an iterative one?
4. What is the relationship between tree height and the number of nodes?
5. How would you implement a self-balancing tree?

## Connections

Trees connect to:
- **AVL/Red-Black trees** (self-balancing variants)
- **Heaps** (complete binary tree, array-backed)
- **Tries** (string prefix trees)
- **Graphs** (trees are acyclic connected graphs)
- **B-trees** (high-branching trees for databases)
- **Segment/Fenwick trees** (range query structures)

## Self-Assessment

- [ ] Can implement all four tree traversals (recursive and iterative)
- [ ] Can compute tree height, diameter, LCA
- [ ] Can validate a BST and find kth smallest
- [ ] Can serialize and deserialize a tree
- [ ] Understand recursive tree algorithm patterns
- [ ] Know when to use tree vs other structures
