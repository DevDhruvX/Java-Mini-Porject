package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class LabTestManagementWindow extends JFrame {
    private Connection connection;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextField testNameField;
    private JComboBox<String> testTypeComboBox;
    private JTextField testDateField;
    private JTextField resultValueField;
    private JTextField normalRangeField;
    private JComboBox<String> statusComboBox;
    private JTextArea notesArea;
    private JTextField reportFilePathField;

    private JTable testTable;
    private DefaultTableModel tableModel;
    private JTextArea testDetailsArea;

    public LabTestManagementWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadPatients();
        loadDoctors();
        loadLabTests();
    }

    private void initializeGUI() {
        setTitle("Laboratory Test Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Laboratory Test Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add Test Tab
        JPanel addTestPanel = createAddTestPanel();
        tabbedPane.addTab("Add Lab Test", addTestPanel);

        // View Tests Tab
        JPanel viewTestsPanel = createViewTestsPanel();
        tabbedPane.addTab("View Lab Tests", viewTestsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createAddTestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Patient selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientComboBox = new JComboBox<>();
        patientComboBox.setPreferredSize(new Dimension(200, 25));
        formPanel.add(patientComboBox, gbc);

        // Doctor selection
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 3;
        doctorComboBox = new JComboBox<>();
        doctorComboBox.setPreferredSize(new Dimension(200, 25));
        formPanel.add(doctorComboBox, gbc);

        // Test name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Test Name:"), gbc);
        gbc.gridx = 1;
        testNameField = new JTextField(20);
        testNameField.setToolTipText("e.g., Complete Blood Count, Blood Sugar");
        formPanel.add(testNameField, gbc);

        // Test type
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Test Type:"), gbc);
        gbc.gridx = 3;
        testTypeComboBox = new JComboBox<>(new String[] {
                "Blood Test", "Urine Test", "X-Ray", "CT Scan", "MRI",
                "Ultrasound", "ECG", "Echo", "Pathology", "Microbiology", "Other"
        });
        formPanel.add(testTypeComboBox, gbc);

        // Test date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Test Date:"), gbc);
        gbc.gridx = 1;
        testDateField = new JTextField(LocalDate.now().toString(), 15);
        testDateField.setToolTipText("YYYY-MM-DD format");
        formPanel.add(testDateField, gbc);

        // Status
        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        statusComboBox = new JComboBox<>(new String[] {
                "Pending", "In Progress", "Completed", "Reviewed", "Cancelled"
        });
        formPanel.add(statusComboBox, gbc);

        // Result value
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Result Value:"), gbc);
        gbc.gridx = 1;
        resultValueField = new JTextField(20);
        resultValueField.setToolTipText("e.g., 120 mg/dL, Normal, Positive");
        formPanel.add(resultValueField, gbc);

        // Normal range
        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Normal Range:"), gbc);
        gbc.gridx = 3;
        normalRangeField = new JTextField(20);
        normalRangeField.setToolTipText("e.g., 70-100 mg/dL, Negative");
        formPanel.add(normalRangeField, gbc);

        // Report file path
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Report File:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        reportFilePathField = new JTextField(30);
        reportFilePathField.setToolTipText("Path to test report file");
        formPanel.add(reportFilePathField, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> browseReportFile());
        formPanel.add(browseBtn, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(notesArea), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton saveBtn = new JButton("Save Lab Test");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.addActionListener(e -> saveLabTest());

        JButton updateBtn = new JButton("Update Test Result");
        updateBtn.setBackground(new Color(52, 152, 219));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> updateTestResult());

        JButton clearBtn = new JButton("Clear Form");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(saveBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createViewTestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Patient:"));

        JComboBox<String> filterPatientComboBox = new JComboBox<>();
        filterPatientComboBox.addItem("All Patients");
        loadPatientsToComboBox(filterPatientComboBox);
        filterPatientComboBox.addActionListener(e -> filterLabTests(filterPatientComboBox));
        filterPanel.add(filterPatientComboBox);

        filterPanel.add(new JLabel("Status:"));
        JComboBox<String> filterStatusComboBox = new JComboBox<>();
        filterStatusComboBox.addItem("All Status");
        filterStatusComboBox.addItem("Pending");
        filterStatusComboBox.addItem("In Progress");
        filterStatusComboBox.addItem("Completed");
        filterStatusComboBox.addItem("Reviewed");
        filterStatusComboBox.addActionListener(e -> filterByStatus(filterStatusComboBox));
        filterPanel.add(filterStatusComboBox);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadLabTests());
        filterPanel.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        String[] columns = { "ID", "Patient", "Doctor", "Test Name", "Type", "Date", "Status", "Result" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        testTable = new JTable(tableModel);
        testTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testTable.setRowHeight(25);
        testTable.getTableHeader().setReorderingAllowed(false);

        // Add mouse listener for row selection
        testTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    showTestDetails();
                } else if (e.getClickCount() == 2) {
                    editSelectedTest();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(testTable);
        tableScrollPane.setPreferredSize(new Dimension(1050, 250));

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Test Details"));
        detailsPanel.setPreferredSize(new Dimension(1050, 200));

        testDetailsArea = new JTextArea();
        testDetailsArea.setEditable(false);
        testDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        testDetailsArea.setBackground(new Color(248, 249, 250));
        testDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(testDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsPanel);
        splitPane.setDividerLocation(270);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPatients() {
        loadPatientsToComboBox(patientComboBox);
    }

    private void loadPatientsToComboBox(JComboBox<String> comboBox) {
        try {
            String query = "SELECT id, name FROM patients ORDER BY name";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (comboBox != patientComboBox) {
                comboBox.removeAllItems();
                comboBox.addItem("All Patients");
            } else {
                comboBox.removeAllItems();
            }

            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void loadDoctors() {
        try {
            String query = "SELECT id, name, specialization FROM doctors ORDER BY name";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            doctorComboBox.removeAllItems();
            while (rs.next()) {
                doctorComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name") +
                        " (" + rs.getString("specialization") + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    private void saveLabTest() {
        if (patientComboBox.getSelectedItem() == null ||
                doctorComboBox.getSelectedItem() == null ||
                testNameField.getText().trim().isEmpty() ||
                testDateField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        try {
            // Extract patient and doctor IDs
            String patientStr = patientComboBox.getSelectedItem().toString();
            String doctorStr = doctorComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);
            int doctorId = Integer.parseInt(doctorStr.split(" - ")[0]);

            String query = "INSERT INTO lab_tests (patient_id, doctor_id, test_name, test_type, " +
                    "test_date, result_value, normal_range, status, report_file_path, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setString(3, testNameField.getText().trim());
            stmt.setString(4, testTypeComboBox.getSelectedItem().toString());
            stmt.setDate(5, Date.valueOf(testDateField.getText().trim()));
            stmt.setString(6, resultValueField.getText().trim().isEmpty() ? null : resultValueField.getText().trim());
            stmt.setString(7, normalRangeField.getText().trim().isEmpty() ? null : normalRangeField.getText().trim());
            stmt.setString(8, statusComboBox.getSelectedItem().toString());
            stmt.setString(9,
                    reportFilePathField.getText().trim().isEmpty() ? null : reportFilePathField.getText().trim());
            stmt.setString(10, notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Lab test saved successfully!");
            clearForm();
            loadLabTests();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving lab test: " + e.getMessage());
        }
    }

    private void updateTestResult() {
        int selectedRow = testTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a test to update!");
            return;
        }

        int testId = (Integer) tableModel.getValueAt(selectedRow, 0);

        String resultValue = JOptionPane.showInputDialog(this, "Enter Test Result:", resultValueField.getText());
        if (resultValue == null)
            return;

        String status = (String) JOptionPane.showInputDialog(this, "Update Status:", "Status Update",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "Pending", "In Progress", "Completed", "Reviewed", "Cancelled" },
                "Completed");
        if (status == null)
            return;

        try {
            String query = "UPDATE lab_tests SET result_value = ?, status = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, resultValue);
            stmt.setString(2, status);
            stmt.setInt(3, testId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Test result updated successfully!");
            loadLabTests();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating test result: " + e.getMessage());
        }
    }

    private void loadLabTests() {
        try {
            String query = "SELECT lt.id, p.name as patient_name, d.name as doctor_name, " +
                    "lt.test_name, lt.test_type, lt.test_date, lt.status, lt.result_value " +
                    "FROM lab_tests lt " +
                    "JOIN patients p ON lt.patient_id = p.id " +
                    "JOIN doctors d ON lt.doctor_id = d.id " +
                    "ORDER BY lt.test_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("test_name"),
                        rs.getString("test_type"),
                        rs.getDate("test_date"),
                        rs.getString("status"),
                        rs.getString("result_value")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading lab tests: " + e.getMessage());
        }
    }

    private void filterLabTests(JComboBox<String> filterComboBox) {
        if (filterComboBox.getSelectedItem() == null ||
                filterComboBox.getSelectedItem().toString().equals("All Patients")) {
            loadLabTests();
            return;
        }

        try {
            String patientStr = filterComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);

            String query = "SELECT lt.id, p.name as patient_name, d.name as doctor_name, " +
                    "lt.test_name, lt.test_type, lt.test_date, lt.status, lt.result_value " +
                    "FROM lab_tests lt " +
                    "JOIN patients p ON lt.patient_id = p.id " +
                    "JOIN doctors d ON lt.doctor_id = d.id " +
                    "WHERE lt.patient_id = ? " +
                    "ORDER BY lt.test_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("test_name"),
                        rs.getString("test_type"),
                        rs.getDate("test_date"),
                        rs.getString("status"),
                        rs.getString("result_value")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering lab tests: " + e.getMessage());
        }
    }

    private void filterByStatus(JComboBox<String> statusComboBox) {
        if (statusComboBox.getSelectedItem() == null ||
                statusComboBox.getSelectedItem().toString().equals("All Status")) {
            loadLabTests();
            return;
        }

        try {
            String status = statusComboBox.getSelectedItem().toString();

            String query = "SELECT lt.id, p.name as patient_name, d.name as doctor_name, " +
                    "lt.test_name, lt.test_type, lt.test_date, lt.status, lt.result_value " +
                    "FROM lab_tests lt " +
                    "JOIN patients p ON lt.patient_id = p.id " +
                    "JOIN doctors d ON lt.doctor_id = d.id " +
                    "WHERE lt.status = ? " +
                    "ORDER BY lt.test_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("test_name"),
                        rs.getString("test_type"),
                        rs.getDate("test_date"),
                        rs.getString("status"),
                        rs.getString("result_value")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering by status: " + e.getMessage());
        }
    }

    private void showTestDetails() {
        int selectedRow = testTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        int testId = (Integer) tableModel.getValueAt(selectedRow, 0);
        loadTestDetails(testId);
    }

    private void loadTestDetails(int testId) {
        try {
            String query = "SELECT lt.*, p.name as patient_name, p.age, p.gender, " +
                    "d.name as doctor_name, d.specialization " +
                    "FROM lab_tests lt " +
                    "JOIN patients p ON lt.patient_id = p.id " +
                    "JOIN doctors d ON lt.doctor_id = d.id " +
                    "WHERE lt.id = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, testId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder details = new StringBuilder();
            if (rs.next()) {
                details.append("=================== LAB TEST REPORT ===================\n\n");
                details.append("Test ID: ").append(rs.getInt("id")).append("\n");
                details.append("Test Date: ").append(rs.getDate("test_date")).append("\n");
                details.append("Status: ").append(rs.getString("status")).append("\n\n");

                details.append("PATIENT INFORMATION:\n");
                details.append("Name: ").append(rs.getString("patient_name")).append("\n");
                details.append("Age: ").append(rs.getInt("age")).append(" years\n");
                details.append("Gender: ").append(rs.getString("gender")).append("\n\n");

                details.append("DOCTOR INFORMATION:\n");
                details.append("Name: Dr. ").append(rs.getString("doctor_name")).append("\n");
                details.append("Specialization: ").append(rs.getString("specialization")).append("\n\n");

                details.append("TEST INFORMATION:\n");
                details.append("Test Name: ").append(rs.getString("test_name")).append("\n");
                details.append("Test Type: ").append(rs.getString("test_type")).append("\n\n");

                if (rs.getString("result_value") != null) {
                    details.append("RESULT:\n");
                    details.append("Value: ").append(rs.getString("result_value")).append("\n");
                    if (rs.getString("normal_range") != null) {
                        details.append("Normal Range: ").append(rs.getString("normal_range")).append("\n");
                    }
                    details.append("\n");
                }

                if (rs.getString("report_file_path") != null) {
                    details.append("REPORT FILE:\n");
                    details.append(rs.getString("report_file_path")).append("\n\n");
                }

                if (rs.getString("notes") != null && !rs.getString("notes").trim().isEmpty()) {
                    details.append("NOTES:\n");
                    details.append(rs.getString("notes")).append("\n\n");
                }

                details.append("Created: ").append(rs.getTimestamp("created_at"));
                details.append("\n=====================================================");
            }

            testDetailsArea.setText(details.toString());
            testDetailsArea.setCaretPosition(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading test details: " + e.getMessage());
        }
    }

    private void editSelectedTest() {
        int selectedRow = testTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        // Pre-fill form with selected test data for editing
        String testName = tableModel.getValueAt(selectedRow, 3).toString();
        String status = tableModel.getValueAt(selectedRow, 6).toString();
        Object result = tableModel.getValueAt(selectedRow, 7);

        testNameField.setText(testName);
        statusComboBox.setSelectedItem(status);
        if (result != null) {
            resultValueField.setText(result.toString());
        }

        JOptionPane.showMessageDialog(this, "Form filled with selected test data. Modify and click Save to update.");
    }

    private void browseReportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Test Report File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf") ||
                        f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "Report Files (*.pdf, *.jpg, *.png)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            reportFilePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void clearForm() {
        testNameField.setText("");
        testDateField.setText(LocalDate.now().toString());
        resultValueField.setText("");
        normalRangeField.setText("");
        reportFilePathField.setText("");
        notesArea.setText("");
        testTypeComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
    }
}