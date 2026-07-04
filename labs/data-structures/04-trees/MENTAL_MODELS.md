# Mental Models for Trees

## The Family Tree

A tree is like a family genealogy:
- One ancestor (root) at the top
- Parents have children; children of the same parent are siblings
- Grandchildren form subtrees branching downward
- A person without children is a leaf

## The Org Chart

A company hierarchy:
- CEO is the root
- VPs are children of the CEO
- Directors report to VPs
- Individual contributors are leaves
- Reporting lines are edges

## The Recursive Definition

```
A tree is either:
  - null (empty)
  - A root node with data, a left subtree, and a right subtree
    where both subtrees are themselves trees
```

This recursive definition is the key to writing tree algorithms.

## The Nested Boxes

A binary tree is like boxes containing smaller boxes:
- The root box contains data
- Inside are two smaller boxes (left and right children)
- Each of those contains data and two even smaller boxes
- A leaf box contains data but no inner boxes

## Directory Structure

```
/ = root
├── home/
│   ├── user/
│   │   ├── docs/
│   │   ├── photos/
│   │   └── music/
│   └── shared/
└── etc/
    ├── config/
    └── logs/
```

This is a tree. Navigating directories is tree traversal. Finding a file is tree search.
