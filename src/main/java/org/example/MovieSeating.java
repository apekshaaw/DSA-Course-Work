package org.example;

public class MovieSeating {

    public static boolean canSitTogether(int[] nums, int indexDiff, int valueDiff) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j <= i + indexDiff && j < nums.length; j++) {
                if (Math.abs(nums[i] - nums[j]) <= valueDiff) {
                    return true; // Found a pair that satisfies the conditions
                }
            }
        }
        return false; // No such pair found
    }

    public static void main(String[] args) {
        int[] nums1 = {2, 3, 5, 4, 9};
        int indexDiff1 = 2;
        int valueDiff1 = 1;
        System.out.println(canSitTogether(nums1, indexDiff1, valueDiff1)); // Output: true

        int[] nums2 = {1, 5, 9, 14, 20};
        int indexDiff2 = 1;
        int valueDiff2 = 2;
        System.out.println(canSitTogether(nums2, indexDiff2, valueDiff2)); // Output: false
    }
}
