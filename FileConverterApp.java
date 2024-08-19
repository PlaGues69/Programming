package org.example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileConverterApp extends JFrame {

    private JButton btnSelectFiles;
    private JComboBox<String> cboFormat;
    private JButton btnStart;
    private JProgressBar progressBar;
    private JTextArea txtStatus;
    private JButton btnCancel;
    private JList<ImageIcon> listResizedImages;
    private JList<String> listConvertedDocs;

    private DefaultListModel<ImageIcon> resizedImagesListModel;
    private DefaultListModel<String> convertedDocsListModel;
    private List<File> filesToConvert;
    private ExecutorService executor;
    private boolean cancelRequested = false;

    public FileConverterApp() {
        setTitle("File Converter Application");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Panel for file selection and actions
        JPanel panelFileActions = new JPanel();
        panelFileActions.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFileActions.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnSelectFiles = new JButton("Select Files");
        btnSelectFiles.setFont(new Font("Arial", Font.BOLD, 14));
        btnSelectFiles.setBackground(new Color(70, 130, 180));
        btnSelectFiles.setForeground(Color.WHITE);
        btnSelectFiles.setPreferredSize(new Dimension(150, 30));
        btnSelectFiles.addActionListener(e -> chooseFiles());
        panelFileActions.add(btnSelectFiles);

        String[] formatOptions = {"PDF to DOCX", "Resize Image"};
        cboFormat = new JComboBox<>(formatOptions);
        cboFormat.setFont(new Font("Arial", Font.BOLD, 14));
        cboFormat.setPreferredSize(new Dimension(150, 30));
        panelFileActions.add(cboFormat);

        btnStart = new JButton("Start Conversion");
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStart.setBackground(new Color(34, 139, 34));
        btnStart.setForeground(Color.WHITE);
        btnStart.setPreferredSize(new Dimension(150, 30));
        btnStart.addActionListener(e -> beginConversion());
        panelFileActions.add(btnStart);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(255, 69, 58));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(100, 30));
        btnCancel.addActionListener(e -> abortConversion());
        panelFileActions.add(btnCancel);

        add(panelFileActions, BorderLayout.NORTH);

        // Panel for progress bar and status updates
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BorderLayout());
        panelCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(1150, 30));
        panelCenter.add(progressBar, BorderLayout.NORTH);

        txtStatus = new JTextArea(10, 30);
        txtStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStatus.setEditable(false);
        JScrollPane statusScrollPane = new JScrollPane(txtStatus);
        panelCenter.add(statusScrollPane, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        // List for resized images
        resizedImagesListModel = new DefaultListModel<>();
        listResizedImages = new JList<>(resizedImagesListModel);
        listResizedImages.setCellRenderer(new ImageIconRenderer());
        listResizedImages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listResizedImages.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        File imageFile = new File("converted_files", resizedImagesListModel.getElementAt(index).getDescription());
                        openFile(imageFile);
                    }
                }
            }
        });
        JScrollPane resizedImagesPane = new JScrollPane(listResizedImages);
        resizedImagesPane.setBorder(BorderFactory.createTitledBorder("Resized Images"));
        resizedImagesPane.setPreferredSize(new Dimension(580, 300));

        // List for converted documents
        convertedDocsListModel = new DefaultListModel<>();
        listConvertedDocs = new JList<>(convertedDocsListModel);
        listConvertedDocs.setFont(new Font("Arial", Font.PLAIN, 14));
        listConvertedDocs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listConvertedDocs.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        File docFile = new File("converted_files", convertedDocsListModel.getElementAt(index));
                        openFile(docFile);
                    }
                }
            }
        });
        JScrollPane convertedDocsPane = new JScrollPane(listConvertedDocs);
        convertedDocsPane.setBorder(BorderFactory.createTitledBorder("Converted Documents"));
        convertedDocsPane.setPreferredSize(new Dimension(580, 300));

        // Split pane for displaying lists
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, resizedImagesPane, convertedDocsPane);
        splitPane.setDividerLocation(600); // Initial divider position
        add(splitPane, BorderLayout.SOUTH);
    }

    private void chooseFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"), "Desktop"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("All Files", "*"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToConvert = List.of(fileChooser.getSelectedFiles());
            txtStatus.append("Files selected:\n");
            for (File file : filesToConvert) {
                txtStatus.append(file.getAbsolutePath() + "\n");
            }
        }
    }

    private void beginConversion() {
        if (filesToConvert == null || filesToConvert.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select files before starting conversion.");
            return;
        }

        cancelRequested = false;
        progressBar.setValue(0);
        txtStatus.append("Conversion in progress...\n");

        String format = (String) cboFormat.getSelectedItem();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File file : filesToConvert) {
            executor.submit(new FileConverterTask(file, format));
        }

        executor.shutdown();
    }

    private void abortConversion() {
        cancelRequested = true;
        if (executor != null) {
            executor.shutdownNow();
        }
        txtStatus.append("Conversion has been cancelled.\n");
    }

    private class FileConverterTask extends SwingWorker<Void, String> {
        private final File file;
        private final String format;

        public FileConverterTask(File file, String format) {
            this.file = file;
            this.format = format;
        }

        @Override
        protected Void doInBackground() {
            try {
                if (cancelRequested) return null;

                publish("Processing: " + file.getName());
                // Simulate conversion process
                Thread.sleep(2000);

                // Simulate conversion and save result
                String outputFileName = performConversion(file, format);
                saveConvertedFile(file, outputFileName);

                publish("Completed: " + outputFileName);

                // Update progress
                int progress = (int) ((double) (filesToConvert.indexOf(file) + 1) / filesToConvert.size() * 100);
                setProgress(progress);
            } catch (InterruptedException | IOException e) {
                publish("Conversion interrupted: " + file.getName());
            }
            return null;
        }

        @Override
        protected void process(List<String> updates) {
            for (String update : updates) {
                txtStatus.append(update + "\n");
                if (update.startsWith("Completed: ")) {
                    String outputFileName = update.substring(11);
                    if (outputFileName.endsWith(".docx")) {
                        convertedDocsListModel.addElement(outputFileName);
                    } else if (outputFileName.endsWith(".jpg") || outputFileName.endsWith(".png")) {
                        ImageIcon icon = new ImageIcon("converted_files/" + outputFileName);
                        icon.setDescription(outputFileName);
                        resizedImagesListModel.addElement(icon);
                    }
                }
            }
        }

        @Override
        protected void done() {
            setProgress(100);
            txtStatus.append("All files processed.\n");
        }

        private String performConversion(File file, String format) {
            // This method simulates the conversion process
            if (format.equals("PDF to DOCX")) {
                return file.getName().replace(".pdf", ".docx");
            } else if (format.equals("Resize Image")) {
                return file.getName().replace(".png", "_resized.png");
            }
            return file.getName();
        }

        private void saveConvertedFile(File inputFile, String outputFileName) throws IOException {
            // This method simulates saving the converted file
            File outputFile = new File("converted_files", outputFileName);
            Files.copy(inputFile.toPath(), outputFile.toPath());
        }
    }

    private void openFile(File file) {
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                txtStatus.append("Failed to open file: " + file.getName() + "\n");
            }
        } else {
            txtStatus.append("File not found: " + file.getName() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileConverterApp::new);
    }

    // Custom cell renderer for ImageIcons
    private class ImageIconRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setPreferredSize(new Dimension(100, 100));
                return label;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
