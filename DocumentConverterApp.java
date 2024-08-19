package org.example;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DocumentConverterApp extends JFrame {
    private JTextField selectedFilesField;
    private JComboBox<String> conversionOptionsComboBox;
    private JProgressBar progressIndicator;
    private JTextArea logsArea;
    private JButton convertButton, stopButton;
    private JFileChooser fileChooser;
    private ExecutorService executor;
    private SwingWorker<Void, ConversionJob> currentJob;
    private JPanel resultPanel;
    private DefaultListModel<File> fileModel;

    public DocumentConverterApp() {
        setTitle("Document Converter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        selectedFilesField = new JTextField();
        selectedFilesField.setEditable(false);
        JButton chooseFilesButton = new JButton("Choose Files");
        conversionOptionsComboBox = new JComboBox<>(new String[]{"Convert PDF to DOCX", "Resize Images"});
        progressIndicator = new JProgressBar(0, 100);
        progressIndicator.setStringPainted(true);
        logsArea = new JTextArea();
        logsArea.setEditable(false);
        convertButton = new JButton("Convert");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Supported Files", "pdf", "jpg", "png"));

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        fileModel = new DefaultListModel<>();
        JList<File> fileList = new JList<>(fileModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel fileSelectionPanel = new JPanel();
        fileSelectionPanel.setLayout(new BorderLayout());
        fileSelectionPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        fileSelectionPanel.add(chooseFilesButton, BorderLayout.SOUTH);

        JPanel conversionPanel = new JPanel();
        conversionPanel.setLayout(new BorderLayout());
        conversionPanel.add(conversionOptionsComboBox, BorderLayout.NORTH);
        conversionPanel.add(progressIndicator, BorderLayout.CENTER);

        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new BorderLayout());
        logsPanel.add(new JScrollPane(logsArea), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.add(convertButton);
        controlPanel.add(stopButton);
        logsPanel.add(controlPanel, BorderLayout.SOUTH);

        add(fileSelectionPanel, BorderLayout.NORTH);
        add(conversionPanel, BorderLayout.CENTER);
        add(logsPanel, BorderLayout.SOUTH);
        add(new JScrollPane(resultPanel), BorderLayout.EAST);

        chooseFilesButton.addActionListener(e -> chooseFiles());
        convertButton.addActionListener(e -> initiateConversion());
        stopButton.addActionListener(e -> haltConversion());

        executor = Executors.newFixedThreadPool(4);
    }

    private void chooseFiles() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] chosenFiles = fileChooser.getSelectedFiles();
            fileModel.clear();
            for (File file : chosenFiles) {
                fileModel.addElement(file);
            }
        }
    }

    private void initiateConversion() {
        if (fileModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "Please select files first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] chosenFiles = new File[fileModel.getSize()];
        for (int i = 0; i < fileModel.getSize(); i++) {
            chosenFiles[i] = fileModel.get(i);
        }

        String conversionOption = (String) conversionOptionsComboBox.getSelectedItem();
        progressIndicator.setValue(0);
        logsArea.setText("");
        convertButton.setEnabled(false);
        stopButton.setEnabled(true);

        resultPanel.removeAll();

        currentJob = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                int totalFiles = chosenFiles.length;
                for (int i = 0; i < totalFiles && !isCancelled(); i++) {
                    File file = chosenFiles[i];
                    ConversionJob job = new ConversionJob(file, conversionOption);
                    executor.submit(job);
                    publish(job);

                    try {
                        job.get();
                    } catch (Exception e) {
                        publish(new ConversionJob(file, conversionOption, e));
                    }

                    int progress = (int) ((i + 1) / (float) totalFiles * 100);
                    setProgress(progress);
                }
                return null;
            }

            @Override
            protected void process(List<ConversionJob> chunks) {
                for (ConversionJob job : chunks) {
                    if (job.getException() == null) {
                        logsArea.append("Successfully converted: " + job.getFile().getName() + " (" + job.getConversionType() + ")\n");
                        displayResult(job.getFile());
                    } else {
                        logsArea.append("Failed to convert: " + job.getFile().getName() + " (" + job.getConversionType() + ") - " + job.getException().getMessage() + "\n");
                    }
                }
            }

            @Override
            protected void done() {
                convertButton.setEnabled(true);
                stopButton.setEnabled(false);
                if (!isCancelled()) {
                    JOptionPane.showMessageDialog(DocumentConverterApp.this, "Conversion completed!", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(DocumentConverterApp.this, "Conversion was stopped!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };

        currentJob.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressIndicator.setValue((Integer) evt.getNewValue());
            }
        });

        currentJob.execute();
    }

    private void haltConversion() {
        if (currentJob != null) {
            currentJob.cancel(true);
        }
    }

    private void displayResult(File file) {
        JLabel resultLabel = new JLabel("Converted File: " + file.getName());
        resultPanel.add(resultLabel);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DocumentConverterApp app = new DocumentConverterApp();
            app.setVisible(true);
        });
    }

    private static class ConversionJob implements Runnable {
        private final File file;
        private final String conversionType;
        private Exception exception;

        public ConversionJob(File file, String conversionType) {
            this.file = file;
            this.conversionType = conversionType;
        }

        public ConversionJob(File file, String conversionType, Exception exception) {
            this.file = file;
            this.conversionType = conversionType;
            this.exception = exception;
        }

        public File getFile() {
            return file;
        }

        public String getConversionType() {
            return conversionType;
        }

        public Exception getException() {
            return exception;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                // Implement the actual conversion logic here
            } catch (InterruptedException e) {
                exception = e;
            }
        }

        public void get() {
        }
    }
}
