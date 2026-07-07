# Matrix Algorithms — Mental Models

## Strassen as Trading Multiplications for Additions

Strassen's algorithm is like a factory that can either do 8 expensive operations (multiplications) or 7 expensive ones plus 18 cheaper operations (additions/subtractions). By cleverly combining matrix blocks using addition before multiplication, Strassen reduces the number of multiplications at the cost of more additions. Since multiplication is more expensive, this tradeoff is profitable for large matrices.

## Gaussian Elimination as Solving a Maze

Gaussian elimination solves a system of equations like finding a path through a maze by removing walls. Each row operation eliminates a variable from other equations (removes a wall). When you finish, the last equation gives you the last variable directly (a clear path), and you work backward to find all variables.

## LU Decomposition as Recording Your Steps

LU decomposition is like recording a cooking recipe: L records the "amounts" of each row operation (the ingredients and quantities), and U is the final upper-triangular result (the prepared dish). Given a new right-hand side (substitute ingredient), you can follow the recorded row operations (L steps) and back-substitute (U steps) to find the solution.

## SVD as Finding the Best View

SVD finds the directions in which a matrix has the most "stretch." Imagine a data cloud shaped like a cigar. SVD finds the principal axis (longest direction), the secondary axis (perpendicular), and the third axis (perpendicular to both). The singular values measure how long the cloud is in each direction. Truncated SVD keeps only the strongest directions, like viewing the cigar from its side rather than its end.

## Power Iteration as Echo Location

Power iteration is like shouting in a cave and listening for the loudest echo. The dominant eigenvector is the direction that returns the strongest echo. Start by shouting in a random direction, listen to the echo (multiply by A), normalize, and shout again. After enough iterations, your shout aligns with the strongest resonant mode of the cave.