package org.example;

import java.util.Arrays;
import java.util.Random;

public class TravelingSalesmanHillClimbing {

    private static final Random rand = new Random();

    public static int[] hillClimbing(int[][] distanceMatrix) {
        int n = distanceMatrix.length;
        int[] currentSolution = generateInitialSolution(n);
        int currentCost = calculateCost(currentSolution, distanceMatrix);

        boolean improvement = true;

        while (improvement) {
            improvement = false;

            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    int[] newSolution = currentSolution.clone();
                    swap(newSolution, i, j);
                    int newCost = calculateCost(newSolution, distanceMatrix);

                    if (newCost < currentCost) {
                        currentSolution = newSolution;
                        currentCost = newCost;
                        improvement = true;
                    }
                }
            }
        }

        return currentSolution;
    }

    private static int[] generateInitialSolution(int n) {
        int[] solution = new int[n];
        for (int i = 0; i < n; i++) {
            solution[i] = i;
        }
        shuffleArray(solution);
        return solution;
    }

    private static void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private static int calculateCost(int[] solution, int[][] distanceMatrix) {
        int cost = 0;
        for (int i = 0; i < solution.length - 1; i++) {
            cost += distanceMatrix[solution[i]][solution[i + 1]];
        }
        cost += distanceMatrix[solution[solution.length - 1]][solution[0]]; // return to the starting city
        return cost;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String[] args) {
        int[][] distanceMatrix = {
                {0, 29, 20, 21},
                {29, 0, 15, 17},
                {20, 15, 0, 28},
                {21, 17, 28, 0}
        };

        int[] bestTour = hillClimbing(distanceMatrix);
        System.out.println("Best Tour: " + Arrays.toString(bestTour));
        System.out.println("Tour Cost: " + calculateCost(bestTour, distanceMatrix));
    }
}

//Time Complexity is O(n*n)
