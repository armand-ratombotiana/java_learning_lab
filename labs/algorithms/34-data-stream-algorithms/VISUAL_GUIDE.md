# Data Stream Algorithms — Visual Guide

## Reservoir Sampling

Stream: [a, b, c, d, e, f, g, h], k=3. i=1: reservoir=[a]. i=2: reservoir=[a,b]. i=3: reservoir=[a,b,c]. i=4: j=random(0,3)=1, replace b with d -> [a,d,c]. i=5: j=2, replace d with e -> [a,e,c]. i=6: j=4>2, skip. i=7: j=1, replace a with g -> [g,e,c]. i=8: j=0, replace c with h -> [g,e,h]. Final sample: [g,e,h].

## Count-Min Sketch Array

2D array width=8, depth=3. Initialize all zeros. Update x: hash1(x)=3, hash2(x)=6, hash3(x)=1. Increment d[0][3], d[1][6], d[2][1]. Estimate: take minimum of d[0][3], d[1][6], d[2][1].

## Misra-Gries Counters

k=3 (2 counters). Stream: [a,b,a,c,a,b,d,c,a]. Start: counters={}. a: add (a,1). b: add (b,1). a: increment a -> (a,2). c: (a,2),(b,1) -> decrement both and c -> (a,1),(b,0) -> remove b, add (c,1). a: increment a -> (a,2),(c,1). b: (a,2),(c,1) -> decrement -> (a,1),(c,0) -> remove c, add (b,1). d: (a,1),(b,1) -> decrement -> (a,0),(b,0) -> both removed. c: add (c,1). a: increment a -> (a,1),(c,1). Final counters: a=1, c=1. True frequencies: a=4, b=2, c=2, d=1.

## Sliding Window

Window size W=5. Stream: [x,y,x,z,x,w,y]. Time 1: {x}. Time 2: {x,y}. Time 3: {x,y}. Time 4: {x,y,z}. Time 5: {x,y,z}. Time 6: x leaves window, {y,z,w}. Time 7: y leaves window, {z,w}. Count distinct in window = 2.