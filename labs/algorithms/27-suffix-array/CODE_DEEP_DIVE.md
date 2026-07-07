# Suffix Array — Code Deep Dive

The SuffixArray class implements prefix-doubling using Java's built-in sorting (Arrays.sort with Comparator). The rank and tmp arrays store current and next ranks. The sa array stores suffix indices. Initial rank is the character code. Each iteration creates pairs (rank[i], rank[i+k]) and sorts.

Key optimization: use a custom comparator that avoids creating Pair objects. Instead, compare rank[i] and rank[j] directly. For tie-breaking, compare rank[i+k] and rank[j+k] with sentinel -1 handling.

After sorting, new ranks are assigned: if (compare(sa[i-1], sa[i]) == 0) then tmp[sa[i]] = tmp[sa[i-1]] else tmp[sa[i]] = i. The compare function checks the two ranks.

The LCPArray class implements Kasai's algorithm. The inverse array invSA: for (int i = 0; i < n; i++) invSA[sa[i]] = i. The main loop for (int i = 0; i < n; i++): if invSA[i] == n-1 (last suffix), set k = 0 and continue. Get next suffix j = sa[invSA[i] + 1]. While i+k < n && j+k < n && s[i+k] == s[j+k] do k++. lcp[invSA[i]] = k. If k > 0: k--.

The SuffixArrayExample class demonstrates applications. The pattern matching method: lowerBound = findFirst(SA, P), upperBound = findLast(SA, P). Both use binary search with custom comparison. The longestRepeatedSubstring: find max lcp[i], return s.substring(sa[i], sa[i] + lcp[i]).

The findFirst method: binary search with compare(S, sa[mid], P) >= 0. The findLast: binary search with compare(S, sa[mid], P) > 0. The compare function compares characters of the suffix starting at sa[mid] with the pattern P, returning negative if suffix < P, 0 if prefix match, positive if suffix > P.