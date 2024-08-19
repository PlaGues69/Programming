package org.example;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestProcessor {

    // Define the Disjoint Set data structure
    static class DisjointSet {
        private int[] parent;
        private int[] rank;

        // Constructor to initialize the Disjoint Set data structure
        public DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i; // Initialize each node as its own parent
                rank[i] = 0;   // Initialize rank to 0
            }
        }

        // Find the root of the set containing the node x
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        // Union operation to merge the sets containing x and y
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) {
                return false; // x and y are already in the same set
            }
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX; // Attach the smaller tree under the larger one
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY; // Attach the smaller tree under the larger one
            } else {
                parent[rootY] = rootX; // Same rank, attach and increase rank
                rank[rootX]++;
            }
            return true;
        }

        // Check if nodes x and y belong to the same set
        public boolean areConnected(int x, int y) {
            return find(x) == find(y);
        }
    }

    // Function to evaluate friend requests and determine their approval status
    public static List<String> evaluateFriendRequests(int numberOfHouses, int[][] restrictions, int[][] requests) {
        DisjointSet ds = new DisjointSet(numberOfHouses); // Initialize Disjoint Set for numberOfHouses houses
        List<String> results = new ArrayList<>();

        // Process each friend request
        for (int[] request : requests) {
            int houseA = request[0];
            int houseB = request[1];
            boolean canBeApproved = true;

            // Check if accepting the request violates any restriction
            for (int[] restriction : restrictions) {
                int restrictedHouseA = restriction[0];
                int restrictedHouseB = restriction[1];
                if ((ds.areConnected(houseA, restrictedHouseA) && ds.areConnected(houseB, restrictedHouseB)) ||
                        (ds.areConnected(houseA, restrictedHouseB) && ds.areConnected(houseB, restrictedHouseA))) {
                    canBeApproved = false;
                    break; // No need to check more restrictions
                }
            }

            // Approve the request if it does not violate any restrictions
            if (canBeApproved) {
                ds.union(houseA, houseB);
                results.add("approved");
            } else {
                // Otherwise, deny the request
                results.add("denied");
            }
        }

        return results; // Return the results of the requests
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        int numberOfHouses = 5; // Total number of houses
        int[][] restrictions = {{0, 1}, {1, 2}, {2, 3}}; // Restrictions on friendships
        int[][] requests = {{0, 4}, {1, 2}, {3, 1}, {3, 4}}; // List of friend requests
        List<String> results = evaluateFriendRequests(numberOfHouses, restrictions, requests);

        // Print the results of each friend request
        for (String result : results) {
            System.out.println(result);
        }
    }
}
