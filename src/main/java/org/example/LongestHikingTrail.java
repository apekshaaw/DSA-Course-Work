package org.example;

public class LongestHikingTrail {

    public static int longestHike(int[] nums, int k) {
        int maxLen = 0;
        int start = 0;

        for (int end = 1; end < nums.length; end++) {
            if (nums[end] - nums[end - 1] > k) {
                start = end; // Move start to current end if elevation gain exceeds k
            }
            maxLen = Math.max(maxLen, end - start + 1);
        }

        return maxLen;
    }

    public static void main(String[] args) {
        int[] trail1 = {4, 2, 1, 4, 3, 4, 5, 8, 15};
        int k1 = 3;
        System.out.println("Longest Hike: " + longestHike(trail1, k1)); // Output: 5

        int[] trail2 = {1, 3, 5, 10, 12, 15};
        int k2 = 3;
        System.out.println("Longest Hike: " + longestHike(trail2, k2)); // Output: 3
    }
}

//Time Complexity is O(n)