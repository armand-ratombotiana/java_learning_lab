# Suffix Array — Visual Guide

## Suffix List for S="banana"

Suffixes: 0:banana, 1:anana, 2:nana, 3:ana, 4:na, 5:a. Sorted: a(5), ana(3), anana(1), banana(0), na(4), nana(2). SA = [5, 3, 1, 0, 4, 2].

## LCP Array

LCP[0] = lcp("a", "ana") = 1 (shared "a"). LCP[1] = lcp("ana", "anana") = 3 (shared "ana"). LCP[2] = lcp("anana", "banana") = 0. LCP[3] = lcp("banana", "na") = 0. LCP[4] = lcp("na", "nana") = 2 (shared "na"). LCP = [0, 1, 3, 0, 0, 2].

## Prefix-Doubling Ranks

k=1: ranks by first char: a=0, b=1, n=2. Pairs for k=2: (0,1) for "anana", (0,2) for "ana", (1,2) for "banana", (2,0) for "na", (2,1) for "nana", (0,-1) for "a". Sorted pairs.

## Distinct Substrings Visual

S="banana" (n=6, total substrings=21). Sum LCP = 1+3+2 = 6. Distinct substrings = 21 - 6 = 15. Each LCP[i] counts shared prefixes between suffix i and i-1 in sorted order, removing duplicates.

## Pattern Matching for "na"

Binary search SA for first >= "na": found at SA[4]="na". Last >= "na": found at SA[5]="nana". Range [4,6) = indices 4,5. Occurrences: SA[4]=4, SA[5]=2, so "na" appears at positions 2 and 4.