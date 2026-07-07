# Exact Exponential Algorithms — Code Deep Dive

The MeetInTheMiddle class enumerates subset sums using bitmask iteration. For each half: for (int mask = 0; mask < (1 << halfSize); mask++) { long sum = 0; for (int i = 0; i < halfSize; i++) if ((mask & (1 << i)) != 0) sum += half[i]; list[mask] = sum; }. The second array is sorted with Arrays.sort.

The merge step: for (long s1 : list1) { int idx = Arrays.binarySearch(list2, target - s1); if (idx >= 0) return true; (or return the actual subsets). } Reconstruction iterates over bits to find which elements correspond to the found sums.

The InclusionExclusion class enumerates subsets of constraints via bitmask. For each mask: compute the count of elements satisfying all constraints in mask. Add or subtract based on parity of mask bits. The complexity depends on the number of constraints and the cost of computing the intersection count.

The FastSubsetConvolution class uses arrays of size (n+1) x (1<<n). For the ranked zeta transform: for (int i = 0; i < n; i++) for (int mask = 0; mask < (1<<n); mask++) if ((mask & (1<<i)) != 0) f[size][mask] += f[size][mask ^ (1<<i)]. The Moebius transform is similar but subtracts.

The convolution: for each size k, for each size j, for each mask: h[k+j][mask] += f[k][mask] * g[j][mask]. Then apply Moebius transform to get the actual convolution result.

Memory optimization: allocate a single 2D array and reuse. The zeta and Moebius transforms operate in-place. For n=20, the array size is 21 * 1,048,576 * 8 bytes ≈ 176 MB, which is large but feasible.