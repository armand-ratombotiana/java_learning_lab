package com.ds01;

/*
 * ArrayAlgorithms - Binary search, two-pointer techniques, and array rotation.
 *
 * Time Complexity:
 * - binarySearch: O(log n)
 * - twoSumSorted: O(n)
 * - reverse: O(n)
 * - rotateLeft: O(n)
 *
 * Space Complexity: O(1) for all operations
 */
public class ArrayAlgorithms {

    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    public static int binarySearchRecursive(int[] arr, int target) {
        return binarySearchRecursive(arr, target, 0, arr.length - 1);
    }

    private static int binarySearchRecursive(int[] arr, int target, int left, int right) {
        if (left > right) return -1;
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) return binarySearchRecursive(arr, target, mid + 1, right);
        return binarySearchRecursive(arr, target, left, mid - 1);
    }

    public static int[] twoSumSorted(int[] sortedArr, int target) {
        int left = 0, right = sortedArr.length - 1;
        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];
            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }

    public static void reverse(int[] arr) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int tmp = arr[left];
            arr[left] = arr[right];
            arr[right] = tmp;
            left++;
            right--;
        }
    }

    public static void rotateLeft(int[] arr, int positions) {
        if (arr == null || arr.length == 0) return;
        int k = positions % arr.length;
        if (k < 0) k += arr.length;
        if (k == 0) return;
        reverse(arr, 0, arr.length - 1);
        reverse(arr, 0, arr.length - k - 1);
        reverse(arr, arr.length - k, arr.length - 1);
    }

    public static void rotateRight(int[] arr, int positions) {
        if (arr == null || arr.length == 0) return;
        int k = positions % arr.length;
        if (k < 0) k += arr.length;
        rotateLeft(arr, arr.length - k);
    }

    public static int maxSubarraySum(int[] arr) {
        int maxEndingHere = arr[0];
        int maxSoFar = arr[0];
        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }

    public static boolean hasPairWithSum(int[] sortedArr, int target) {
        int left = 0, right = sortedArr.length - 1;
        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];
            if (sum == target) return true;
            if (sum < target) left++;
            else right--;
        }
        return false;
    }

    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int tmp = arr[start];
            arr[start] = arr[end];
            arr[end] = tmp;
            start++;
            end--;
        }
    }

    public static int removeDuplicatesSorted(int[] sortedArr) {
        if (sortedArr.length == 0) return 0;
        int write = 1;
        for (int read = 1; read < sortedArr.length; read++) {
            if (sortedArr[read] != sortedArr[read - 1]) {
                sortedArr[write++] = sortedArr[read];
            }
        }
        return write;
    }
}
