package org.example;

import java.util.*;

class ClassroomScheduler {
    static class Class {
        int start;
        int end;

        Class(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static int findBusiestClassroom(int n, int[][] classes) {
        // PriorityQueue to keep track of available rooms, sorted by room number (natural ordering)
        PriorityQueue<Integer> availableRooms = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            availableRooms.offer(i);
        }

        // PriorityQueue to manage ongoing classes, sorted by end time
        PriorityQueue<int[]> ongoingClasses = new PriorityQueue<>(
                Comparator.comparingInt(a -> a[1]) // Order by end time
        );

        int[] roomUsage = new int[n]; // Array to count the usage of each room
        for (int[] cls : classes) {
            // Free up rooms that are no longer occupied (i.e., classes that have ended)
            while (!ongoingClasses.isEmpty() && ongoingClasses.peek()[1] <= cls[0]) {
                availableRooms.offer(ongoingClasses.poll()[0]);
            }

            // If no rooms are available, delay the class
            if (availableRooms.isEmpty()) {
                int[] nextClass = ongoingClasses.poll();
                availableRooms.offer(nextClass[0]);
                roomUsage[nextClass[0]]++;
                ongoingClasses.offer(new int[]{nextClass[0], nextClass[1] + (cls[1] - cls[0])});
            } else {
                // Assign an available room to the current class
                int room = availableRooms.poll();
                roomUsage[room]++;
                ongoingClasses.offer(new int[]{room, cls[1]});
            }
        }

        // Determine the room that held the most classes
        int maxUsage = 0;
        int busiestRoom = 0;
        for (int i = 0; i < n; i++) {
            if (roomUsage[i] > maxUsage) {
                maxUsage = roomUsage[i];
                busiestRoom = i;
            }
        }

        return busiestRoom;
    }

    public static void main(String[] args) {
        int n = 2;
        int[][] classes = {{0, 10}, {1, 5}, {2, 7}, {3, 4}};
        System.out.println(findBusiestClassroom(n, classes)); // Output: 0

        int n2 = 3;
        int[][] classes2 = {{1, 20}, {2, 10}, {3, 5}, {4, 9}, {6, 8}};
        System.out.println(findBusiestClassroom(n2, classes2)); // Output: 1
    }
}
