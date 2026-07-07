# Suffix Array &amp; LCP Array — Overview

This lab covers suffix arrays and LCP arrays, fundamental data structures for string processing. A suffix array is a sorted array of all suffixes of a string; the LCP array stores the lengths of the longest common prefix between consecutive suffixes. Together they enable efficient solutions for pattern matching, longest repeated substring, distinct substring counting, and many other string problems.

## Learning Objectives

- Construct suffix arrays using the prefix-doubling algorithm (O(n log n))
- Build the LCP array using Kasai's algorithm (O(n))
- Perform pattern matching using binary search on the suffix array
- Compute the longest repeated substring and the number of distinct substrings

## Prerequisites

- Java arrays and sorting
- Basic string manipulation
- Understanding of lexicographic ordering

## Estimated Time

- **Theory**: 75 minutes
- **Practice**: 90 minutes
- **Exercises**: 75 minutes
- **Total**: 4-5 hours