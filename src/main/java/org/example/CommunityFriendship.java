package org.example;

import java.util.*;

public class CommunityFriendship {
    private int[] parent;
    private int[] rank;

    public CommunityFriendship(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    private boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false; // x and y are already connected
        }

        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }

    public String[] processFriendRequests(int n, int[][] restrictions, int[][] requests) {
        List<String> results = new ArrayList<>();

        for (int[] request : requests) {
            int u = request[0];
            int v = request[1];

            int rootU = find(u);
            int rootV = find(v);
            boolean canBeFriends = true;

            for (int[] restriction : restrictions) {
                int x = restriction[0];
                int y = restriction[1];

                int rootX = find(x);
                int rootY = find(y);

                if ((rootU == rootX && rootV == rootY) || (rootU == rootY && rootV == rootX)) {
                    canBeFriends = false;
                    break;
                }
            }

            if (canBeFriends) {
                union(u, v);
                results.add("approved");
            } else {
                results.add("denied");
            }
        }

        return results.toArray(new String[0]);
    }

    public static void main(String[] args) {
        int n = 3;
        int[][] restrictions = {{0, 1}};
        int[][] requests = {{0, 2}, {2, 1}};
        CommunityFriendship community = new CommunityFriendship(n);
        System.out.println(Arrays.toString(community.processFriendRequests(n, restrictions, requests)));
        // Output: [approved, denied]

        int n2 = 5;
        int[][] restrictions2 = {{0, 1}, {1, 2}, {2, 3}};
        int[][] requests2 = {{0, 4}, {1, 2}, {3, 1}, {3, 4}};
        CommunityFriendship community2 = new CommunityFriendship(n2);
        System.out.println(Arrays.toString(community2.processFriendRequests(n2, restrictions2, requests2)));
        // Output: [approved, denied, approved, denied]
    }
}

