# Step by Step: Array Operations

## Insertion at End (Dynamic Array)

```
State: capacity=4, size=3
       [10, 20, 30, _]

add(40):
  1. size(3) < capacity(4) → no resize needed
  2. elements[3] = 40
  3. size++ → size=4
  Result: [10, 20, 30, 40]
```

## Insertion at End with Resize

```
State: capacity=4, size=4
       [10, 20, 30, 40]

add(50):
  1. size(4) == capacity(4) → grow needed
  2. newCapacity = 4 + 4/2 = 6
  3. allocate new Object[6]
  4. copy old → new: [10, 20, 30, 40, _, _]
  5. elements = new array (old eligible for GC)
  6. elements[4] = 50
  7. size=5
  Result: [10, 20, 30, 40, 50, _]
```

## Insertion at Middle

```
State: [10, 20, 30, 40, _], size=4, capacity=5

add(1, 99):
  1. shift right: System.arraycopy(elements, 1, elements, 2, 3)
     Before: [10, 20, 30, 40, _]
     After:  [10, 20, 20, 30, 40]
  2. elements[1] = 99
  3. size++ → size=5
  Result: [10, 99, 20, 30, 40]
```

## Deletion at Middle

```
State: [10, 99, 20, 30, 40], size=5

remove(1):
  1. Save old: old = elements[1] (99)
  2. Shift left: System.arraycopy(elements, 2, elements, 1, 3)
     Before: [10, 99, 20, 30, 40]
     After:  [10, 20, 30, 40, 40]
  3. elements[4] = null  (allow GC)
  4. size-- → size=4
  Result: [10, 20, 30, 40, _]
```

## Binary Search (Sorted Array)

```
State: [2, 5, 8, 12, 16, 23, 38, 56, 72, 91], find target=23

Step 1: low=0, high=9, mid=(0+9)/2=4 → elements[4]=16 < 23
        low = mid+1 = 5
Step 2: low=5, high=9, mid=(5+9)/2=7 → elements[7]=56 > 23
        high = mid-1 = 6
Step 3: low=5, high=6, mid=(5+6)/2=5 → elements[5]=23 == target
        Return 5
```
