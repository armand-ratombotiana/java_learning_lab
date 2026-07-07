# Code Deep Dive: Link-Cut Tree

## Source Structure

The implementation of the Link-Cut Tree follows a modular design pattern. Each class has a clear responsibility and the interactions between classes are well-defined.

## Key Classes

### Main Data Structure Class
This class provides the public API and manages the root of the structure. It delegates to helper methods for recursive operations while maintaining a clear separation of concerns.

### Node Class
The node class represents individual elements. It encapsulates the data plus all structural metadata. Node immutability is a design choice in persistent variants.

### Utility Classes
Helper classes provide supporting functionality such as hashing, comparisons, and iteration.

## Implementation Patterns

### Recursive Traversal
Most operations use recursion to traverse the structure. This naturally mirrors the hierarchical nature of tree-based structures.

### Iteration
For large structures, iterative approaches may be preferred to avoid stack overflow. The choice between recursion and iteration depends on expected structure size.

### Null Safety
Null references are used to represent absent children or empty structures. Careful null checking prevents NullPointerException.

## Performance-Critical Sections

The hottest code paths are typically:
- Comparisons during insertion and search
- Pointer updates during restructuring
- Memory allocation for new nodes

## Error Handling Strategy

The implementation uses exceptions for invalid operations:
- IllegalArgumentException for null keys
- IndexOutOfBoundsException for out-of-range positions
- Custom exceptions for domain-specific errors
