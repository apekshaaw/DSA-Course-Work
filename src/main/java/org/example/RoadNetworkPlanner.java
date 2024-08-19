package org.example;

import java.util.*;

public class RoadNetworkPlanner {

    static class Edge {
        int from, to, weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node> {
        int vertex, distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static List<Edge> modifyConstructionTimes(int n, List<Edge> roads, int source, int destination, int targetTime) {
        int low = 1, high = 2 * (int) 1e9;

        List<Edge> result = new ArrayList<>();

        while (low <= high) {
            int mid = low + (high - low) / 2;

            // Update the roads under construction with the mid value
            List<Edge> modifiedRoads = new ArrayList<>();
            for (Edge edge : roads) {
                if (edge.weight == -1) {
                    modifiedRoads.add(new Edge(edge.from, edge.to, mid));
                } else {
                    modifiedRoads.add(edge);
                }
            }

            int currentTime = dijkstra(n, modifiedRoads, source, destination);

            if (currentTime == targetTime) {
                result = modifiedRoads;
                break;
            } else if (currentTime < targetTime) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    private static int dijkstra(int n, List<Edge> roads, int source, int destination) {
        List<List<Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }

        for (Edge edge : roads) {
            adjList.get(edge.from).add(edge);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;
        pq.offer(new Node(source, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            int currentVertex = currentNode.vertex;

            if (currentVertex == destination) {
                return distances[currentVertex];
            }

            for (Edge edge : adjList.get(currentVertex)) {
                int newDist = distances[currentVertex] + edge.weight;
                if (newDist < distances[edge.to]) {
                    distances[edge.to] = newDist;
                    pq.offer(new Node(edge.to, newDist));
                }
            }
        }

        return distances[destination];
    }

    public static void main(String[] args) {
        int n = 5;
        List<Edge> roads = Arrays.asList(
                new Edge(4, 1, -1),
                new Edge(2, 0, -1),
                new Edge(0, 3, -1),
                new Edge(4, 3, -1)
        );
        int source = 0;
        int destination = 1;
        int targetTime = 5;

        List<Edge> result = modifyConstructionTimes(n, roads, source, destination, targetTime);
        for (Edge edge : result) {
            System.out.println("From: " + edge.from + ", To: " + edge.to + ", Weight: " + edge.weight);
        }
        // Expected output: [[4, 1, 1], [2, 0, 1], [0, 3, 3], [4, 3, 1]]
    }
}

