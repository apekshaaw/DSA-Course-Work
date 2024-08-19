package org.example;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

class Result {
    boolean isBST;
    int sum;
    int minVal;
    int maxVal;

    Result(boolean isBST, int sum, int minVal, int maxVal) {
        this.isBST = isBST;
        this.sum = sum;
        this.minVal = minVal;
        this.maxVal = maxVal;
    }
}

public class MagicalGroveFinder {

    private int maxSum = 0;

    public int findLargestMagicalGrove(TreeNode root) {
        findGrove(root);
        return maxSum;
    }

    private Result findGrove(TreeNode node) {
        if (node == null) {
            return new Result(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        Result leftResult = findGrove(node.left);
        Result rightResult = findGrove(node.right);

        if (leftResult.isBST && rightResult.isBST && node.val > leftResult.maxVal && node.val < rightResult.minVal) {
            int sum = node.val + leftResult.sum + rightResult.sum;
            maxSum = Math.max(maxSum, sum);
            int minVal = Math.min(node.val, leftResult.minVal);
            int maxVal = Math.max(node.val, rightResult.maxVal);
            return new Result(true, sum, minVal, maxVal);
        } else {
            return new Result(false, 0, 0, 0);
        }
    }

    public static void main(String[] args) {
        // Example: [1, 4, 3, 2, 4, 2, 5, null, null, null, null, null, null, 4, 6]
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(4);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(2);
        root.right.right = new TreeNode(5);
        root.right.right.left = new TreeNode(4);
        root.right.right.right = new TreeNode(6);

        MagicalGroveFinder finder = new MagicalGroveFinder();
        System.out.println(finder.findLargestMagicalGrove(root)); // Output: 20
    }
}
