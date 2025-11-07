package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class MedicalHistoryWindow extends JFrame {
    private Connection connection;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextField visitDateField;
    private JTextArea diagnosisArea;
    private JTextArea treatmentArea;
    private JTextArea symptomsArea;
    private JTextArea notesArea;
    private JComboBox<String> conditionStatusComboBox;

    // Vital signs fields
    private JTextField temperatureField;
    private JTextField bloodPressureField;
    private JTextField heartRateField;
    private JTextField respiratoryRateField;
    private JTextField weightField;
    private JTextField heightField;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextArea historyDetailsArea;

    public MedicalHistoryWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadPatients();
        loadDoctors();
        loadMedicalHistory();
    }

    private void initializeGUI() {
        setTitle("Medical History Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Medical History Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add Medical History Tab
        JPanel addHistoryPanel = createAddHistoryPanel();
        tabbedPane.addTab("Add Medical Record", addHistoryPanel);

        // View Medical History Tab
        JPanel viewHistoryPanel = createViewHistoryPanel();
        tabbedPane.addTab("View Medical History", viewHistoryPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createAddHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

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

        // Visit date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Visit Date:"), gbc);
        gbc.gridx = 1;
        visitDateField = new JTextField(LocalDate.now().toString(), 15);
        visitDateField.setToolTipText("YYYY-MM-DD format");
        formPanel.add(visitDateField, gbc);

        // Condition status
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Condition Status:"), gbc);
        gbc.gridx = 3;
        conditionStatusComboBox = new JComboBox<>(
                new String[] { "Active", "Resolved", "Chronic", "Under Treatment", "Monitoring" });
        formPanel.add(conditionStatusComboBox, gbc);

        // Vital Signs Panel
        JPanel vitalSignsPanel = createVitalSignsPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(vitalSignsPanel, gbc);

        // Symptoms
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Symptoms:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        symptomsArea = new JTextArea(3, 30);
        symptomsArea.setLineWrap(true);
        symptomsArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(symptomsArea), gbc);

        // Diagnosis
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        diagnosisArea = new JTextArea(3, 30);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(diagnosisArea), gbc);

        // Treatment
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Treatment:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        treatmentArea = new JTextArea(3, 30);
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(treatmentArea), gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(notesArea), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton saveBtn = new JButton("Save Medical Record");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.addActionListener(e -> saveMedicalRecord());

        JButton clearBtn = new JButton("Clear Form");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createVitalSignsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Vital Signs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        // Temperature
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Temperature (°F):"), gbc);
        gbc.gridx = 1;
        temperatureField = new JTextField(8);
        temperatureField.setToolTipText("e.g., 98.6");
        panel.add(temperatureField, gbc);

        // Blood Pressure
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Blood Pressure:"), gbc);
        gbc.gridx = 3;
        bloodPressureField = new JTextField(8);
        bloodPressureField.setToolTipText("e.g., 120/80");
        panel.add(bloodPressureField, gbc);

        // Heart Rate
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Heart Rate (bpm):"), gbc);
        gbc.gridx = 1;
        heartRateField = new JTextField(8);
        heartRateField.setToolTipText("e.g., 72");
        panel.add(heartRateField, gbc);

        // Respiratory Rate
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(new JLabel("Respiratory Rate:"), gbc);
        gbc.gridx = 3;
        respiratoryRateField = new JTextField(8);
        respiratoryRateField.setToolTipText("e.g., 16");
        panel.add(respiratoryRateField, gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        weightField = new JTextField(8);
        weightField.setToolTipText("e.g., 70.5");
        panel.add(weightField, gbc);

        // Height
        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 3;
        heightField = new JTextField(8);
        heightField.setToolTipText("e.g., 175");
        panel.add(heightField, gbc);

        return panel;
    }

    private JPanel createViewHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Patient:"));

        JComboBox<String> filterPatientComboBox = new JComboBox<>();
        filterPatientComboBox.addItem("All Patients");
        loadPatientsToComboBox(filterPatientComboBox);
        filterPatientComboBox.addActionListener(e -> filterMedicalHistory(filterPatientComboBox));
        filterPanel.add(filterPatientComboBox);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadMedicalHistory());
        filterPanel.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        String[] columns = { "ID", "Patient", "Doctor", "Visit Date", "Diagnosis", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setReorderingAllowed(false);

        // Add mouse listener for row selection
        historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    showHistoryDetails();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setPreferredSize(new Dimension(1100, 250));

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Medical Record Details"));
        detailsPanel.setPreferredSize(new Dimension(1100, 200));

        historyDetailsArea = new JTextArea();
        historyDetailsArea.setEditable(false);
        historyDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyDetailsArea.setBackground(new Color(248, 249, 250));
        historyDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(historyDetailsArea);
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

    private void saveMedicalRecord() {
        if (patientComboBox.getSelectedItem() == null ||
                doctorComboBox.getSelectedItem() == null ||
                visitDateField.getText().trim().isEmpty() ||
                diagnosisArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        try {
            // Extract patient and doctor IDs
            String patientStr = patientComboBox.getSelectedItem().toString();
            String doctorStr = doctorComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);
            int doctorId = Integer.parseInt(doctorStr.split(" - ")[0]);

            // Create vital signs JSON
            String vitalSigns = createVitalSignsJSON();

            String query = "INSERT INTO medical_history (patient_id, doctor_id, visit_date, diagnosis, " +
                    "treatment, symptoms, vital_signs, condition_status, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setDate(3, Date.valueOf(visitDateField.getText().trim()));
            stmt.setString(4, diagnosisArea.getText().trim());
            stmt.setString(5, treatmentArea.getText().trim());
            stmt.setString(6, symptomsArea.getText().trim());
            stmt.setString(7, vitalSigns);
            stmt.setString(8, conditionStatusComboBox.getSelectedItem().toString());
            stmt.setString(9, notesArea.getText().trim());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Medical record saved successfully!");
            clearForm();
            loadMedicalHistory();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving medical record: " + e.getMessage());
        }
    }

    private String createVitalSignsJSON() {
        StringBuilder json = new StringBuilder("{");
        boolean hasValues = false;

        if (!temperatureField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"temperature\":\"").append(temperatureField.getText().trim()).append("\"");
            hasValues = true;
        }
        if (!bloodPressureField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"bloodPressure\":\"").append(bloodPressureField.getText().trim()).append("\"");
            hasValues = true;
        }
        if (!heartRateField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"heartRate\":\"").append(heartRateField.getText().trim()).append("\"");
            hasValues = true;
        }
        if (!respiratoryRateField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"respiratoryRate\":\"").append(respiratoryRateField.getText().trim()).append("\"");
            hasValues = true;
        }
        if (!weightField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"weight\":\"").append(weightField.getText().trim()).append("\"");
            hasValues = true;
        }
        if (!heightField.getText().trim().isEmpty()) {
            if (hasValues)
                json.append(",");
            json.append("\"height\":\"").append(heightField.getText().trim()).append("\"");
            hasValues = true;
        }

        json.append("}");
        return hasValues ? json.toString() : null;
    }

    private void loadMedicalHistory() {
        try {
            String query = "SELECT mh.id, p.name as patient_name, d.name as doctor_name, " +
                    "mh.visit_date, mh.diagnosis, mh.condition_status " +
                    "FROM medical_history mh " +
                    "JOIN patients p ON mh.patient_id = p.id " +
                    "JOIN doctors d ON mh.doctor_id = d.id " +
                    "ORDER BY mh.visit_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("visit_date"),
                        rs.getString("diagnosis"),
                        rs.getString("condition_status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading medical history: " + e.getMessage());
        }
    }

    private void filterMedicalHistory(JComboBox<String> filterComboBox) {
        if (filterComboBox.getSelectedItem() == null ||
                filterComboBox.getSelectedItem().toString().equals("All Patients")) {
            loadMedicalHistory();
            return;
        }

        try {
            String patientStr = filterComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);

            String query = "SELECT mh.id, p.name as patient_name, d.name as doctor_name, " +
                    "mh.visit_date, mh.diagnosis, mh.condition_status " +
                    "FROM medical_history mh " +
                    "JOIN patients p ON mh.patient_id = p.id " +
                    "JOIN doctors d ON mh.doctor_id = d.id " +
                    "WHERE mh.patient_id = ? " +
                    "ORDER BY mh.visit_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("visit_date"),
                        rs.getString("diagnosis"),
                        rs.getString("condition_status")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering medical history: " + e.getMessage());
        }
    }

    private void showHistoryDetails() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        int historyId = (Integer) tableModel.getValueAt(selectedRow, 0);
        loadHistoryDetails(historyId);
    }

    private void loadHistoryDetails(int historyId) {
        try {
            String query = "SELECT mh.*, p.name as patient_name, p.age, p.gender, " +
                    "d.name as doctor_name, d.specialization " +
                    "FROM medical_history mh " +
                    "JOIN patients p ON mh.patient_id = p.id " +
                    "JOIN doctors d ON mh.doctor_id = d.id " +
                    "WHERE mh.id = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, historyId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder details = new StringBuilder();
            if (rs.next()) {
                details.append("================== MEDICAL RECORD ==================\n\n");
                details.append("Record ID: ").append(rs.getInt("id")).append("\n");
                details.append("Visit Date: ").append(rs.getDate("visit_date")).append("\n");
                details.append("Status: ").append(rs.getString("condition_status")).append("\n\n");

                details.append("PATIENT INFORMATION:\n");
                details.append("Name: ").append(rs.getString("patient_name")).append("\n");
                details.append("Age: ").append(rs.getInt("age")).append(" years\n");
                details.append("Gender: ").append(rs.getString("gender")).append("\n\n");

                details.append("DOCTOR INFORMATION:\n");
                details.append("Name: Dr. ").append(rs.getString("doctor_name")).append("\n");
                details.append("Specialization: ").append(rs.getString("specialization")).append("\n\n");

                if (rs.getString("symptoms") != null && !rs.getString("symptoms").trim().isEmpty()) {
                    details.append("SYMPTOMS:\n");
                    details.append(rs.getString("symptoms")).append("\n\n");
                }

                details.append("DIAGNOSIS:\n");
                details.append(rs.getString("diagnosis")).append("\n\n");

                if (rs.getString("treatment") != null && !rs.getString("treatment").trim().isEmpty()) {
                    details.append("TREATMENT:\n");
                    details.append(rs.getString("treatment")).append("\n\n");
                }

                // Parse and display vital signs
                String vitalSigns = rs.getString("vital_signs");
                if (vitalSigns != null && !vitalSigns.trim().isEmpty() && !vitalSigns.equals("{}")) {
                    details.append("VITAL SIGNS:\n");
                    details.append("──────────────────────\n");
                    parseVitalSigns(vitalSigns, details);
                    details.append("\n");
                }

                if (rs.getString("notes") != null && !rs.getString("notes").trim().isEmpty()) {
                    details.append("NOTES:\n");
                    details.append(rs.getString("notes")).append("\n\n");
                }

                details.append("Created: ").append(rs.getTimestamp("created_at"));
                details.append("\n====================================================");
            }

            historyDetailsArea.setText(details.toString());
            historyDetailsArea.setCaretPosition(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading history details: " + e.getMessage());
        }
    }

    private void parseVitalSigns(String vitalSigns, StringBuilder details) {
        // Simple JSON parsing for vital signs
        vitalSigns = vitalSigns.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = vitalSigns.split(",");

        for (String pair : pairs) {
            if (pair.trim().isEmpty())
                continue;
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                switch (key) {
                    case "temperature":
                        details.append("Temperature: ").append(value).append("°F\n");
                        break;
                    case "bloodPressure":
                        details.append("Blood Pressure: ").append(value).append("\n");
                        break;
                    case "heartRate":
                        details.append("Heart Rate: ").append(value).append(" bpm\n");
                        break;
                    case "respiratoryRate":
                        details.append("Respiratory Rate: ").append(value).append("\n");
                        break;
                    case "weight":
                        details.append("Weight: ").append(value).append(" kg\n");
                        break;
                    case "height":
                        details.append("Height: ").append(value).append(" cm\n");
                        break;
                }
            }
        }
    }

    private void clearForm() {
        visitDateField.setText(LocalDate.now().toString());
        diagnosisArea.setText("");
        treatmentArea.setText("");
        symptomsArea.setText("");
        notesArea.setText("");
        temperatureField.setText("");
        bloodPressureField.setText("");
        heartRateField.setText("");
        respiratoryRateField.setText("");
        weightField.setText("");
        heightField.setText("");
        conditionStatusComboBox.setSelectedIndex(0);
    }
}