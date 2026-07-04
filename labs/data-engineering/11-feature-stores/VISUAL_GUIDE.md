# Visual Guide

## Architecture
```
[Source Events] -> [Feature Engineering] -> [Feature Store]
                                             |           |
                                        [Online]    [Offline]
                                        [Redis]     [Delta]
```

## Point-in-Time
```
t0  t1  t2  t3  t4
e1  e2  e3  e4  e5
    |   |
  Feature  Label
  v1      L1  -> Correct (v1 < L1)
```
