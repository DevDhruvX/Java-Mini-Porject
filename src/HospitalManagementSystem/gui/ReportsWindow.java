package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ReportsWindow extends JFrame {
    private Connection connection;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo;
    private JSpinner fromDateSpinner, toDateSpinner;
    private JButton generateButton, exportButton;

    public ReportsWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Hospital Reports");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(33, 150, 243));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Hospital Management Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.setBackground(new Color(33, 150, 243));

        // Report type selection
        JLabel reportLabel = new JLabel("Report Type:");
        reportLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        reportLabel.setForeground(Color.WHITE);

        reportTypeCombo = new JComboBox<>(new String[] {
                "All Patients", "All Doctors", "All Appointments",
                "Appointments by Date Range", "Patient Appointments",
                "Doctor Schedule", "Monthly Statistics"
        });
        reportTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        // Date range
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fromLabel.setForeground(Color.WHITE);

        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromEditor);
        fromDateSpinner.setPreferredSize(new Dimension(120, 25));

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toLabel.setForeground(Color.WHITE);

        toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        toDateSpinner.setEditor(toEditor);
        toDateSpinner.setPreferredSize(new Dimension(120, 25));

        generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBackground(new Color(76, 175, 80));
        generateButton.setForeground(Color.WHITE);
        generateButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateReport());

        controlsPanel.add(reportLabel);
        controlsPanel.add(reportTypeCombo);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(fromLabel);
        controlsPanel.add(fromDateSpinner);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(toLabel);
        controlsPanel.add(toDateSpinner);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(generateButton);

        panel.add(controlsPanel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTable.setRowHeight(22);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reportTable.getTableHeader().setBackground(new Color(33, 150, 243));
        reportTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        exportButton = new JButton("Export to CSV");
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.setBackground(new Color(255, 152, 0));
        exportButton.setForeground(Color.WHITE);
        exportButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportToCSV());
        exportButton.setEnabled(false);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(158, 158, 158));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        panel.add(exportButton);
        panel.add(closeButton);

        return panel;
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();

        try {
            switch (reportType) {
                case "All Patients":
                    generateAllPatientsReport();
                    break;
                case "All Doctors":
                    generateAllDoctorsReport();
                    break;
                case "All Appointments":
                    generateAllAppointmentsReport();
                    break;
                case "Appointments by Date Range":
                    generateAppointmentsByDateRangeReport();
                    break;
                case "Patient Appointments":
                    generatePatientAppointmentsReport();
                    break;
                case "Doctor Schedule":
                    generateDoctorScheduleReport();
                    break;
                case "Monthly Statistics":
                    generateMonthlyStatisticsReport();
                    break;
            }
            exportButton.setEnabled(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateAllPatientsReport() throws SQLException {
        String[] columns = { "ID", "Name", "Age", "Gender", "Phone", "Address", "Registration Date" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        String query = "SELECT id, name, age, gender, phone, address, created_at FROM patients ORDER BY name";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("id"));
            row.add(rs.getString("name"));
            row.add(rs.getInt("age"));
            row.add(rs.getString("gender"));
            row.add(rs.getString("phone"));
            row.add(rs.getString("address"));
            row.add(rs.getTimestamp("created_at"));
            tableModel.addRow(row);
        }
    }

    private void generateAllDoctorsReport() throws SQLException {
        String[] columns = { "ID", "Name", "Specialization", "Phone", "Email" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        String query = "SELECT id, name, specialization, phone, email FROM doctors ORDER BY name";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("id"));
            row.add(rs.getString("name"));
            row.add(rs.getString("specialization"));
            row.add(rs.getString("phone"));
            row.add(rs.getString("email"));
            tableModel.addRow(row);
        }
    }

    private void generateAllAppointmentsReport() throws SQLException {
        String[] columns = { "ID", "Patient", "Doctor", "Date", "Time", "Status" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        String query = "SELECT a.id, p.name as patient_name, d.name as doctor_name, " +
                "a.appointment_date, a.appointment_time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

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
    }

    private void generateAppointmentsByDateRangeReport() throws SQLException {
        String[] columns = { "ID", "Patient", "Doctor", "Date", "Time", "Status" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();

        String query = "SELECT a.id, p.name as patient_name, d.name as doctor_name, " +
                "a.appointment_date, a.appointment_time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.appointment_date BETWEEN ? AND ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setDate(1, new java.sql.Date(fromDate.getTime()));
        pstmt.setDate(2, new java.sql.Date(toDate.getTime()));

        ResultSet rs = pstmt.executeQuery();

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
    }

    private void generatePatientAppointmentsReport() throws SQLException {
        String[] columns = { "Patient", "Doctor", "Date", "Time", "Status", "Total Appointments" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        String query = "SELECT p.name as patient_name, d.name as doctor_name, " +
                "a.appointment_date, a.appointment_time, a.status, " +
                "COUNT(*) OVER (PARTITION BY p.id) as total_appointments " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY p.name, a.appointment_date DESC";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getString("patient_name"));
            row.add(rs.getString("doctor_name"));
            row.add(rs.getDate("appointment_date"));
            row.add(rs.getTime("appointment_time"));
            row.add(rs.getString("status"));
            row.add(rs.getInt("total_appointments"));
            tableModel.addRow(row);
        }
    }

    private void generateDoctorScheduleReport() throws SQLException {
        String[] columns = { "Doctor", "Specialization", "Patient", "Date", "Time", "Status" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        String query = "SELECT d.name as doctor_name, d.specialization, p.name as patient_name, " +
                "a.appointment_date, a.appointment_time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY d.name, a.appointment_date, a.appointment_time";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getString("doctor_name"));
            row.add(rs.getString("specialization"));
            row.add(rs.getString("patient_name"));
            row.add(rs.getDate("appointment_date"));
            row.add(rs.getTime("appointment_time"));
            row.add(rs.getString("status"));
            tableModel.addRow(row);
        }
    }

    private void generateMonthlyStatisticsReport() throws SQLException {
        String[] columns = { "Metric", "Count" };
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        // Total patients
        String patientQuery = "SELECT COUNT(*) as count FROM patients";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(patientQuery);
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("Total Patients");
            row.add(rs.getInt("count"));
            tableModel.addRow(row);
        }

        // Total doctors
        String doctorQuery = "SELECT COUNT(*) as count FROM doctors";
        rs = stmt.executeQuery(doctorQuery);
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("Total Doctors");
            row.add(rs.getInt("count"));
            tableModel.addRow(row);
        }

        // Total appointments this month
        String appointmentQuery = "SELECT COUNT(*) as count FROM appointments WHERE MONTH(appointment_date) = MONTH(CURDATE()) AND YEAR(appointment_date) = YEAR(CURDATE())";
        rs = stmt.executeQuery(appointmentQuery);
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("Appointments This Month");
            row.add(rs.getInt("count"));
            tableModel.addRow(row);
        }

        // Completed appointments
        String completedQuery = "SELECT COUNT(*) as count FROM appointments WHERE status = 'Completed'";
        rs = stmt.executeQuery(completedQuery);
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("Completed Appointments");
            row.add(rs.getInt("count"));
            tableModel.addRow(row);
        }

        // Cancelled appointments
        String cancelledQuery = "SELECT COUNT(*) as count FROM appointments WHERE status = 'Cancelled'";
        rs = stmt.executeQuery(cancelledQuery);
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add("Cancelled Appointments");
            row.add(rs.getInt("count"));
            tableModel.addRow(row);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        fileChooser.setSelectedFile(new java.io.File("hospital_report_" + timestamp + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1)
                        writer.append(",");
                }
                writer.append("\n");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1)
                            writer.append(",");
                    }
                    writer.append("\n");
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!",
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}