# Bit Manipulation — Step by Step Guide

## Step 1: Create the BitTricks Utility Class

Define a class with private constructor. Implement isPowerOfTwo: check x > 0 && (x & (x-1)) == 0. Test with powers of two (1,2,4,8,16) and non-powers (3,5,6,7).

## Step 2: Implement Population Count

Use Brian Kernighan's method: int count = 0; while (x != 0) { x &= (x - 1); count++; }. Test with x=0 (0), x=1 (1), x=255 (8), x=-1 (32).

## Step 3: Implement Bit Reversal

Use the five-step mask approach. Each step swaps increasingly large blocks. Test with 1 (0x80000000), 0xFF000000 (0x000000FF).

## Step 4: Generate Gray Code

Implement binaryToGray using G = B ^ (B >> 1). Implement grayToBinary using iterative XOR: for (mask = gray >> 1; mask != 0; mask >>= 1) gray ^= mask. Verify that adjacent Gray codes differ by one bit.

## Step 5: Build XOR Basis

Create array basis[31]. For each value x, iterate bits from 31 down to 0. If bit i is set and basis[i] == 0, store x in basis[i] and break. Else x ^= basis[i]. Test by inserting values and recomputing max XOR.

## Step 6: Implement TSP Bit DP

Initialize dp[1<<start][start] = 0, rest INF. Iterate mask 1 to (1<<n)-1. For each v in mask (where dp[mask][v] != INF), for each u not in mask, update. Answer: min over v of dp[(1<<n)-1][v] + dist[v][start].

## Step 7: Test All Components

Write JUnit tests verifying each method against known results. Test edge cases: zero, max value, negative numbers. Test TSP on a 4-city instance with known optimal tour.