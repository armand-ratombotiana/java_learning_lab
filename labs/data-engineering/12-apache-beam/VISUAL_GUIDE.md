# Visual Guide to Apache Beam

```
Pipeline DAG:
[Read] -> [ParDo(Parse)] -> [ParDo(Extract)] -> [Count] -> [Write]

Windowing:
Events:  e1   e2   e3   e4   e5   e6   e7
Time:    0    2    4    6    8    10   12
Fixed(5):[0-5)    [5-10)      [10-15)
         e1,e2,e3 e4,e5,e6    e7

Triggers:
Default: Fire at watermark
Early:   Fire every 1min + at watermark
Late:    Fire at watermark + allowedLateness window
```
