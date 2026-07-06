# Visual Guide — Computational Geometry

## Convex Hull Construction (Monotone Chain)

Points: [(0,0), (1,1), (2,0), (1,-1), (3,3), (0,3)]

Sorted by x: (0,0), (0,3), (1,-1), (1,1), (2,0), (3,3)

Lower hull (left to right):
- Push (0,0), (0,3)
- (1,-1): orientation(0,0), (0,3), (1,-1) = right -> pop (0,3)
- Push (1,-1). Continue: (1,1) -> right turn -> pop (1,-1), push (1,1)
- (2,0): pop (1,1), push (2,0). (3,3): left turn -> push (3,3)

Lower hull: (0,0), (2,0), (3,3)

Upper hull (right to left):
- Start at rightmost, similar process

Final hull: (0,0), (2,0), (3,3), (0,3)
