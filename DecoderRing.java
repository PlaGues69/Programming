package org.example;

import java.util.Scanner;

public class DecoderRing {

    // Function to perform a character shift
    private static char shiftCharacter(char ch, int direction) {
        if (Character.isLowerCase(ch)) {
            if (direction == 1) { // Forward shift
                return ch == 'z' ? 'a' : (char)(ch + 1);
            } else { // Backward shift
                return ch == 'a' ? 'z' : (char)(ch - 1);
            }
        } else if (Character.isUpperCase(ch)) {
            if (direction == 1) { // Forward shift
                return ch == 'Z' ? 'A' : (char)(ch + 1);
            } else { // Backward shift
                return ch == 'A' ? 'Z' : (char)(ch - 1);
            }
        } else {
            return ch; // Non-alphabetic characters remain unchanged
        }
    }

    // Function to apply given shifts to the input string
    private static String performShifts(String text, int[][] shiftOperations) {
        char[] characters = text.toCharArray();

        for (int[] shift : shiftOperations) {
            int startIndex = shift[0];
            int endIndex = shift[1];
            int direction = shift[2];

            for (int i = startIndex; i <= endIndex; i++) {
                characters[i] = shiftCharacter(characters[i], direction);
            }
        }

        return new String(characters);
    }

    // Main method for user interaction and process initiation
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        // Capture the user's message
        System.out.print("Please input the message: ");
        String message = inputScanner.nextLine();

        // Capture the shift operations
        System.out.print("Please input the shift operations in the format [[start, end, direction], ...]: ");
        String shiftOpsInput = inputScanner.nextLine();

        // Parse the shift operations
        int[][] shiftOperations = parseShiftOps(shiftOpsInput);

        // Check if parsing was successful
        if (shiftOperations == null) {
            System.out.println("There was an error parsing your shift operations. Ensure the format is correct.");
        } else {
            // Execute shifts and output the result
            String decipheredMessage = performShifts(message, shiftOperations);
            System.out.println("Deciphered output: " + decipheredMessage);
        }

        inputScanner.close();
    }

    // Helper function to parse the shift operations input
    private static int[][] parseShiftOps(String shiftOpsInput) {
        try {
            // Normalize input string by removing excess spaces and brackets
            shiftOpsInput = shiftOpsInput.trim().replaceAll("\\s+", "");
            if (shiftOpsInput.startsWith("[[") && shiftOpsInput.endsWith("]]")) {
                shiftOpsInput = shiftOpsInput.substring(2, shiftOpsInput.length() - 2);
            }

            // Split the string into individual shift operations
            String[] individualShifts = shiftOpsInput.split("\\],\\[");

            // Initialize the shift operations array
            int[][] shiftOperations = new int[individualShifts.length][3];

            // Parse each shift operation
            for (int i = 0; i < individualShifts.length; i++) {
                String[] components = individualShifts[i].split(",");
                if (components.length != 3) {
                    throw new IllegalArgumentException("Shift operation format is incorrect.");
                }
                shiftOperations[i][0] = Integer.parseInt(components[0]); // Start index
                shiftOperations[i][1] = Integer.parseInt(components[1]); // End index
                shiftOperations[i][2] = Integer.parseInt(components[2]); // Shift direction
            }

            return shiftOperations;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
            // Handle potential parsing errors
            ex.printStackTrace();
            return null;
        }
    }
}
