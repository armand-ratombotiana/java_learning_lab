# Step-by-Step — Computational Geometry Implementation

## Graham Scan Steps

1. Find lowest point (and leftmost if tie) as the pivot
2. Sort remaining points by polar angle relative to pivot
3. Initialize a stack with pivot and first two sorted points
4. For i = 3 to n-1:
   a. Take top two stack elements and current point
   b. While orientation of these three is NOT counterclockwise: pop stack
   c. Push current point onto stack
5. Return stack contents as convex hull

## Monotone Chain Steps

1. Sort all points by x (then y) ascending
2. Build lower hull:
   a. Start from leftmost point
   b. For each point, pop while last two hull points + current make a right turn
   c. Push current point
3. Build upper hull:
   a. Start from rightmost point
   b. Same process scanning right to left
4. Remove duplicates at endpoints
5. Concatenate lower (minus last) and upper (minus last)
