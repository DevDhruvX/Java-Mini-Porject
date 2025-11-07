package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionManagementWindow extends JFrame {
    private Connection connection;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextArea chiefComplaintArea;
    private JTextArea diagnosisArea;
    private JTextArea notesArea;
    private JTable medicineTable;
    private DefaultTableModel medicineTableModel;
    private JTextField nextVisitField;

    // Medicine form fields
    private JComboBox<String> medicineComboBox;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextField durationField;
    private JTextArea instructionsArea;
    private JSpinner quantitySpinner;

    private List<Medicine> availableMedicines;
    private int currentAppointmentId = -1;

    public PrescriptionManagementWindow(Connection connection) {
        this.connection = connection;
        this.availableMedicines = new ArrayList<>();
        initializeGUI();
        loadMedicines();
        loadPatients();
        loadDoctors();
    }

    public PrescriptionManagementWindow(Connection connection, int appointmentId, int patientId, int doctorId) {
        this.connection = connection;
        this.currentAppointmentId = appointmentId;
        this.availableMedicines = new ArrayList<>();
        initializeGUI();
        loadMedicines();
        loadPatients();
        loadDoctors();
        preselectPatientAndDoctor(patientId, doctorId);
    }

    private void initializeGUI() {
        setTitle("Prescription Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Prescription Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left panel - Prescription form
        JPanel leftPanel = createPrescriptionForm();

        // Right panel - Medicine list
        JPanel rightPanel = createMedicinePanel();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createPrescriptionForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Patient selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientComboBox = new JComboBox<>();
        patientComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(patientComboBox, gbc);

        // Doctor selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1;
        doctorComboBox = new JComboBox<>();
        doctorComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(doctorComboBox, gbc);

        // Chief complaint
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Chief Complaint:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        chiefComplaintArea = new JTextArea(3, 20);
        chiefComplaintArea.setLineWrap(true);
        chiefComplaintArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(chiefComplaintArea), gbc);

        // Diagnosis
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        diagnosisArea = new JTextArea(3, 20);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(diagnosisArea), gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(notesArea), gbc);

        // Next visit date
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Next Visit Date:"), gbc);
        gbc.gridx = 1;
        nextVisitField = new JTextField(15);
        nextVisitField.setToolTipText("YYYY-MM-DD format");
        panel.add(nextVisitField, gbc);

        return panel;
    }

    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Medicines"));

        // Medicine form
        JPanel medicineForm = createMedicineForm();
        panel.add(medicineForm, BorderLayout.NORTH);

        // Medicine table
        String[] columns = { "Medicine", "Dosage", "Frequency", "Duration", "Quantity", "Instructions" };
        medicineTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Medicine buttons
        JPanel medicineButtonPanel = new JPanel(new FlowLayout());
        JButton addMedicineBtn = new JButton("Add Medicine");
        addMedicineBtn.setBackground(new Color(46, 204, 113));
        addMedicineBtn.setForeground(Color.WHITE);
        addMedicineBtn.addActionListener(e -> addMedicine());

        JButton removeMedicineBtn = new JButton("Remove Medicine");
        removeMedicineBtn.setBackground(new Color(231, 76, 60));
        removeMedicineBtn.setForeground(Color.WHITE);
        removeMedicineBtn.addActionListener(e -> removeMedicine());

        medicineButtonPanel.add(addMedicineBtn);
        medicineButtonPanel.add(removeMedicineBtn);
        panel.add(medicineButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMedicineForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add Medicine"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        // Medicine selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Medicine:"), gbc);
        gbc.gridx = 1;
        medicineComboBox = new JComboBox<>();
        medicineComboBox.setPreferredSize(new Dimension(150, 25));
        panel.add(medicineComboBox, gbc);

        // Dosage
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Dosage:"), gbc);
        gbc.gridx = 1;
        dosageField = new JTextField(15);
        dosageField.setToolTipText("e.g., 1 tablet, 5ml");
        panel.add(dosageField, gbc);

        // Frequency
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1;
        frequencyField = new JTextField(15);
        frequencyField.setToolTipText("e.g., Twice daily, Every 8 hours");
        panel.add(frequencyField, gbc);

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        durationField = new JTextField(15);
        durationField.setToolTipText("e.g., 7 days, 2 weeks");
        panel.add(durationField, gbc);

        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panel.add(quantitySpinner, gbc);

        // Instructions
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Instructions:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        instructionsArea = new JTextArea(2, 15);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setToolTipText("e.g., Take after food, Avoid alcohol");
        panel.add(new JScrollPane(instructionsArea), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton savePrescriptionBtn = new JButton("Save Prescription");
        savePrescriptionBtn.setBackground(new Color(52, 152, 219));
        savePrescriptionBtn.setForeground(Color.WHITE);
        savePrescriptionBtn.setFont(new Font("Arial", Font.BOLD, 14));
        savePrescriptionBtn.addActionListener(e -> savePrescription());

        JButton viewPrescriptionsBtn = new JButton("View All Prescriptions");
        viewPrescriptionsBtn.setBackground(new Color(155, 89, 182));
        viewPrescriptionsBtn.setForeground(Color.WHITE);
        viewPrescriptionsBtn.addActionListener(e -> viewAllPrescriptions());

        JButton clearBtn = new JButton("Clear Form");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearForm());

        JButton checkInteractionsBtn = new JButton("Check Drug Interactions");
        checkInteractionsBtn.setBackground(new Color(230, 126, 34));
        checkInteractionsBtn.setForeground(Color.WHITE);
        checkInteractionsBtn.addActionListener(e -> checkDrugInteractions());

        panel.add(savePrescriptionBtn);
        panel.add(viewPrescriptionsBtn);
        panel.add(checkInteractionsBtn);
        panel.add(clearBtn);

        return panel;
    }

    private void loadMedicines() {
        try {
            String query = "SELECT * FROM medicines ORDER BY medicine_name";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (availableMedicines != null) {
                availableMedicines.clear();
            }
            if (medicineComboBox != null) {
                medicineComboBox.removeAllItems();
            }

            while (rs.next()) {
                Medicine medicine = new Medicine(
                        rs.getInt("id"),
                        rs.getString("medicine_name"),
                        rs.getString("generic_name"),
                        rs.getString("strength"),
                        rs.getString("contraindications"),
                        rs.getString("side_effects"));
                
                if (availableMedicines != null) {
                    availableMedicines.add(medicine);
                }
                if (medicineComboBox != null) {
                    medicineComboBox.addItem(medicine.getName() + " (" + medicine.getStrength() + ")");
                }
            }
        } catch (SQLException e) {
            // If medicines table doesn't exist, show a helpful message
            if (e.getMessage().contains("doesn't exist") || e.getMessage().contains("Table") || e.getMessage().contains("Unknown table")) {
                JOptionPane.showMessageDialog(this, 
                    "Medicines table not found. Please run the database migration script first.\n" +
                    "Go to phpMyAdmin and execute the database_migration.sql file.", 
                    "Database Setup Required", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error loading medicines: " + e.getMessage());
            }
        }
    }

    private void loadPatients() {
        try {
            String query = "SELECT id, name FROM patients ORDER BY name";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            patientComboBox.removeAllItems();
            while (rs.next()) {
                patientComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
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

    private void preselectPatientAndDoctor(int patientId, int doctorId) {
        // Preselect patient and doctor if coming from appointment
        for (int i = 0; i < patientComboBox.getItemCount(); i++) {
            String item = patientComboBox.getItemAt(i);
            if (item.startsWith(patientId + " - ")) {
                patientComboBox.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
            String item = doctorComboBox.getItemAt(i);
            if (item.startsWith(doctorId + " - ")) {
                doctorComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void addMedicine() {
        if (medicineComboBox.getSelectedItem() == null ||
                dosageField.getText().trim().isEmpty() ||
                frequencyField.getText().trim().isEmpty() ||
                durationField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all medicine fields!");
            return;
        }

        String medicine = medicineComboBox.getSelectedItem().toString();
        String dosage = dosageField.getText().trim();
        String frequency = frequencyField.getText().trim();
        String duration = durationField.getText().trim();
        int quantity = (Integer) quantitySpinner.getValue();
        String instructions = instructionsArea.getText().trim();

        medicineTableModel.addRow(new Object[] { medicine, dosage, frequency, duration, quantity, instructions });

        // Clear medicine form
        dosageField.setText("");
        frequencyField.setText("");
        durationField.setText("");
        quantitySpinner.setValue(1);
        instructionsArea.setText("");
    }

    private void removeMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow >= 0) {
            medicineTableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a medicine to remove!");
        }
    }

    private void savePrescription() {
        if (patientComboBox.getSelectedItem() == null ||
                doctorComboBox.getSelectedItem() == null ||
                chiefComplaintArea.getText().trim().isEmpty() ||
                diagnosisArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        if (medicineTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one medicine!");
            return;
        }

        try {
            connection.setAutoCommit(false);

            // Extract patient and doctor IDs
            String patientStr = patientComboBox.getSelectedItem().toString();
            String doctorStr = doctorComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);
            int doctorId = Integer.parseInt(doctorStr.split(" - ")[0]);

            // Insert prescription
            String prescriptionQuery = "INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, " +
                    "prescription_date, chief_complaint, diagnosis, notes, next_visit_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement prescriptionStmt = connection.prepareStatement(prescriptionQuery,
                    Statement.RETURN_GENERATED_KEYS);
            // appointment_id may be optional depending on where the prescription is created from
            if (currentAppointmentId > 0) {
                prescriptionStmt.setInt(1, currentAppointmentId);
            } else {
                // explicitly set SQL NULL of INTEGER type when no appointment is associated
                prescriptionStmt.setNull(1, Types.INTEGER);
            }
            prescriptionStmt.setInt(2, patientId);
            prescriptionStmt.setInt(3, doctorId);
            prescriptionStmt.setDate(4, Date.valueOf(LocalDate.now()));
            prescriptionStmt.setString(5, chiefComplaintArea.getText().trim());
            prescriptionStmt.setString(6, diagnosisArea.getText().trim());
            prescriptionStmt.setString(7, notesArea.getText().trim());

            String nextVisitStr = nextVisitField.getText().trim();
            if (!nextVisitStr.isEmpty()) {
                prescriptionStmt.setDate(8, Date.valueOf(nextVisitStr));
            } else {
                prescriptionStmt.setNull(8, Types.DATE);
            }

            prescriptionStmt.executeUpdate();

            ResultSet rs = prescriptionStmt.getGeneratedKeys();
            int prescriptionId = 0;
            if (rs.next()) {
                prescriptionId = rs.getInt(1);
            }

            // Insert medicines
            String medicineQuery = "INSERT INTO prescription_medicines (prescription_id, medicine_name, " +
                    "dosage, frequency, duration, instructions, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement medicineStmt = connection.prepareStatement(medicineQuery);

            for (int i = 0; i < medicineTableModel.getRowCount(); i++) {
                medicineStmt.setInt(1, prescriptionId);
                medicineStmt.setString(2, medicineTableModel.getValueAt(i, 0).toString());
                medicineStmt.setString(3, medicineTableModel.getValueAt(i, 1).toString());
                medicineStmt.setString(4, medicineTableModel.getValueAt(i, 2).toString());
                medicineStmt.setString(5, medicineTableModel.getValueAt(i, 3).toString());
                medicineStmt.setString(6, medicineTableModel.getValueAt(i, 5).toString());
                medicineStmt.setInt(7, Integer.parseInt(medicineTableModel.getValueAt(i, 4).toString()));
                medicineStmt.addBatch();
            }

            medicineStmt.executeBatch();
            connection.commit();

            JOptionPane.showMessageDialog(this, "Prescription saved successfully!");
            clearForm();

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error saving prescription: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDrugInteractions() {
        if (medicineTableModel.getRowCount() < 2) {
            JOptionPane.showMessageDialog(this, "Add at least 2 medicines to check interactions!");
            return;
        }

        List<String> medicines = new ArrayList<>();
        for (int i = 0; i < medicineTableModel.getRowCount(); i++) {
            medicines.add(medicineTableModel.getValueAt(i, 0).toString());
        }

        StringBuilder interactions = new StringBuilder("Drug Interaction Check:\n\n");
        boolean foundInteractions = false;

        try {
            for (int i = 0; i < medicines.size(); i++) {
                for (int j = i + 1; j < medicines.size(); j++) {
                    String medicine1 = medicines.get(i).split(" \\(")[0];
                    String medicine2 = medicines.get(j).split(" \\(")[0];

                    String query = "SELECT di.interaction_level, di.description " +
                            "FROM drug_interactions di " +
                            "JOIN medicines m1 ON di.medicine1_id = m1.id " +
                            "JOIN medicines m2 ON di.medicine2_id = m2.id " +
                            "WHERE (m1.medicine_name = ? AND m2.medicine_name = ?) " +
                            "OR (m1.medicine_name = ? AND m2.medicine_name = ?)";

                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, medicine1);
                    stmt.setString(2, medicine2);
                    stmt.setString(3, medicine2);
                    stmt.setString(4, medicine1);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        foundInteractions = true;
                        interactions.append("WARNING: ").append(medicine1).append(" + ").append(medicine2).append("\n");
                        interactions.append("Level: ").append(rs.getString("interaction_level")).append("\n");
                        interactions.append("Description: ").append(rs.getString("description")).append("\n\n");
                    }
                }
            }

            if (!foundInteractions) {
                interactions.append("✅ No known drug interactions found between the selected medicines.");
            }

            JOptionPane.showMessageDialog(this, interactions.toString(), "Drug Interaction Check",
                    foundInteractions ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking drug interactions: " + e.getMessage());
        }
    }

    private void viewAllPrescriptions() {
        new PrescriptionViewerWindow(connection).setVisible(true);
    }

    private void clearForm() {
        chiefComplaintArea.setText("");
        diagnosisArea.setText("");
        notesArea.setText("");
        nextVisitField.setText("");
        medicineTableModel.setRowCount(0);
        dosageField.setText("");
        frequencyField.setText("");
        durationField.setText("");
        quantitySpinner.setValue(1);
        instructionsArea.setText("");
    }

    // Medicine class
    private static class Medicine {
        private int id;
        private String name;
        private String genericName;
        private String strength;
        private String contraindications;
        private String sideEffects;

        public Medicine(int id, String name, String genericName, String strength,
                String contraindications, String sideEffects) {
            this.id = id;
            this.name = name;
            this.genericName = genericName;
            this.strength = strength;
            this.contraindications = contraindications;
            this.sideEffects = sideEffects;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getGenericName() {
            return genericName;
        }

        public String getStrength() {
            return strength;
        }

        public String getContraindications() {
            return contraindications;
        }

        public String getSideEffects() {
            return sideEffects;
        }
    }
}