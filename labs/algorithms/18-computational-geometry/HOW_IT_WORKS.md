# How Computational Geometry Works

## Graham Scan

1. Find the point with lowest y (lowest x for ties) as pivot
2. Sort all other points by polar angle from pivot
3. Push pivot and first two sorted points onto stack
4. For each remaining point:
   a. While stack top doesn't make a left turn with current point, pop
   b. Push current point onto stack
5. Stack contains convex hull vertices

## Monotone Chain

1. Sort points by x (then y)
2. Build lower hull: scan left to right, maintain left turns
3. Build upper hull: scan right to left, maintain left turns
4. Remove duplicate endpoints (first and last points)
5. Concatenate lower and upper hulls

## Closest Pair

1. Sort points by x-coordinate
2. Recursively divide into left and right halves
3. Find minimum distance delta = min(left_min, right_min)
4. Create strip of points within delta of midline
5. Sort strip by y-coordinate
6. For each point in strip, check next 7 points in y-order
7. Return minimum distance found
