# Suffix Array — Step by Step Guide

## Step 1: Implement Prefix-Doubling

Create class SuffixArray with method build(String s). Initialize rank array with character codes. Initialize sa array with indices 0..n-1. Set k = 1. While k < n: sort sa by (rank[i], rank[i+k]) using comparator. Assign new ranks. If all ranks are unique, break. Else k *= 2. Return sa.

## Step 2: Verify with Short Strings

Test with "banana": SA should be [5,3,1,0,4,2]. Test with "aaaa": SA should be [3,2,1,0]. Test with empty string and single character.

## Step 3: Implement Kasai's LCP

Create method buildLCP(String s, int[] sa). Compute invSA. Initialize k = 0. For i = 0..n-1: rank = invSA[i]; if rank == n-1: k = 0; continue; j = sa[rank+1]; while i+k < n && j+k < n && s[i+k] == s[j+k]: k++; lcp[rank] = k; if k > 0: k--. Return lcp.

## Step 4: Test LCP

Verify LCP for "banana": [0,1,3,0,0,2]. Verify LCP for "aaaa": [1,2,3]. LCP[0] is always 0 (no previous suffix).

## Step 5: Implement Pattern Matching

Perform binary search on SA to find lower bound (first suffix >= pattern) and upper bound (first suffix > pattern). Range [lo, hi) gives all occurrences. Write comparison function that compares characters of suffix with pattern.

## Step 6: Compute Longest Repeated Substring

Find max LCP value and the corresponding position. Extract the substring.

## Step 7: Count Distinct Substrings

Total distinct = n*(n+1)/2 - sum(LCP). Verify with "banana": 21 - 6 = 15.