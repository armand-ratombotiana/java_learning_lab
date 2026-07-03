# How Searching Works

## Binary Search
Array: [2,5,8,12,16,23,38,56,72,91], Target: 23
lo=0, hi=9, mid=4 → arr[4]=16 < 23 → lo=5
lo=5, hi=9, mid=7 → arr[7]=56 > 23 → hi=6
lo=5, hi=6, mid=5 → arr[5]=23 ✓ Found at index 5

## Interpolation Search Probe
pos = lo + ((target - arr[lo]) × (hi - lo)) / (arr[hi] - arr[lo])
For target=23: pos = 0 + (21 × 9) / 89 ≈ 2 → arr[2]=8 < 23
Next: pos = 5 → arr[5]=23 ✓
