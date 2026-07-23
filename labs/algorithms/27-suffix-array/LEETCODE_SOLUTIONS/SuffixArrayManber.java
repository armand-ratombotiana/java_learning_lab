package com.algorithms.suffixarray;

import java.util.Arrays;

/**
 * Custom: Suffix Array Construction (Manber-Myers algorithm)
 * Used for string pattern matching, longest repeated substring, etc.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class SuffixArrayManber {

    public static class Suffix implements Comparable<Suffix> {
        int index;
        int rank, nextRank;
        Suffix(int index, int rank, int nextRank) {
            this.index = index;
            this.rank = rank;
            this.nextRank = nextRank;
        }
        public int compareTo(Suffix o) {
            if (rank != o.rank) return rank - o.rank;
            return nextRank - o.nextRank;
        }
    }

    public int[] buildSuffixArray(String s) {
        int n = s.length();
        Suffix[] suffixes = new Suffix[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = new Suffix(i, s.charAt(i) - 'a', (i + 1 < n) ? s.charAt(i + 1) - 'a' : -1);
        }
        Arrays.sort(suffixes);

        int[] index = new int[n];
        for (int k = 4; k < 2 * n; k *= 2) {
            int rank = 0, prevRank = suffixes[0].rank;
            suffixes[0].rank = 0;
            index[suffixes[0].index] = 0;
            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank == prevRank && suffixes[i].nextRank == suffixes[i - 1].nextRank) {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = rank;
                } else {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = ++rank;
                }
                index[suffixes[i].index] = i;
            }
            for (int i = 0; i < n; i++) {
                int nextIdx = suffixes[i].index + k / 2;
                suffixes[i].nextRank = (nextIdx < n) ? suffixes[index[nextIdx]].rank : -1;
            }
            Arrays.sort(suffixes);
        }
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = suffixes[i].index;
        return sa;
    }

    public static void main(String[] args) {
        SuffixArrayManber sam = new SuffixArrayManber();
        int[] sa = sam.buildSuffixArray("banana");
        System.out.print("Suffix array of 'banana': ");
        for (int i : sa) System.out.print(i + " ");
        System.out.println("(expected: [5, 3, 1, 0, 4, 2])");
    }
}
