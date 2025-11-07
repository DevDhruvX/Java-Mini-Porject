package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentManagementWindow extends JFrame {
    private Connection connection;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> patientCombo, doctorCombo, statusCombo;
    private JSpinner dateSpinner, timeSpinner;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private int selectedAppointmentId = -1;

    public AppointmentManagementWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadComboBoxData();
        loadAppointmentData();
    }

    private void initializeGUI() {
        setTitle("Appointment Management");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createFormPanel();
        add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(33, 150, 243));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchAppointments();
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(searchLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(searchField);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 10));

        String[] columnNames = { "ID", "Patient", "Doctor", "Date", "Time", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentTable.setRowHeight(25);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = appointmentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });

        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        appointmentTable.getTableHeader().setBackground(new Color(33, 150, 243));
        appointmentTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Appointment Information"),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(350, 0));

        // Patient selection
        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(createFieldPanel("Patient:", patientCombo));
        panel.add(Box.createVerticalStrut(15));

        // Doctor selection
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(createFieldPanel("Doctor:", doctorCombo));
        panel.add(Box.createVerticalStrut(15));

        // Date picker
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(createFieldPanel("Date:", dateSpinner));
        panel.add(Box.createVerticalStrut(15));

        // Time picker
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        timeSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(createFieldPanel("Time:", timeSpinner));
        panel.add(Box.createVerticalStrut(15));

        // Status selection
        statusCombo = new JComboBox<>(new String[] { "Scheduled", "Completed", "Cancelled", "No Show" });
        statusCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(createFieldPanel("Status:", statusCombo));
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldPanel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(80, 25));

        if (field instanceof JComboBox || field instanceof JSpinner) {
            field.setPreferredSize(new Dimension(200, 25));
        }

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addButton = createStyledButton("Book Appointment", new Color(76, 175, 80));
        updateButton = createStyledButton("Update Appointment", new Color(33, 150, 243));
        deleteButton = createStyledButton("Cancel Appointment", new Color(244, 67, 54));
        clearButton = createStyledButton("Clear Form", new Color(158, 158, 158));

        // Add prescription button
        JButton prescriptionButton = createStyledButton("Create Prescription", new Color(156, 39, 176));
        prescriptionButton.addActionListener(e -> openPrescriptionForAppointment());

        addButton.addActionListener(e -> addAppointment());
        updateButton.addActionListener(e -> updateAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(prescriptionButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private void loadComboBoxData() {
        // Load patients
        try {
            String patientQuery = "SELECT id, name FROM patients ORDER BY name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(patientQuery);

            patientCombo.removeAllItems();
            patientCombo.addItem("Select Patient");

            while (rs.next()) {
                patientCombo.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load doctors
        try {
            String doctorQuery = "SELECT id, name, specialization FROM doctors ORDER BY name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(doctorQuery);

            doctorCombo.removeAllItems();
            doctorCombo.addItem("Select Doctor");

            while (rs.next()) {
                doctorCombo.addItem(
                        rs.getInt("id") + " - " + rs.getString("name") + " (" + rs.getString("specialization") + ")");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAppointmentData() {
        try {
            String query = "SELECT a.id, p.name as patient_name, d.name as doctor_name, " +
                    "a.appointment_date, a.appointment_time, a.status, " +
                    "a.patient_id, a.doctor_id " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("patient_name"));
                row.add(rs.getString("doctor_name"));
                row.add(rs.getDate("appointment_date"));
                row.add(rs.getTime("appointment_time"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointment data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAppointments() {
        String searchText = searchField.getText().toLowerCase();

        try {
            String query = "SELECT a.id, p.name as patient_name, d.name as doctor_name, " +
                    "a.appointment_date, a.appointment_time, a.status " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE LOWER(p.name) LIKE ? OR LOWER(d.name) LIKE ? OR LOWER(a.status) LIKE ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time";

            PreparedStatement pstmt = connection.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("patient_name"));
                row.add(rs.getString("doctor_name"));
                row.add(rs.getDate("appointment_date"));
                row.add(rs.getTime("appointment_time"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching appointments: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int selectedRow) {
        selectedAppointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);

        // Find and select patient and doctor in combo boxes
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 2);

        for (int i = 0; i < patientCombo.getItemCount(); i++) {
            if (patientCombo.getItemAt(i).contains(patientName)) {
                patientCombo.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).contains(doctorName)) {
                doctorCombo.setSelectedIndex(i);
                break;
            }
        }

        // Set date and time
        Date appointmentDate = (Date) tableModel.getValueAt(selectedRow, 3);
        Time appointmentTime = (Time) tableModel.getValueAt(selectedRow, 4);

        dateSpinner.setValue(appointmentDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(appointmentTime);
        timeSpinner.setValue(cal.getTime());

        // Set status
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        statusCombo.setSelectedItem(status);
    }

    private void addAppointment() {
        if (!validateForm())
            return;

        try {
            // Check if doctor is available at this time
            if (!checkDoctorAvailability()) {
                JOptionPane.showMessageDialog(this, "Doctor is not available at this date and time!",
                        "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);

            pstmt.setInt(1, getSelectedPatientId());
            pstmt.setInt(2, getSelectedDoctorId());
            pstmt.setDate(3, new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
            pstmt.setTime(4, new java.sql.Time(((Date) timeSpinner.getValue()).getTime()));
            pstmt.setString(5, (String) statusCombo.getSelectedItem());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error booking appointment: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        if (selectedAppointmentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm())
            return;

        try {
            String query = "UPDATE appointments SET patient_id=?, doctor_id=?, appointment_date=?, appointment_time=?, status=? WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(query);

            pstmt.setInt(1, getSelectedPatientId());
            pstmt.setInt(2, getSelectedDoctorId());
            pstmt.setDate(3, new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
            pstmt.setTime(4, new java.sql.Time(((Date) timeSpinner.getValue()).getTime()));
            pstmt.setString(5, (String) statusCombo.getSelectedItem());
            pstmt.setInt(6, selectedAppointmentId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Appointment updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment() {
        if (selectedAppointmentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM appointments WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, selectedAppointmentId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAppointmentData();
                    clearForm();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error cancelling appointment: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean checkDoctorAvailability() {
        try {
            String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=? AND status != 'Cancelled'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, getSelectedDoctorId());
            pstmt.setDate(2, new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
            pstmt.setTime(3, new java.sql.Time(((Date) timeSpinner.getValue()).getTime()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getSelectedPatientId() {
        String selected = (String) patientCombo.getSelectedItem();
        if (selected != null && !selected.equals("Select Patient")) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }

    private int getSelectedDoctorId() {
        String selected = (String) doctorCombo.getSelectedItem();
        if (selected != null && !selected.equals("Select Doctor")) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }

    private void clearForm() {
        patientCombo.setSelectedIndex(0);
        doctorCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        timeSpinner.setValue(cal.getTime());

        statusCombo.setSelectedIndex(0);
        selectedAppointmentId = -1;
        appointmentTable.clearSelection();
    }

    private boolean validateForm() {
        if (patientCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (doctorCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void openPrescriptionForAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to create prescription!");
            return;
        }

        try {
            // Get appointment details
            int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String patientInfo = tableModel.getValueAt(selectedRow, 1).toString();
            String doctorInfo = tableModel.getValueAt(selectedRow, 2).toString();

            // Extract patient and doctor IDs
            String patientQuery = "SELECT patient_id, doctor_id FROM appointments WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(patientQuery);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");

                // Open prescription window with pre-selected patient and doctor
                new PrescriptionManagementWindow(connection, appointmentId, patientId, doctorId).setVisible(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error opening prescription: " + e.getMessage());
        }
    }
}