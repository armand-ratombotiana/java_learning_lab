# Step by Step: AVL Insertion

## Insert Sequence: 10, 20, 30, 40, 50, 25

```
Insert 10:    10

Insert 20:    10
               \
                20
               (balance = -1, OK)

Insert 30:    10
               \
                20
                 \
                  30
    balance(10) = -2 → RR case → leftRotate(10)
              20
             /  \
           10    30

Insert 40:    20
             /  \
           10    30
                   \
                    40
    balance(30) = -2 → RR → leftRotate(30)
              20
             /  \
           10    40
                /  \
              30    40? No → 
    After leftRotate(30):
              20
             /  \
           10    30  ← wait...
```

Let me redo more carefully:

```
After RR fix:    20
                 / \
               10  30
                     \
                      40

Insert 40:    20 (bf=-1)
             /  \
           10   30 (bf=-1)
                  \
                   40 (bf=0)
    No rotation needed yet.

Insert 50:    20 (bf=-2)
             /  \
           10   30 (bf=-2)
                  \
                   40 (bf=-1)
                     \
                      50
    RR at 30: leftRotate(30)
              20
             /  \
           10   40
                / \
              30   50
    Check 20: bf = height(10)=0 - height(40)=1 → OK

Insert 25:    20 (bf=-1)
             /  \
           10   40 (bf=+1)
                / \
              30   50
             /
           25
    balance(40) = height(30)=1 - height(50)=0 = +1 → OK
    Check 20: bf = 0 - 2 = -2 → violation
    LR case: left child(40) has right-heavy(30)
    First rightRotate(40.left=30):
              20
             /  \
           10   30
                / \
              25   40
                     \
                      50
    Then leftRotate(20):
              30
             /  \
           20   40
          / \     \
        10   25    50
    All balances OK.
```

## Fenwick Tree Query Step by Step

```
Query prefix sum up to index 7 (binary: 111):

idx=7 (111): sum += bit[7]
idx -= 7 & -7 = 7 - 1 = 6 (110)
idx=6 (110): sum += bit[6]
idx -= 6 & -6 = 6 - 2 = 4 (100)
idx=4 (100): sum += bit[4]
idx -= 4 & -4 = 4 - 4 = 0
Done: sum = bit[7] + bit[6] + bit[4]
```
