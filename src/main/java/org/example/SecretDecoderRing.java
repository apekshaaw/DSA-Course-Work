package org.example;

public class SecretDecoderRing {

    public static String decodeMessage(String s, int[][] shifts) {
        char[] message = s.toCharArray();

        for (int[] shift : shifts) {
            int start = shift[0];
            int end = shift[1];
            int direction = shift[2];

            for (int i = start; i <= end; i++) {
                message[i] = rotateChar(message[i], direction);
            }
        }

        return new String(message);
    }

    private static char rotateChar(char c, int direction) {
        if (direction == 1) {
            // Clockwise rotation
            return c == 'z' ? 'a' : (char) (c + 1);
        } else {
            // Counter-clockwise rotation
            return c == 'a' ? 'z' : (char) (c - 1);
        }
    }

    public static void main(String[] args) {
        String s = "hello";
        int[][] shifts = {{0, 1, 1}, {2, 3, 0}, {0, 2, 1}};
        System.out.println(decodeMessage(s, shifts)); // Output: jglko
    }
}

