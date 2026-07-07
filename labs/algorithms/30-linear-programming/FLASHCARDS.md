# Flashcards — Linear Programming

Q: What is a BFS?
A: Basic Feasible Solution: m basic variables, n-m non-basic (zero)

Q: Reduced cost?
A: c_j - c_B^T B^{-1} A_j; negative means entering variable improves objective

Q: Minimum ratio test?
A: min b_i / a_{iq} for a_{iq} > 0; determines leaving variable

Q: When is LP unbounded?
A: Entering column has all entries <= 0

Q: Two-phase method?
A: Phase I finds feasible basis; Phase II optimizes

Q: Shadow price?
A: Dual variable; rate of objective change per RHS unit

Q: Bland's rule prevents?
A: Cycling in degenerate pivots

Q: Weak duality?
A: c^T x >= b^T y for feasible primal x and dual y