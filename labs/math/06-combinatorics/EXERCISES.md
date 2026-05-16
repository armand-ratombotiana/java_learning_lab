# Exercises: Combinatorics

**1.** How many ways to arrange "MATH"?
- Solution: 4! = 24 (4 distinct letters)

**2.** How many 5-digit codes using 0-9 with no repetition?
- Solution: P(10,5) = 10×9×8×7×6 = 30240

**3.** Choose 3 from 10: C(10,3) = ?
- Solution: C(10,3) = 10!/(3!7!) = (10×9×8)/(3×2×1) = 120

**4.** How many ways to select committee of 4 from 10 people?
- Solution: C(10,4) = 210 (same as choosing, order doesn't matter)

**5.** Find coefficient of x³ in (x+2)⁵
- Solution: C(5,3)x³(2)² = 10 × 4 = 40

**6.** A password has 3 letters followed by 4 digits. How many passwords?
- Solution: 26³ × 10⁴ = 17576 × 10000 = 175,760,000

**7.** How many derangements of {1,2,3}?
- Solution: D₃ = 3!(1 - 1/1! + 1/2! - 1/3!) = 6 × (1-1+1/2-1/6) = 6 × (1/3) = 2

**8.** Find partitions of 4
- Solution: 5 partitions: 4, 3+1, 2+2, 2+1+1, 1+1+1+1

**9.** How many paths from (0,0) to (3,3) moving only right/up?
- Solution: C(6,3) = 20 (choose 3 of 6 steps to be "right")

**10.** In how many ways can 5 boys and 5 girls sit at a round table?
- Solution: (10-1)! × 5! = 9! × 120 = 43545600 (fix one person to break rotation, arrange others, then place girls in gaps)