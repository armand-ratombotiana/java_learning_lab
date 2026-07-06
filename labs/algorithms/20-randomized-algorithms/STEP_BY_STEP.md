# Step-by-Step — Randomized Algorithms Implementation

## Reservoir Sampling Steps

1. Create reservoir array of size k
2. Copy first k elements into reservoir
3. For i from k to n-1:
   a. Generate random j in [0, i]
   b. If j < k, set reservoir[j] = stream[i]
4. Return reservoir

## Fisher-Yates Shuffle Steps

1. For i from n-1 down to 1:
   a. Generate random j in [0, i]
   b. Swap arr[i] and arr[j]
2. Array is now uniformly random permutation

## Freivalds' Steps

1. Generate random vector x of length n with entries 0 or 1
2. Compute Bx = B * x (vector result)
3. Compute ABx = A * Bx
4. Compute Cx = C * x
5. If ABx != Cx, return FALSE (definite)
6. Else return TRUE (probable, re-run for higher confidence)
