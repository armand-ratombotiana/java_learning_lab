# Exact Exponential Algorithms — Common Mistakes

1. Meet-in-the-middle: not sorting the second half's sums before binary search. Binary search requires sorted input to be correct.

2. Meet-in-the-middle memory: storing all 2^{n/2} sums for both halves can cause memory overflow for n > 50. The typical approach stores one half in sorted order and iterates through the other half.

3. Inclusion-exclusion: forgetting the (-1)^{|S|} sign factor. The alternating sum is crucial; wrong parity leads to incorrect results.

4. Inclusion-exclusion intersection computation: counting elements satisfying ALL properties in a subset is often non-trivial. Underestimating or overestimating this count breaks the algorithm.

5. Fast subset convolution memory: allocating (n+1) * 2^n doubles requires significant memory (176 MB for n=20). For larger n, memory becomes prohibitive.

6. Zeta transform order: the outer loop must iterate over bits, and the inner loop over masks. Swapping the loops (mask-outer, bit-inner) produces incorrect results.

7. Moebius transform direction: the Moebius transform is the inverse of the zeta transform and uses subtraction instead of addition. Using addition (i.e., applying zeta twice) gives wrong results.

8. Branching algorithm design: choosing reduction rules that are not safe (they may eliminate optimal solutions). All branching rules must be proved correct.

9. Assuming 2^{n/2} ~ 2^{n/2}: for n odd, the split gives n/2 floor and ceil. Both work, but the performance depends on the smaller half for the enumeration that is stored and sorted.