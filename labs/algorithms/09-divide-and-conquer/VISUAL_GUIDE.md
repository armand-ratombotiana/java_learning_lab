# Visual Guide — Divide and Conquer

## Closest Pair Division
`
Points sorted by x:
   a    b    c    d | e    f    g    h
                    ↑ median
Left: δL           Right: δR
       δ = min(δL, δR)

Strip: points within δ of median
   b    d | e    g
   Check pairs across boundary
`

## D&C Algorithm Tree
`
         Problem
        /    |    \
       /     |     \
  SubP1   SubP2   SubP3
   / \     / \     / \
  S1  S2  S3  S4  S5  S6
   \ /     \ /     \ /
   C1      C2      C3
        \  |  /
       Solution
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Divide and Conquer

## Closest Pair of Points
`java
public class ClosestPair {
    record Point(double x, double y) {}

    public double closestPair(Point[] points) {
        Point[] px = points.clone();
        Point[] py = points.clone();
        Arrays.sort(px, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(py, Comparator.comparingDouble(p -> p.y));
        return closestPairRec(px, py, 0, px.length);
    }

    private double closestPairRec(Point[] px, Point[] py, int lo, int hi) {
        if (hi - lo <= 3) return bruteForce(px, lo, hi);
        int mid = lo + (hi - lo) / 2;
        double midX = px[mid].x;

        Point[] pyLeft = Arrays.stream(py).filter(p -> p.x <= midX).toArray(Point[]::new);
        Point[] pyRight = Arrays.stream(py).filter(p -> p.x > midX).toArray(Point[]::new);

        double δL = closestPairRec(px, pyLeft, lo, mid);
        double δR = closestPairRec(px, pyRight, mid, hi);
        double δ = Math.min(δL, δR);

        List<Point> strip = new ArrayList<>();
        for (Point p : py)
            if (Math.abs(p.x - midX) < δ) strip.add(p);

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < δ; j++) {
                double d = distance(strip.get(i), strip.get(j));
                δ = Math.min(δ, d);
            }
        }
        return δ;
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Divide and Conquer

## D&C Problem Solving
1. Identify if problem can be broken into independent subproblems
2. Define base case: smallest instance solved directly
3. Determine divide step: how to split input
4. Determine combine step: how to merge subproblem results
5. Analyze using recurrence relation
6. Consider whether D&C is optimal vs DP or greedy

## When NOT to Use D&C
- Subproblems are not independent (use DP instead)
- Combine step is more expensive than brute force
- Problem size is too small for recursion overhead to be worth it
