# Visual Guide — Searching

## Binary Search Decision Tree
`
Searching for 23 in [2,5,8,12,16,23,38,56,72,91]

          mid=4 (16)
         /          \
      <16           >16
   lo=0,hi=3     lo=5,hi=9
   mid=1 (5)     mid=7 (56)
    /     \         /     \
  ...    ...      <56    >56
                lo=5    lo=8
                hi=6    hi=9
                mid=5=23 ✓
`

## Comparison
Unsorted → Linear O(n)
Sorted → Binary O(log n)
Uniform Sorted → Interpolation O(log log n)
