package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileConverterApp extends JFrame {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JFileChooser fileChooser;
    private File[] selectedFiles;
    private ImageResizeWorker worker;
    private JTextField widthField;
    private JTextField heightField;
    private JButton downloadButton;
    private BufferedImage resizedImage;

    public FileConverterApp() {
        setTitle("File Converter");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.decode("#F0F0F0"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title Label
        JLabel titleLabel = new JLabel("File Converter", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // File selection button
        JButton chooseFilesButton = new JButton("Choose Files");
        chooseFilesButton.setBackground(Color.decode("#91B5A6"));
        chooseFilesButton.setForeground(Color.WHITE);
        chooseFilesButton.setFont(new Font("Arial", Font.PLAIN, 12));
        chooseFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFiles();
            }
        });
        gbc.gridy = 1;
        add(chooseFilesButton, gbc);

        // Width and Height fields
        JLabel widthLabel = new JLabel("Width:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(widthLabel, gbc);

        widthField = new JTextField("800", 5);
        gbc.gridx = 1;
        add(widthField, gbc);

        JLabel heightLabel = new JLabel("Height:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(heightLabel, gbc);

        heightField = new JTextField("600", 5);
        gbc.gridx = 1;
        add(heightField, gbc);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(progressBar, gbc);

        // Control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.decode("#F0F0F0"));

        JButton startButton = new JButton("Start");
        startButton.setBackground(Color.decode("#91B5A6"));
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Arial", Font.PLAIN, 12));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startResizing();
            }
        });
        controlPanel.add(startButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.decode("#F4A460"));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelResizing();
            }
        });
        controlPanel.add(cancelButton);

        gbc.gridy = 5;
        add(controlPanel, gbc);

        // Status label
        statusLabel = new JLabel("Status: Waiting for files...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 6;
        add(statusLabel, gbc);

        // Download button
        downloadButton = new JButton("Download Image");
        downloadButton.setBackground(Color.decode("#91B5A6"));
        downloadButton.setForeground(Color.BLACK);
        downloadButton.setFont(new Font("Arial", Font.PLAIN, 12));
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadImage();
            }
        });
        gbc.gridy = 7;
        add(downloadButton, gbc);

        // File chooser setup
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
    }

    private void chooseFiles() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();
            statusLabel.setText("Selected Files: " + selectedFiles.length);
        } else {
            statusLabel.setText("No files selected.");
        }
    }

    private void startResizing() {
        if (selectedFiles == null || selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select files to resize.", "No files", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable buttons during processing
        setControlsEnabled(false);

        // Start the resizing process using a SwingWorker
        worker = new ImageResizeWorker(selectedFiles);
        worker.execute();
    }

    private void cancelResizing() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
            statusLabel.setText("Conversion cancelled.");
            progressBar.setValue(0);
            setControlsEnabled(true);
        }
    }

    private void setControlsEnabled(boolean enabled) {
        fileChooser.setEnabled(enabled);
    }

    private class ImageResizeWorker extends SwingWorker<Void, Integer> {
        private File[] files;

        public ImageResizeWorker(File[] files) {
            this.files = files;
        }

        @Override
        protected Void doInBackground() {
            int count = 0;
            for (File file : files) {
                if (isCancelled()) {
                    break;
                }

                try {
                    BufferedImage originalImage = ImageIO.read(file);
                    int newWidth = Integer.parseInt(widthField.getText());
                    int newHeight = Integer.parseInt(heightField.getText());
                    resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                    g.dispose();

                    // Save the resized image in the 'images' directory of your project
                    String outputFilePath = System.getProperty("user.dir") + "/src/main/java/org/example/images/resized_" + file.getName();
                    ImageIO.write(resizedImage, "jpg", new File(outputFilePath));

                    // Update progress
                    count++;
                    int progress = (int) ((count / (float) files.length) * 100);
                    publish(progress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            for (int progress : chunks) {
                progressBar.setValue(progress);
                statusLabel.setText("Progress: " + progress + "%");
            }
        }

        @Override
        protected void done() {
            try {
                get(); // Retrieve result or exception
                statusLabel.setText("All conversions completed!");
                downloadButton.setEnabled(true); // Enable download button after completion
            } catch (InterruptedException | ExecutionException e) {
                statusLabel.setText("Error during conversion.");
                e.printStackTrace();
            } finally {
                setControlsEnabled(true);
            }
        }
    }

    private void downloadImage() {
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setDialogTitle("Save Image As");
        saveChooser.setSelectedFile(new File("resized_image.jpg"));
        int userSelection = saveChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = saveChooser.getSelectedFile();
            try {
                ImageIO.write(resizedImage, "jpg", fileToSave);
                JOptionPane.showMessageDialog(this, "Image saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileConverterApp app = new FileConverterApp();
            app.setVisible(true);
        });
    }
}
//Time Complexity is O(f)