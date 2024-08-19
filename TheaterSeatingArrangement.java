package org.example;

public class TheaterSeatingArrangement {

    public static boolean canSitTogether(int[] seats, int maxIndexDiff, int maxValueDiff) {
        // Loop through each seat in the array
        for (int current = 0; current < seats.length; current++) {
            // Check following seats within the maximum allowed index difference
            for (int next = current + 1; next <= current + maxIndexDiff && next < seats.length; next++) {
                // Check if the difference in seat numbers is within the allowed value difference
                if (Math.abs(seats[current] - seats[next]) <= maxValueDiff) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] seats1 = {2, 3, 5, 4, 9};
        int maxIndexDiff1 = 2;
        int maxValueDiff1 = 1;
        System.out.println(canSitTogether(seats1, maxIndexDiff1, maxValueDiff1)); // Output: true

        int[] seats2 = {1, 5, 9, 13};
        int maxIndexDiff2 = 2;
        int maxValueDiff2 = 3;
        System.out.println(canSitTogether(seats2, maxIndexDiff2, maxValueDiff2)); // Output: false

        int[] seats3 = {4, 6, 8, 10};
        int maxIndexDiff3 = 1;
        int maxValueDiff3 = 2;
        System.out.println(canSitTogether(seats3, maxIndexDiff3, maxValueDiff3)); // Output: true
    }
}
