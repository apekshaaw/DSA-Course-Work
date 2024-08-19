package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {
    private JTextField inputField;
    private String expression = "";

    public BasicCalculatorGUI() {
        setTitle("Basic Calculator");
        setSize(300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create input field
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        inputField.setHorizontalAlignment(SwingConstants.RIGHT);
        inputField.setBackground(Color.LIGHT_GRAY);
        inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(inputField, gbc);

        // Define buttons
        String[][] buttons = {
                {"7", "8", "9"},
                {"4", "5", "6"},
                {"1", "2", "3"},
                {"0", "ce", "="},
                {"/", "x", "-"},
                {"+", "(", ")"}
        };

        int row = 1;
        for (String[] buttonRow : buttons) {
            gbc.gridwidth = 1;
            int col = 0;
            for (String text : buttonRow) {
                JButton btn;
                if (text.equals("=")) {
                    btn = createButton(text, Color.decode("#91B5A6"), Color.WHITE, e -> evaluateExpression());
                } else if (text.equals("ce")) {
                    btn = createButton(text, Color.decode("#F4A460"), Color.WHITE, e -> clearExpression());
                } else if ("/x+-()".contains(text)) {
                    btn = createButton(text, Color.decode("#91B5A6"), Color.WHITE, e -> addToExpression(text));
                } else {
                    btn = createButton(text, Color.decode("#4D4D4D"), Color.WHITE, e -> addToExpression(text));
                }
                gbc.gridx = col;
                gbc.gridy = row;
                add(btn, gbc);
                col++;
            }
            row++;
        }

        // Configure grid layout
        for (int i = 0; i < 7; i++) {
            gbc.gridy = i;
            gbc.weighty = 1.0;
        }
        for (int i = 0; i < 4; i++) {
            gbc.gridx = i;
            gbc.weightx = 1.0;
        }
    }

    private JButton createButton(String text, Color bgColor, Color fgColor, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.addActionListener(actionListener);
        return button;
    }

    private void addToExpression(String value) {
        expression += value;
        inputField.setText(expression);
    }

    private void clearExpression() {
        expression = "";
        inputField.setText("");
    }

    private void evaluateExpression() {
        try {
            String result = evaluateCustomExpression(expression.replace("x", "*"));
            inputField.setText(result);
            expression = result;
        } catch (Exception e) {
            inputField.setText("Error");
            expression = "";
        }
    }

    // Custom algorithm to evaluate the mathematical expression
    private String evaluateCustomExpression(String expression) throws Exception {
        Stack<Integer> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int num = 0;
        boolean numFlag = false;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                num = num * 10 + (ch - '0');
                numFlag = true;
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                if (numFlag) {
                    numbers.push(num);
                    num = 0;
                    numFlag = false;
                }
                while (!operators.isEmpty() && operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // pop the '('
            } else if (isOperator(ch)) {
                if (numFlag) {
                    numbers.push(num);
                    num = 0;
                    numFlag = false;
                }
                while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }
        if (numFlag) {
            numbers.push(num);
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return String.valueOf(numbers.pop());
    }

    private int applyOperation(char op, int b, int a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicCalculatorGUI calculator = new BasicCalculatorGUI();
            calculator.setVisible(true);
        });
    }
}

//Time Complexity is O(n)