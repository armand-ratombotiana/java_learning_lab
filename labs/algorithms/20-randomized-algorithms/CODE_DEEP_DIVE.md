# Code Deep Dive — Randomized Algorithms

## Random Source Selection

Java provides two main random sources: java.util.Random (thread-safe but slower) and java.util.concurrent.ThreadLocalRandom (per-thread, faster, preferred). For cryptographic applications, use java.security.SecureRandom.

## Implementing Quickselect Iteratively

Recursive quickselect may stack overflow for large inputs. An iterative version using a while loop avoids this while maintaining the expected O(n) time:

`java
int quickselectIterative(int[] arr, int k) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int pivotIdx = partition(arr, left, right, 
            ThreadLocalRandom.current().nextInt(left, right + 1));
        if (k == pivotIdx) return arr[k];
        else if (k < pivotIdx) right = pivotIdx - 1;
        else left = pivotIdx + 1;
    }
    return arr[left];
}
`

## Shuffling Immutable Collections

For immutable collections, Fisher-Yates cannot be applied in-place. Create an array copy, shuffle the copy, and return it as a new list view.

## Verifying Uniformity of Shuffle

Statistical tests (chi-squared test on permutation counts) can verify shuffle uniformity. For n elements, each permutation should appear with probability 1/n!.
