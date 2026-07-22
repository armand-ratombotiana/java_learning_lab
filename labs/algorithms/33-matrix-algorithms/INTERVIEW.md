# Interview Questions: Matrix Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 48 Rotate Image | Medium | Google, Meta, Amazon, Microsoft | Layer-by-layer / transpose |
| LC 54 Spiral Matrix | Medium | Google, Meta, Amazon, Microsoft | Direction simulation |
| LC 73 Set Matrix Zeroes | Medium | Google, Meta, Amazon, Microsoft | In-place markers |
| LC 240 Search 2D Matrix II | Medium | Google, Meta, Amazon | Search from corner |
| LC 378 Kth Smallest in Sorted Matrix | Medium | Google, Amazon, Meta | Binary search / heap |
| LC 311 Sparse Matrix Multiplication | Medium | Google, Amazon | Compressed representation |
| LC 348 Design Tic-Tac-Toe | Medium | Google, Microsoft, Meta | Row/col/diag counters |

## NeetCode Reference
- LC 48 Rotate Image (NeetCode 150)
- LC 54 Spiral Matrix (NeetCode 150)
- LC 73 Set Matrix Zeroes (NeetCode 150)
- LC 240 Search 2D Matrix II (NeetCode 150)
- LC 378 Kth Smallest in Sorted Matrix (NeetCode 150)

## Company-Specific Questions
### Google
- Rotate Image and Spiral Matrix are Google classics with follow-ups
- Design an algorithm for matrix multiplication with cache-friendly blocking
- Sparse matrix operations are Google-scale problems
- How would you represent and multiply matrices in a distributed system?

### Microsoft
- Set Matrix Zeroes for in-place algorithm design
- How does Excel handle large matrix computations?
- Design a matrix library for C# (translation to Java)
- Tic-Tac-Toe winner detection with O(1) per move

### Meta
- Rotate Image in-place is a Meta favorite
- Matrix traversal problems (Spiral Matrix) test direction management
- Search in sorted matrix with O(m+n) complexity

### Amazon
- Sparse matrix multiplication for recommendation systems
- How would you compute product co-occurrence matrices?
- Kth smallest for finding median ratings across products

### Apple
- Matrix operations for graphics rendering (Core Graphics, Metal)
- Memory-efficient transpose for large images
- How would you rotate a bitmap image efficiently?
- Accelerate matrix operations using vDSP/Accelerate framework

### Oracle
- How does Oracle implement matrix operations in database?
- Design a matrix storage scheme for data warehouse cubes
- Explain Oracle's pivot/unpivot operations as matrix transpose

## Real Production Scenarios
- Scenario 1: Image processing pipeline - applying rotation and transformation matrices to process millions of images per day for a social media platform's thumbnail generation
- Scenario 2: Recommendation system - computing sparse matrix factorization (e.g., ALS) for a user-item rating matrix with 10^8 non-zero entries collaboratively filtering product recommendations
- Scenario 3: Game state validation - debugging a Tic-Tac-Toe win detection that fails on diagonal wins when the board is represented as a 1D array instead of 2D

## Interview Tips
- In-place operations: transpose + reverse for rotation, use first row/col as markers for zeroes
- Matrix traversal: direction arrays (dr, dc) with boundary conditions and direction change
- Sparse matrices: store as List of non-zero entries (COO) or CSR representation
- Common edge cases: single row, single column, empty matrix, non-square matrices

## Java-Specific Considerations
- Represent matrices as `int[][]` or `double[][]`; row-major order is the default
- For sparse matrices: `HashMap<Integer, HashMap<Integer, Double>>` or `List<Entry>` (COO)
- `Arrays.deepToString()` and `Arrays.deepEquals()` for 2D array debugging
- Pitfall: `matrix.length` vs `matrix[0].length` confusion for non-square matrices
- Pitfall: modifying the matrix while iterating (use saved state or two-pass approach)
- For performance: iterate rows first (row-major): `for (int[] row : matrix) { for (int val : row) { ... } }`
