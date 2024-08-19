package org.example;

import java.util.*;

public class TransportService {

    // Inner class to represent a node in a linked list
    static class Node {
        int value;
        Node nextNode;
        Node(int value) {
            this.value = value;
            this.nextNode = null;
        }
    }

    // Method to reverse segments of the linked list
    public static Node reverseSegment(Node head, int segmentSize) {
        Node previous = null;
        Node current = head;
        Node next = null;
        int counter = 0;

        while (current != null && counter < segmentSize) {
            next = current.nextNode;
            current.nextNode = previous;
            previous = current;
            current = next;
            counter++;
        }

        if (next != null) {
            head.nextNode = reverseSegment(next, segmentSize);
        }

        return previous;
    }

    // Method to improve the boarding process by reversing in segments
    public static Node improveBoarding(Node head, int segmentSize) {
        if (head == null || segmentSize <= 1) return head;
        return reverseSegment(head, segmentSize);
    }

    // Method to display the linked list
    public static void displayList(Node head) {
        Node temp = head;
        while (temp != null) {
            System.out.print(temp.value + " ");
            temp = temp.nextNode;
        }
        System.out.println();
    }

    // Main function to test the code
    public static void main(String[] args) {
        // Creating the linked list: 1 -> 2 -> 3 -> 4 -> 5
        Node head = new Node(1);
        head.nextNode = new Node(2);
        head.nextNode.nextNode = new Node(3);
        head.nextNode.nextNode.nextNode = new Node(4);
        head.nextNode.nextNode.nextNode.nextNode = new Node(5);

        int segmentSize = 2;
        System.out.println("Initial list:");
        displayList(head);

        Node improvedHead = improveBoarding(head, segmentSize);

        System.out.println("List after optimization with segmentSize = " + segmentSize + ":");
        displayList(improvedHead);

        // Another example with segmentSize = 3
        head = new Node(1);
        head.nextNode = new Node(2);
        head.nextNode.nextNode = new Node(3);
        head.nextNode.nextNode.nextNode = new Node(4);
        head.nextNode.nextNode.nextNode.nextNode = new Node(5);

        segmentSize = 3;
        System.out.println("Initial list:");
        displayList(head);

        improvedHead = improveBoarding(head, segmentSize);

        System.out.println("List after optimization with segmentSize = " + segmentSize + ":");
        displayList(improvedHead);
    }
}

// Outputs for the given scenarios in the question and executing the modified code:

//Initial list:
//1 2 3 4 5
//List after optimization with segmentSize = 2:
//2 1 4 3 5
//Initial list:
//1 2 3 4 5
//List after optimization with segmentSize = 3:
//3 2 1 5 4
