package org.example;

import java.util.*;

public class CityPlanner {

    // Inner class to represent a road connection with a destination and time cost
    static class Road {
        int destination, travelTime;
        public Road(int destination, int travelTime) {
            this.destination = destination;
            this.travelTime = travelTime;
        }
    }

    public static void main(String[] args) {
        int locations = 5; // Total number of locations
        int[][] paths = {
                {4, 1, -1},
                {2, 0, -1},
                {0, 3, -1},
                {4, 3, -1}
        }; // Paths with travel durations, -1 indicates they are incomplete
        int start = 0;
        int end = 1;
        int requiredTime = 5;

        List<int[]> modifiedPaths = adjustConstructionTimes(locations, paths, start, end, requiredTime);
        for (int[] path : modifiedPaths) {
            System.out.println(Arrays.toString(path));
        }
    }

    public static List<int[]> adjustConstructionTimes(int locations, int[][] paths, int start, int end, int requiredTime) {
        // Creating the graph using adjacency lists
        List<List<Road>> cityMap = new ArrayList<>();
        for (int i = 0; i < locations; i++) {
            cityMap.add(new ArrayList<>());
        }

        // Storing the paths still under construction
        List<int[]> unfinishedPaths = new ArrayList<>();

        // Building the graph and recording paths under construction
        for (int[] path : paths) {
            if (path[2] == -1) {
                unfinishedPaths.add(path);
            } else {
                cityMap.get(path[0]).add(new Road(path[1], path[2]));
                cityMap.get(path[1]).add(new Road(path[0], path[2]));
            }
        }

        // Initially setting all under-construction paths to have a travel time of 1
        for (int[] path : unfinishedPaths) {
            path[2] = 1;
            cityMap.get(path[0]).add(new Road(path[1], path[2]));
            cityMap.get(path[1]).add(new Road(path[0], path[2]));
        }

        // Find the shortest path with current settings
        int shortestTime = calculateShortestPath(cityMap, start, end, locations);

        if (shortestTime == requiredTime) {
            // If the initial configuration matches the required travel time, return paths as-is
            return Arrays.asList(paths);
        } else {
            // Adjusting travel times for under-construction paths
            int timeDifference = requiredTime - shortestTime;
            for (int[] path : unfinishedPaths) {
                if (timeDifference > 0) {
                    path[2] += timeDifference; // Increase the travel time
                    cityMap.get(path[0]).clear();
                    cityMap.get(path[1]).clear();
                }
            }

            // Rebuilding the graph with updated times
            for (int[] path : paths) {
                cityMap.get(path[0]).add(new Road(path[1], path[2]));
                cityMap.get(path[1]).add(new Road(path[0], path[2]));
            }

            // Return the modified path configurations
            return Arrays.asList(paths);
        }
    }

    // Dijkstra's algorithm to calculate the shortest path between start and end points
    public static int calculateShortestPath(List<List<Road>> cityMap, int start, int end, int locations) {
        int[] minDistances = new int[locations];
        Arrays.fill(minDistances, Integer.MAX_VALUE);
        minDistances[start] = 0;
        PriorityQueue<Road> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(road -> road.travelTime));
        priorityQueue.add(new Road(start, 0));

        while (!priorityQueue.isEmpty()) {
            Road currentRoad = priorityQueue.poll();
            int currentLocation = currentRoad.destination;
            int currentTime = currentRoad.travelTime;

            if (currentTime > minDistances[currentLocation]) continue;

            for (Road road : cityMap.get(currentLocation)) {
                int nextLocation = road.destination;
                int travelTime = road.travelTime;
                if (minDistances[currentLocation] + travelTime < minDistances[nextLocation]) {
                    minDistances[nextLocation] = minDistances[currentLocation] + travelTime;
                    priorityQueue.add(new Road(nextLocation, minDistances[nextLocation]));
                }
            }
        }
        return minDistances[end];
    }
}
