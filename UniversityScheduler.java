package org.example;

import java.util.*;

public class UniversityScheduler {
    static class Class {
        int start;
        int end;
        int size;

        public Class(int start, int end, int size) {
            this.start = start;
            this.end = end;
            this.size = size;
        }
    }

    public static int mostUsedClassroom(int n, int[][] classesInput) {
        List<Class> classes = new ArrayList<>();
        for (int[] c : classesInput) {
            classes.add(new Class(c[0], c[1], c[2]));
        }

        // Sort classes by start time and then by size (larger first)
        classes.sort((a, b) -> {
            if (a.start != b.start) return Integer.compare(a.start, b.start);
            return Integer.compare(b.size, a.size);
        });

        // Priority queue to manage end times of classes in rooms
        PriorityQueue<int[]> roomQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        int[] classCount = new int[n]; // Count classes in each room

        for (Class c : classes) {
            // Free up rooms that have finished their classes by the start time of the current class
            while (!roomQueue.isEmpty() && roomQueue.peek()[0] <= c.start) {
                roomQueue.poll();
            }

            if (roomQueue.size() < n) {
                // Assign class to a new room
                int room = roomQueue.size();
                roomQueue.add(new int[]{c.end, room});
                classCount[room]++;
            } else {
                // Reassign class to the room that will be free the earliest
                int[] earliestRoom = roomQueue.poll();
                roomQueue.add(new int[]{Math.max(earliestRoom[0], c.end), earliestRoom[1]});
                classCount[earliestRoom[1]]++;
            }
        }

        // Find the room with the maximum number of classes
        int maxClasses = 0;
        int roomWithMaxClasses = 0;
        for (int i = 0; i < n; i++) {
            if (classCount[i] > maxClasses) {
                maxClasses = classCount[i];
                roomWithMaxClasses = i;
            }
        }

        return roomWithMaxClasses;
    }

    public static void main(String[] args) {
        int n1 = 2;
        int[][] classes1 = {{0, 10, 30}, {1, 5, 20}, {2, 7, 25}, {3, 4, 15}};
        System.out.println(mostUsedClassroom(n1, classes1)); // Output: 0

        int n2 = 3;
        int[][] classes2 = {{1, 20, 30}, {2, 10, 25}, {3, 5, 15}, {4, 9, 10}, {6, 8, 20}};
        System.out.println(mostUsedClassroom(n2, classes2)); // Output: 1
    }
}
