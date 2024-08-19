package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class DeliveryOptimizationApp extends JFrame {
    private JTextArea pointsTextArea;
    private JTextField capacityField, distanceField;
    private JLabel statusLabel;
    private JComboBox<String> algorithmComboBox;
    private ExecutorService executorService;
    private Future<?> optimizationTask;
    private List<String[]> deliveryPoints;
    private Graph<Integer, DefaultEdge> graph;
    private List<Integer> optimizedRoute;

    public DeliveryOptimizationApp() {
        setTitle("Delivery Route Optimization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.decode("#F0F0F0"));
        executorService = Executors.newFixedThreadPool(4);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Delivery points frame
        JLabel pointsLabel = new JLabel("Delivery Points (Address, Priority):");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(pointsLabel, gbc);

        JButton importButton = new JButton("Import Points");
        importButton.setBackground(Color.decode("#91B5A6"));
        importButton.setFont(new Font("Arial", Font.PLAIN, 12));
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importPoints();
            }
        });
        gbc.gridx = 1;
        add(importButton, gbc);

        pointsTextArea = new JTextArea(10, 80);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(new JScrollPane(pointsTextArea), gbc);

        // Vehicle options frame
        JLabel capacityLabel = new JLabel("Vehicle Capacity:");
        capacityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(capacityLabel, gbc);

        capacityField = new JTextField(10);
        gbc.gridx = 1;
        add(capacityField, gbc);

        JLabel distanceLabel = new JLabel("Max Driving Distance:");
        distanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(distanceLabel, gbc);

        distanceField = new JTextField(10);
        gbc.gridx = 1;
        add(distanceField, gbc);

        // Algorithm selection frame
        JLabel algorithmLabel = new JLabel("Optimization Algorithm:");
        algorithmLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(algorithmLabel, gbc);

        algorithmComboBox = new JComboBox<>(new String[]{"Nearest Neighbor"});
        gbc.gridx = 1;
        add(algorithmComboBox, gbc);

        // Control buttons frame
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.decode("#F0F0F0"));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(controlPanel, gbc);

        JButton optimizeButton = new JButton("Optimize Route");
        optimizeButton.setBackground(Color.decode("#91B5A6"));
        optimizeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        optimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optimizeRoute();
            }
        });
        controlPanel.add(optimizeButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.decode("#F4A460"));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelOptimization();
            }
        });
        controlPanel.add(cancelButton);

        // Status frame
        statusLabel = new JLabel("Status: Waiting for input...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 6;
        add(statusLabel, gbc);

        // Route visualization
        JButton visualizeButton = new JButton("Visualize Route");
        visualizeButton.setBackground(Color.decode("#91B5A6"));
        visualizeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        visualizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizeRoute();
            }
        });
        gbc.gridy = 7;
        add(visualizeButton, gbc);
    }

    private void importPoints() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner scanner = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                pointsTextArea.setText(content.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void optimizeRoute() {
        parsePoints();
        if (deliveryPoints.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please import or enter delivery points.", "No points", JOptionPane.WARNING_MESSAGE);
            return;
        }

        statusLabel.setText("Status: Optimizing route...");
        optimizationTask = executorService.submit(() -> {
            graph = new SimpleWeightedGraph<>(DefaultEdge.class);
            for (int i = 0; i < deliveryPoints.size(); i++) {
                graph.addVertex(i);
            }
            for (int i = 0; i < deliveryPoints.size(); i++) {
                for (int j = i + 1; j < deliveryPoints.size(); j++) {
                    double weight = randomWeight();
                    graph.setEdgeWeight(graph.addEdge(i, j), weight);
                }
            }

            TwoApproxMetricTSP<Integer, DefaultEdge> tsp = new TwoApproxMetricTSP<>();
            optimizedRoute = tsp.getTour(graph).getVertexList();
            SwingUtilities.invokeLater(() -> statusLabel.setText("Status: Optimization complete!"));
        });
    }

    private double randomWeight() {
        return 10.0 + (90.0 * Math.random());
    }

    private void parsePoints() {
        String[] lines = pointsTextArea.getText().split("\n");
        deliveryPoints = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            deliveryPoints.add(parts);
        }
    }

    private void cancelOptimization() {
        if (optimizationTask != null && !optimizationTask.isDone()) {
            optimizationTask.cancel(true);
            statusLabel.setText("Status: Optimization cancelled.");
        }
    }

    private void visualizeRoute() {
        if (optimizedRoute == null || optimizedRoute.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please optimize a route first.", "No route", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Optimized Delivery Route");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                int margin = 50;

                for (int i = 0; i < optimizedRoute.size() - 1; i++) {
                    int x1 = margin + (int) (Math.random() * (width - 2 * margin));
                    int y1 = margin + (int) (Math.random() * (height - 2 * margin));
                    int x2 = margin + (int) (Math.random() * (width - 2 * margin));
                    int y2 = margin + (int) (Math.random() * (height - 2 * margin));
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DeliveryOptimizationApp app = new DeliveryOptimizationApp();
            app.setVisible(true);
        });
    }
}
