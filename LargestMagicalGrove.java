package org.example;

public class LargestMagicalGrove {

    // Node class representing each node in the binary tree.
    static class Node {
        int value;
        Node leftChild;
        Node rightChild;
        Node(int value) { this.value = value; }
    }

    // Helper class to store the results of subtree evaluations
    static class TreeInfo {
        boolean isValidBST;
        int subtreeSum;
        int minValue;
        int maxValue;

        TreeInfo(boolean isValidBST, int subtreeSum, int minValue, int maxValue) {
            this.isValidBST = isValidBST;
            this.subtreeSum = subtreeSum;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    // Variable to track the maximum sum of BST subtrees
    private static int maxBSTSum = 0;

    public static void main(String[] args) {
        // Example binary tree
        Node root = new Node(1);
        root.leftChild = new Node(4);
        root.rightChild = new Node(3);
        root.leftChild.leftChild = new Node(2);
        root.leftChild.rightChild = new Node(4);
        root.rightChild.leftChild = new Node(2);
        root.rightChild.rightChild = new Node(5);
        root.rightChild.rightChild.leftChild = new Node(4);
        root.rightChild.rightChild.rightChild = new Node(6);

        int result = findLargestBSTSum(root);
        System.out.println(result); // Expected Output: 20

        // Additional test cases
        // Test Case 1: Empty tree
        Node root1 = null;
        System.out.println(findLargestBSTSum(root1)); // Expected Output: 0

        // Test Case 2: Single-node tree
        Node root2 = new Node(10);
        System.out.println(findLargestBSTSum(root2)); // Expected Output: 10

        // Test Case 3: Tree with no valid BST subtrees
        Node root3 = new Node(10);
        root3.leftChild = new Node(5);
        root3.rightChild = new Node(15);
        root3.leftChild.rightChild = new Node(20);
        root3.rightChild.leftChild = new Node(6);

        System.out.println(findLargestBSTSum(root3)); // Expected Output: 15 (the largest valid BST is the single node with value 15)
    }

    // Main function to compute the largest BST subtree sum
    public static int findLargestBSTSum(Node root) {
        maxBSTSum = 0; // Reset the maximum sum before calculation
        traverseAndEvaluate(root); // Start traversal and evaluation
        return maxBSTSum; // Return the largest BST sum found
    }

    // Recursive function to perform post-order traversal and evaluate BST properties
    private static TreeInfo traverseAndEvaluate(Node node) {
        if (node == null) {
            // Base case: null node is considered a valid BST
            return new TreeInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        // Recursively evaluate left and right subtrees
        TreeInfo leftInfo = traverseAndEvaluate(node.leftChild);
        TreeInfo rightInfo = traverseAndEvaluate(node.rightChild);

        // Check if current node and its subtrees form a valid BST
        if (leftInfo.isValidBST && rightInfo.isValidBST && node.value > leftInfo.maxValue && node.value < rightInfo.minValue) {
            // Calculate the sum of the BST subtree
            int currentSum = node.value + leftInfo.subtreeSum + rightInfo.subtreeSum;
            // Update the maximum BST sum
            maxBSTSum = Math.max(maxBSTSum, currentSum);
            // Return updated TreeInfo for the current subtree
            return new TreeInfo(true, currentSum, Math.min(node.value, leftInfo.minValue), Math.max(node.value, rightInfo.maxValue));
        } else {
            // Return TreeInfo indicating this subtree is not a valid BST
            return new TreeInfo(false, 0, 0, 0);
        }
    }
}
