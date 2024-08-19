package org.example;

import java.util.Deque;
import java.util.LinkedList;

public class TrailHiking {

    // Function to find the maximum length of consecutive segments within elevation gain limit k
    public static int maxConsecutiveSegment(int[] elevations, int maxGain) {
        int length = elevations.length;
        if (length == 0) return 0;

        // Deques to track the minimum and maximum elevations within the current window
        Deque<Integer> minQueue = new LinkedList<>();
        Deque<Integer> maxQueue = new LinkedList<>();

        int start = 0; // Start index of the sliding window
        int maxLength = 0; // Maximum length of valid segment

        // Process each elevation with the end index
        for (int end = 0; end < length; end++) {
            // Maintain minQueue with the current elevation
            while (!minQueue.isEmpty() && elevations[minQueue.peekLast()] >= elevations[end]) {
                minQueue.pollLast();
            }
            minQueue.addLast(end);

            // Maintain maxQueue with the current elevation
            while (!maxQueue.isEmpty() && elevations[maxQueue.peekLast()] <= elevations[end]) {
                maxQueue.pollLast();
            }
            maxQueue.addLast(end);

            // Validate the current window
            while (elevations[maxQueue.peekFirst()] - elevations[minQueue.peekFirst()] > maxGain) {
                // If invalid, adjust the window from the start
                if (minQueue.peekFirst() == start) minQueue.pollFirst();
                if (maxQueue.peekFirst() == start) maxQueue.pollFirst();
                start++;
            }

            // Update the maximum length
            maxLength = Math.max(maxLength, end - start + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        // Test Case 1
        int[] elevations1 = {1, 3, 6, 7, 9, 2, 5, 8};
        int maxGain1 = 3;
        System.out.println("Test Case 1: Maximum length = " + maxConsecutiveSegment(elevations1, maxGain1)); // Output: 4

        // Test Case 2
        int[] elevations2 = {10, 13, 15, 18, 12, 8, 7, 14, 20};
        int maxGain2 = 5;
        System.out.println("Test Case 2: Maximum length = " + maxConsecutiveSegment(elevations2, maxGain2)); // Output: 3
    }
}
