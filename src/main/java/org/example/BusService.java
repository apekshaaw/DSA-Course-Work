package org.example;

import java.util.Arrays;

public class BusService {

    public static int[] optimizeBoarding(int[] passengers, int k) {
        for (int i = 0; i < passengers.length; i += k) {
            int end = Math.min(i + k - 1, passengers.length - 1);
            reverse(passengers, i, end);
        }
        return passengers;
    }

    // Helper method to reverse a portion of the array
    private static void reverse(int[] array, int start, int end) {
        while (start < end) {
            int temp = array[start];
            array[start] = array[end];
            array[end] = temp;
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        int[] passengers1 = {1, 2, 3, 4, 5};
        int k1 = 2;
        System.out.println(Arrays.toString(optimizeBoarding(passengers1, k1))); // Output: [2, 1, 4, 3, 5]

        int[] passengers2 = {1, 2, 3, 4, 5};
        int k2 = 3;
        System.out.println(Arrays.toString(optimizeBoarding(passengers2, k2))); // Output: [3, 2, 1, 4, 5]
    }
}
//Time Complexity is O(n), where n is the number of passengers.