# How Divide and Conquer Works

## Merge Sort
`
[38,27,43,3,9,82,10]
         ↓ divide
[38,27,43,3] [9,82,10]
    ↓ divide      ↓ divide
[38,27] [43,3] [9,82] [10]
  ↓        ↓      ↓     ↓
[38][27] [43][3] [9][82][10]
  ↓  ↓     ↓  ↓    ↓  ↓   ↓
  merge    merge   merge
[27,38] [3,43] [9,82] [10]
    ↓        ↓      ↓
    merge    merge
[3,27,38,43] [9,10,82]
        ↓    ↓
        merge
[3,9,10,27,38,43,82]
`

## Closest Pair of Points
1. Sort points by x-coordinate
2. Divide at median x into left/right halves
3. Recursively find δ = min(leftMin, rightMin)
4. Create strip of points within δ of median
5. Sort strip by y, compare each with next 7 points
